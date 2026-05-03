import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/models/apartment.dart';
import 'package:frontend/shared/data/models/create_apartment_response.dart';
import 'package:frontend/shared/data/models/profile.dart';
import 'package:frontend/shared/data/network/dio_client.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';

final apartmentProvider = AsyncNotifierProvider<_ApartmentNotifier, Apartment?>(
  _ApartmentNotifier.new,
);

class _ApartmentNotifier extends AsyncNotifier<Apartment?> {
  late final String baseUrl;

  late final int apartmentId;

  @override
  Future<Apartment?> build() async {
    baseUrl = "/apartments";

    final user = ref.watch(authProvider).value?.user;

    if (user == null) {
      throw Exception("User is not authorized.");
    }
    if (user.profile == null) {
      return null;
    }

    apartmentId = user.profile!.apartmentId;

    final response = await AppDio.dio.get("$baseUrl/$apartmentId");
    Apartment apartment = Apartment.fromJson(response.data);

    return apartment;
  }

  Future<Profile?> create({required String name, String? address}) async {
    try {
      state = const AsyncLoading();

      final response = await AppDio.dio.post(
        baseUrl,
        data: {
          "name": name,
          "address": address?.isEmpty == true ? null : address,
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

      final apartment = Apartment(
        address: address,
        budget: createApartmentResponse.budget,
        currentExpenseSum: 0,
        id: createApartmentResponse.id,
        name: name,
      );

      state = await AsyncValue.guard(() async {
        return apartment;
      });

      return createApartmentResponse.profile;
    } catch (e) {
      print("Error during creating apartment");
      print(e);
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
}
