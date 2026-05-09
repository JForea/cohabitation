import 'dart:async';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/network/dio_client.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';

final unreadNotificationsCountProvider =
    AsyncNotifierProvider<_UnreadNotificationsCountNotifier, int>(
      _UnreadNotificationsCountNotifier.new,
    );

class _UnreadNotificationsCountNotifier extends AsyncNotifier<int> {
  late String _url;

  @override
  FutureOr<int> build() async {
    final apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));

    if (apartmentId == null) {
      throw Exception("Not in apartment.");
    }

    _url = "/apartments/$apartmentId/notifications/unread-count";

    final response = await AppDio.dio.get(_url);

    return response.data as int;
  }

  Future<void> refresh() async {
    state = const AsyncLoading();

    state = await AsyncValue.guard(() async {
      final response = await AppDio.dio.get(_url);

      return response.data as int;
    });
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
