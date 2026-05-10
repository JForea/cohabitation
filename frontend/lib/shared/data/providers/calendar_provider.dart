import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/network/dio_client.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:intl/intl.dart';

final calendarProvider =
    AsyncNotifierProvider<_CalendarNotifier, Set<DateTime>>(
      _CalendarNotifier.new,
    );

class _CalendarNotifier extends AsyncNotifier<Set<DateTime>> {
  late String url;
  late DateTime currentMonth;

  @override
  Future<Set<DateTime>> build() async {
    final apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));

    if (apartmentId == null) {
      throw Exception("Not in apartment.");
    }

    url = "/apartments/$apartmentId/events/calendar";

    currentMonth = DateTime.now();

    return loadMonth(DateTime.now());
  }

  Future<Set<DateTime>> loadMonth(DateTime month) async {
    final query = {
      "year": month.year,
      "month": DateFormat.MMMM("en_US").format(month).toUpperCase(),
    };

    final response = await AppDio.dio.get(url, queryParameters: query);

    return (response.data as List).map((date) => DateTime.parse(date)).toSet();
  }

  Future<void> changeMonth(DateTime month) async {
    currentMonth = month;

    state = await AsyncValue.guard(() => loadMonth(month));
  }

  Future<void> refresh() async {
    state = const AsyncLoading();

    state = await AsyncValue.guard(() => loadMonth(currentMonth));
  }
}
