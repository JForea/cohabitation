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
      return [];
    }

    try {
      final query = {"page": _page, "size": _pageSize};

      final response = await AppDio.dio.get(_baseUrl, queryParameters: query);

      _isLoading = true;

      final notifications = (response.data as List)
          .map((n) => NotificationValue.fromJson(n))
          .toList();

      if (notifications.length < _pageSize) {
        _hasMore = false;
      }
      _page++;
      _isLoading = false;

      return notifications;
    } catch (e) {
      _isLoading = false;
      _hasMore = false;
      return [];
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
