import 'dart:async';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/models/buying.dart';
import 'package:frontend/shared/data/network/dio_client.dart';
import 'package:frontend/shared/data/types/buying_category.dart';

final buyingsProvider =
    AsyncNotifierProvider.family<
      _BuyingNotifier,
      Map<BuyingCategory, List<Buying>>,
      int
    >(_BuyingNotifier.new);

class _BuyingNotifier extends AsyncNotifier<Map<BuyingCategory, List<Buying>>> {
  _BuyingNotifier(this.apartmentId);
  final int apartmentId;
  late final String baseUrl;

  static const _pageSize = 30;

  int _page = 0;
  bool _hasMore = true;
  bool _isLoading = false;

  @override
  Future<Map<BuyingCategory, List<Buying>>> build() async {
    _page = 0;
    _hasMore = true;
    baseUrl = "/$apartmentId/buyings";

    final buyings = await _fetchPage();

    final map = <BuyingCategory, List<Buying>>{};

    _addToMap(map, buyings);

    return map;
  }

  Future<List<Buying>> _fetchPage() async {
    final query = {'page': '$_page', 'size': '$_pageSize', 'isPublic': 'true'};

    final response = await AppDio.dio.get(baseUrl, queryParameters: query);

    final data = response.data as List;

    return data.map((buyingJson) => Buying.fromJson(buyingJson)).toList();
  }

  void _addToMap(Map<BuyingCategory, List<Buying>> map, List<Buying> buyings) {
    for (var b in buyings) {
      (map[b.category] ??= []).add(b);
    }
  }

  Future<void> loadMore() async {
    if (_isLoading || !_hasMore || state.isLoading) return;

    _isLoading = true;

    try {
      _page++;
      final newBuyings = await _fetchPage();

      if (newBuyings.length < _pageSize) {
        _hasMore = false;
      }

      final mapValue = state.value ?? <BuyingCategory, List<Buying>>{};

      _addToMap(mapValue, newBuyings);

      state = AsyncData(mapValue);
    } catch (e, st) {
      _page--;
      state = AsyncError<Map<BuyingCategory, List<Buying>>>(e, st);
    } finally {
      _isLoading = false;
    }
  }

  Future<void> refresh() async {
    _page = 0;
    _hasMore = true;

    state = const AsyncLoading();
    state = await AsyncValue.guard(() async {
      final map = <BuyingCategory, List<Buying>>{};

      final buyings = await _fetchPage();

      _addToMap(map, buyings);

      return map;
    });
  }
}
