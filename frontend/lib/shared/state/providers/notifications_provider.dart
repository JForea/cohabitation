import 'dart:async';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/core/failures/failures.dart';
import 'package:frontend/shared/data/models/notification_value.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/repositories/notification_repository.dart';

final notificationsProvider =
    AsyncNotifierProvider<_NotificationsNotifier, List<NotificationValue>>(
      _NotificationsNotifier.new,
    );

class _NotificationsNotifier extends AsyncNotifier<List<NotificationValue>> {
  late NotificationRepository _notificationRepository;

  late int? _apartmentId;

  // late bool _isPersonal;

  static const _pageSize = 20;

  late int _page;
  late bool _hasMore;
  late bool _isLoading;

  @override
  FutureOr<List<NotificationValue>> build() async {
    _notificationRepository = ref.read(notificationRepositoryProvider);
    _apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));

    if (_apartmentId == null) throw NotInApartmentFailure();

    // _isPersonal = true;
    _page = 0;
    _hasMore = true;
    _isLoading = false;

    return await _notificationRepository.getPage(
      apartmentId: _apartmentId!,
      page: _page,
      pageSize: _pageSize,
    );
  }

  Future<void> loadMore() async {
    if (_isLoading || !_hasMore) return;

    if (_apartmentId == null) throw NotInApartmentFailure();

    _isLoading = true;

    final previous = state.value;
    if (previous == null) return;

    try {
      final notifications = await _notificationRepository.getPage(
        apartmentId: _apartmentId!,
        page: _page + 1,
        pageSize: _pageSize,
      );

      _page++;

      if (notifications.length < _pageSize) {
        _hasMore = false;
      }

      state = AsyncData([...previous, ...notifications]);
    } finally {
      _isLoading = false;
    }
  }

  Future<void> markAsRead(int notificationId) async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    final current = state.value ?? [];

    final index = current.indexWhere((n) => n.id == notificationId);

    if (index == -1 || current[index].isRead) return;

    final updated = [...current];

    updated[index] = updated[index].copyWith(isRead: true);

    state = AsyncData(updated);

    try {
      await _notificationRepository.markAsRead(_apartmentId!, notificationId);
    } catch (e) {
      state = AsyncData(current);

      rethrow;
    }
  }

  Future<void> markAsReadAll() async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    final current = state.value ?? [];

    final updated = current.map((n) {
      if (!n.isRead) {
        return n.copyWith(isRead: true);
      }
      return n;
    }).toList();

    state = AsyncData(updated);

    try {
      await _notificationRepository.markAsReadAll(_apartmentId!);
    } catch (e) {
      state = AsyncData(current);

      rethrow;
    }
  }

  Future<void> refresh() async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    if (_isLoading) return;

    _isLoading = true;

    try {
      final notifications = await _notificationRepository.getPage(
        apartmentId: _apartmentId!,
        page: 0,
        pageSize: _pageSize,
      );

      _page = 0;
      _hasMore = notifications.length >= _pageSize;

      state = AsyncData(notifications);
    } finally {
      _isLoading = false;
    }
  }
}
