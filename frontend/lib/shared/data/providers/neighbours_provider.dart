import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/data/network/dio_client.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/types/role.dart';

final neighboursProvider =
    AsyncNotifierProvider<_ApartmentNotifier, List<Profile>>(
      _ApartmentNotifier.new,
    );

class _ApartmentNotifier extends AsyncNotifier<List<Profile>> {
  late String _baseUrl;

  @override
  Future<List<Profile>> build() async {
    final apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));

    if (apartmentId == null) {
      throw Exception("Not in apartment.");
    }

    _baseUrl = "/apartments/$apartmentId/profiles";

    final query = {"excludeMe": 'true'};

    final response = await AppDio.dio.get(_baseUrl, queryParameters: query);
    List<Profile> profiles = (response.data as List)
        .map((json) => Profile.fromJson(json))
        .toList();

    return profiles;
  }

  Future<void> refresh() async {
    final query = {"excludeMe": 'true'};

    state = await AsyncValue.guard(() async {
      final response = await AppDio.dio.get(_baseUrl, queryParameters: query);

      return (response.data as List)
          .map((json) => Profile.fromJson(json))
          .toList();
    });
  }

  Future<bool> kick(int profileId) async {
    final previousValue = state.value ?? [];

    try {
      final newValue = [...previousValue];
      newValue.removeWhere((p) => p.id == profileId);
      state = AsyncData(newValue);
      await AppDio.dio.post("$_baseUrl/$profileId/kick");
      return true;
    } catch (e) {
      state = AsyncData(previousValue);
      return false;
    }
  }

  Future<bool> setRole(int profileId, Role role) async {
    final previousValue = state.value ?? [];

    try {
      final newValue = [...previousValue];
      for (int i = 0; i < newValue.length; i++) {
        if (newValue[i].id == profileId) {
          newValue[i] = newValue[i].copyWith(role: role);
        }
      }
      state = AsyncData(newValue);
      await AppDio.dio.patch(
        "$_baseUrl/$profileId",
        queryParameters: {"role": role.name.toUpperCase()},
      );
      return true;
    } catch (e) {
      state = AsyncData(previousValue);
      return false;
    }
  }
}
