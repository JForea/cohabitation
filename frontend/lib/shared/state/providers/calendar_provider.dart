import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/core/failures/failures.dart';
import 'package:frontend/shared/state/providers/apartment_provider.dart';
import 'package:frontend/shared/data/repositories/event_repository.dart';

final calendarProvider = AsyncNotifierProvider<CalendarNotifier, Set<DateTime>>(
  CalendarNotifier.new,
);

class CalendarNotifier extends AsyncNotifier<Set<DateTime>> {
  late EventRepository _eventRepository;

  late DateTime _currentMonth;

  late int? _apartmentId;

  @override
  Future<Set<DateTime>> build() async {
    _eventRepository = ref.read(eventRepositoryProvider);
    _apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));

    if (_apartmentId == null) throw NotInApartmentFailure();

    _currentMonth = DateTime.now();

    return _eventRepository.loadMonth(_apartmentId!, _currentMonth);
  }

  Future<void> changeMonth(DateTime month) async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    final previousMonth = _currentMonth;
    final previous = state;

    try {
      _currentMonth = month;

      Set<DateTime> dates = await _eventRepository.loadMonth(
        _apartmentId!,
        _currentMonth,
      );

      state = AsyncData(dates);
    } catch (e) {
      _currentMonth = previousMonth;
      state = previous;

      rethrow;
    }
  }

  Future<void> refresh() async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    Set<DateTime> dates = await _eventRepository.loadMonth(
      _apartmentId!,
      _currentMonth,
    );

    state = AsyncData(dates);
  }
}
