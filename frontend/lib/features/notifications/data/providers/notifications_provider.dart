import 'dart:async';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/notifications/data/models/notification_value.dart';
import 'package:frontend/shared/data/network/dio_client.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';

final notificationsProvider =
    AsyncNotifierProvider<_NotificationsNotifier, List<NotificationValue>>(
      _NotificationsNotifier.new,
    );

class _NotificationsNotifier extends AsyncNotifier<List<NotificationValue>> {
  late bool _isPersonal;

  late String _baseUrl;

  static const _pageSize = 20;

  late int _page;
  late bool _hasMore;
  late bool _isLoading;

  @override
  FutureOr<List<NotificationValue>> build() async {
    final apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));

    if (apartmentId == null) {
      throw Exception("Not in apartment.");
    }

    _baseUrl = "/apartments/$apartmentId/notifications";
    _isPersonal = false;
    _page = 0;
    _hasMore = true;
    _isLoading = false;

    return await _loadPage();
  }

  Future<List<NotificationValue>> _loadPage() async {
    if (_isLoading || !_hasMore) {
      return state.value ?? [];
    }

    _isLoading = true;

    try {
      final query = {"page": _page, "size": _pageSize};

      final response = await AppDio.dio.get(_baseUrl, queryParameters: query);

      final notifications = (response.data as List)
          .map((n) => NotificationValue.fromJson(n))
          .toList();

      if (notifications.length < _pageSize) {
        _hasMore = false;
      }

      _page++;

      return notifications;
    } finally {
      _isLoading = false;
    }
  }

  Future<bool> markAsRead(int notificationId) async {
    final current = state.value ?? [];

    final index = current.indexWhere((n) => n.id == notificationId);

    if (index == -1 || current[index].isRead) return false;

    final updated = [...current];

    updated[index] = updated[index].copyWith(isRead: true);

    state = AsyncValue.data(updated);

    try {
      await AppDio.dio.patch("$_baseUrl/$notificationId");
      return true;
    } catch (_) {
      state = AsyncValue.data(current);
      return false;
    }
  }

  Future<bool> markAsReadAll() async {
    final current = state.value ?? [];

    final updated = current.map((n) {
      if (!n.isRead) {
        return n.copyWith(isRead: true);
      }
      return n;
    }).toList();

    state = AsyncValue.data(updated);

    try {
      await AppDio.dio.patch(_baseUrl);
      return true;
    } catch (_) {
      state = AsyncValue.data(current);
      return false;
    }
  }

  Future<void> refresh() async {
    if (_isLoading) {
      return;
    }

    _page = 0;
    _hasMore = true;

    state = await AsyncValue.guard(() async => await _loadPage());
  }
}
