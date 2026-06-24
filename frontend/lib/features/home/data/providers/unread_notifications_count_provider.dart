import 'dart:async';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/core/failures/failures.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/repositories/notification_repository.dart';

final unreadNotificationsCountProvider =
    AsyncNotifierProvider<_UnreadNotificationsCountNotifier, int>(
      _UnreadNotificationsCountNotifier.new,
    );

class _UnreadNotificationsCountNotifier extends AsyncNotifier<int> {
  late NotificationRepository _notificationRepository;

  late int? _apartmentId;

  @override
  FutureOr<int> build() async {
    _notificationRepository = ref.read(notificationRepositoryProvider);
    _apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));

    if (_apartmentId == null) throw NotInApartmentFailure();

    return _notificationRepository.getUnreadCount(_apartmentId!);
  }

  Future<void> refresh() async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    final previous = state.value ?? 0;

    try {
      state = AsyncData(
        await _notificationRepository.getUnreadCount(_apartmentId!),
      );
    } catch (e) {
      state = AsyncData(previous);
    }
  }

  void decrement() {
    final current = state.value ?? 0;

    if (current > 0) {
      state = AsyncData(current - 1);
    }
  }

  void clear() {
    state = AsyncData(0);
  }

  void set(int cnt) {
    state = AsyncData(cnt);
  }
}
