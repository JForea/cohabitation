import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/models/apartment/apartment.dart';
import 'package:frontend/shared/data/models/apartment/create_apartment_response.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/data/network/dio_client.dart';
import 'package:frontend/shared/data/providers/user_provider.dart';

final asyncApartmentProvider =
    AsyncNotifierProvider<_ApartmentNotifier, Apartment?>(
      _ApartmentNotifier.new,
    );

class _ApartmentNotifier extends AsyncNotifier<Apartment?> {
  late String baseUrl;

  late int? apartmentId;

  @override
  Future<Apartment?> build() async {
    baseUrl = "/apartments";

    apartmentId = ref.watch(
      userProvider.select((s) => s?.profile?.apartmentId),
    );

    if (apartmentId == null) {
      return null;
    }

    final response = await AppDio.dio.get("$baseUrl/$apartmentId");
    Apartment apartment = Apartment.fromJson(response.data);

    return apartment;
  }

  Future<Profile?> create({required String name, String? address}) async {
    try {
      state = const AsyncLoading();

      final minutesOffset = DateTime.now().timeZoneOffset.inMinutes;

      final response = await AppDio.dio.post(
        baseUrl,
        data: {
          "name": name,
          "address": address?.isEmpty == true ? null : address,
          "minutesOffset": minutesOffset,
        },
      );

      final token = response.headers['Authorization'];

      if (token == null) {
        return null;
      }

      await AppDio.updateToken(token.first);

      final createApartmentResponse = CreateApartmentResponse.fromJson(
        response.data,
      );

      state = await AsyncValue.guard(() async {
        return Apartment(
          address: address,
          budget: createApartmentResponse.budget,
          id: createApartmentResponse.id,
          name: name,
        );
      });

      return createApartmentResponse.profile;
    } catch (e) {
      return null;
    }
  }

  Future<Profile?> join(String inviteCode) async {
    try {
      final query = {'code': inviteCode};

      final response = await AppDio.dio.post(
        "$baseUrl/join",
        queryParameters: query,
      );

      final token = response.headers['Authorization'];

      if (token == null) {
        return null;
      }

      await AppDio.updateToken(token.first);

      state = await AsyncValue.guard(() async {
        return Apartment.fromJson(response.data);
      });

      final profile = Profile.fromJson(response.data["profile"]);

      return profile;
    } catch (e) {
      return null;
    }
  }

  Future<void> generateCode() async {
    final response = await AppDio.dio.patch("$baseUrl/$apartmentId/code");
    Apartment? apartment = state.value;

    if (apartment == null) {
      return;
    }

    apartment = apartment.copyWith(inviteCode: response.data["inviteCode"]);

    state = AsyncValue.data(apartment);
  }

  Future<void> setBudget(int budget) async {
    final currentValue = state.value;

    if (currentValue == null) return;

    state = AsyncData(currentValue.copyWith(budget: budget));

    try {
      await AppDio.dio.patch("$baseUrl/$apartmentId/budget", data: budget);
    } catch (_) {
      state = AsyncData(currentValue);
    }
  }
}
