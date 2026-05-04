import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/models/profile.dart';
import 'package:frontend/shared/data/network/dio_client.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';

final neighboursProvider =
    AsyncNotifierProvider<_ApartmentNotifier, List<Profile>>(
      _ApartmentNotifier.new,
    );

class _ApartmentNotifier extends AsyncNotifier<List<Profile>> {
  late final String baseUrl;

  @override
  Future<List<Profile>> build() async {
    final apartment = ref.watch(apartmentProvider).value;

    if (apartment == null) {
      throw Exception("Not in apartment.");
    }

    baseUrl = "/apartments/${apartment.id}/profiles";

    final query = {"excludeMe": 'true'};

    final response = await AppDio.dio.get(baseUrl, queryParameters: query);
    List<Profile> profiles = (response.data as List)
        .map((json) => Profile.fromJson(json))
        .toList();

    return profiles;
  }

  Future<void> refresh() async {
    final query = {"excludeMe": 'true'};

    state = await AsyncValue.guard(() async {
      final response = await AppDio.dio.get(baseUrl, queryParameters: query);

      return (response.data as List)
          .map((json) => Profile.fromJson(json))
          .toList();
    });
  }
}
