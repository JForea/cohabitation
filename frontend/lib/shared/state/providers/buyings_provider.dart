import 'dart:async';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/filters/buying_filter.dart';
import 'package:frontend/features/buyings/data/models/buying_redacted.dart';
import 'package:frontend/core/failures/failures.dart';
import 'package:frontend/shared/domain/models/buying.dart';
import 'package:frontend/shared/domain/models/profile/profile.dart';
import 'package:frontend/shared/state/providers/apartment_provider.dart';
import 'package:frontend/shared/data/repositories/buying_repository.dart';
import 'package:frontend/shared/domain/types/buying_category.dart';
import 'package:frontend/shared/domain/types/role.dart';

final buyingsProvider =
    AsyncNotifierProvider<_BuyingNotifier, Map<BuyingCategory, List<Buying>>>(
      _BuyingNotifier.new,
    );

class _BuyingNotifier extends AsyncNotifier<Map<BuyingCategory, List<Buying>>> {
  late BuyingRepository _buyingsRepository;

  static const _pageSize = 30;
  late int _page;
  late bool _hasMore;
  late bool _isLoading;

  late int? _apartmentId;

  late BuyingFilter _filter = BuyingFilter();

  @override
  Future<Map<BuyingCategory, List<Buying>>> build() async {
    _buyingsRepository = ref.read(buyingRepositoryProvider);
    _apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));

    if (_apartmentId == null) {
      throw NotInApartmentFailure();
    }

    _page = 0;
    _hasMore = true;
    _isLoading = false;

    final buyings = await _buyingsRepository.getPage(
      apartmentId: _apartmentId!,
      page: _page,
      pageSize: _pageSize,
      assignedTo: _filter.assignedTo,
      isPublic: _filter.isPublic,
    );

    final map = <BuyingCategory, List<Buying>>{};
    _addToMap(map, buyings);

    return map;
  }

  void _addToMap(Map<BuyingCategory, List<Buying>> map, List<Buying> buyings) {
    for (var b in buyings) {
      (map[b.category] ??= []).add(b);
    }
  }

  Future<void> create({
    required Profile userProfile,
    required BuyingRedacted buyingRedacted,
    Profile? assignedTo,
    required bool isPublic,
  }) async {
    if (_isLoading) return;

    if (_apartmentId == null) throw NotInApartmentFailure();

    try {
      _isLoading = true;

      final buying = await _buyingsRepository.save(
        apartmentId: _apartmentId!,
        createdBy: userProfile,
        assignedTo: assignedTo,
        buyingRedacted: buyingRedacted,
        isPublic: isPublic,
      );

      final current = {...?state.value};

      _addToMap(current, [buying]);

      state = AsyncData(current);

      return;
    } finally {
      _isLoading = false;
    }
  }

  Future<void> createMany({
    required Profile userProfile,
    required List<BuyingRedacted> buyingsRedacted,
    Profile? assignedTo,
    required bool isPublic,
  }) async {
    if (_isLoading) return;

    if (_apartmentId == null) throw NotInApartmentFailure();

    try {
      _isLoading = true;

      List<Buying> buyings = await _buyingsRepository.createMany(
        apartmentId: _apartmentId!,
        createdBy: userProfile,
        assignedTo: assignedTo,
        buyingsRedacted: buyingsRedacted,
        isPublic: isPublic,
      );

      final current = {...?state.value};

      _addToMap(current, buyings);

      state = AsyncData(current);
    } finally {
      _isLoading = false;
    }
  }

  Future<void> switchBuyingStatus(int buyingId, Profile userProfile) async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    final previous = state.value ?? {};

    try {
      bool ok = true;

      final updated = <BuyingCategory, List<Buying>>{};

      final keys = previous.keys.toList();
      for (int i = 0; i < keys.length; i++) {
        final key = keys[i];
        final values = previous[key];

        for (int j = 0; j < (values?.length ?? 0); j++) {
          Buying b = values![j];

          if (b.id == buyingId) {
            if (b.completedBy != null) {
              if (b.completedBy!.id == userProfile.id ||
                  userProfile.role != Role.inhabitant) {
                b = b.copyWith(clearCompletedBy: true);
              } else {
                ok = false;
              }
            } else if (b.completedBy == null) {
              b = b.copyWith(completedBy: userProfile);
            }
          }

          updated[key] ??= [];
          updated[key]!.add(b);
        }
      }

      if (!ok) {
        return;
      }

      state = AsyncData(updated);

      _isLoading = true;

      await _buyingsRepository.switchBuyingStatus(
        apartmentId: _apartmentId!,
        buyingId: buyingId,
        userProfile: userProfile,
      );
    } catch (e) {
      state = AsyncData(previous);
      rethrow;
    } finally {
      _isLoading = false;
    }
  }

  Future<void> loadMore() async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    if (_isLoading || !_hasMore) return;

    try {
      _isLoading = true;

      final buyings = await _buyingsRepository.getPage(
        apartmentId: _apartmentId!,
        page: _page + 1,
        pageSize: _pageSize,
        assignedTo: _filter.assignedTo,
        isPublic: _filter.isPublic,
      );

      _page++;

      if (buyings.length < _pageSize) {
        _hasMore = false;
      }

      final current = {...?state.value};

      _addToMap(current, buyings);

      state = AsyncData(current);
    } finally {
      _isLoading = false;
    }
  }

  Future<void> refresh({bool? fullRefresh}) async {
    if (_apartmentId == null) throw NotInApartmentFailure();
    if (_isLoading) return;

    final previous = state;
    final previousPage = _page;
    final previousHasMore = _hasMore;

    try {
      _isLoading = true;

      if (fullRefresh == true) {
        state = const AsyncLoading();
      }

      final current = <BuyingCategory, List<Buying>>{};

      final buyings = await _buyingsRepository.getPage(
        apartmentId: _apartmentId!,
        page: 0,
        pageSize: _pageSize,
        assignedTo: _filter.assignedTo,
        isPublic: _filter.isPublic,
      );

      _page = 0;
      _hasMore = buyings.length >= _pageSize;

      _addToMap(current, buyings);
      state = AsyncData(current);
    } catch (e) {
      _page = previousPage;
      _hasMore = previousHasMore;
      state = previous;
      rethrow;
    } finally {
      _isLoading = false;
    }
  }

  Future<void> deleteMany(List<int> ids) async {
    if (_apartmentId == null) {
      throw NotInApartmentFailure();
    }

    final previous = state.value;

    if (previous == null) return;

    final current = <BuyingCategory, List<Buying>>{};

    for (final entry in previous.entries) {
      current[entry.key] = entry.value
          .where((b) => !ids.contains(b.id))
          .toList();
    }

    state = AsyncData(current);

    try {
      await _buyingsRepository.deleteMany(_apartmentId!, ids);
    } catch (e) {
      state = AsyncData(previous);

      rethrow;
    }
  }

  Future<void> setFilter(BuyingFilter filter) async {
    _filter = filter;
    await refresh(fullRefresh: true);
  }
}
