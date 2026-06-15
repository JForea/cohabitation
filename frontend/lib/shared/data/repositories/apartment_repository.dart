import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/dtos/join_apartment_response.dart';
import 'package:frontend/shared/data/failures/failures.dart';
import 'package:frontend/shared/data/failures/map_dio_exception.dart';
import 'package:frontend/shared/data/models/apartment.dart';
import 'package:frontend/shared/data/dtos/create_apartment_response.dart';
import 'package:frontend/shared/data/network/api_client.dart';
import 'package:frontend/shared/data/network/api_client_provider.dart';

final apartmentRepositoryProvider = Provider<ApartmentRepository>((ref) {
  final apiClient = ref.read(apiClientProvider);

  return ApartmentRepository(apiClient);
});

class ApartmentRepository {
  ApartmentRepository(this._apiClient, {String baseUrl = "/apartments"})
    : _baseUrl = baseUrl;

  final ApiClient _apiClient;
  final String _baseUrl;

  Future<Apartment> get(int apartmentId) async {
    try {
      final response = await _apiClient.get("$_baseUrl/$apartmentId");
      return Apartment.fromJson(response);
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<CreateApartmentResponse> create({
    required String name,
    String? address,
  }) async {
    try {
      final minutesOffset = DateTime.now().timeZoneOffset.inMinutes;

      final response = await _apiClient.post(
        _baseUrl,
        data: {
          "name": name,
          "address": address?.isEmpty == true ? null : address,
          "minutesOffset": minutesOffset,
        },
      );

      try {
        return CreateApartmentResponse.fromJson(response);
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      if (e.response?.statusCode == 403) {
        throw AlreadyInApartmentFailure();
      }

      throw mapDioException(e);
    }
  }

  Future<JoinApartmentResponse> join(String inviteCode) async {
    try {
      final response = await _apiClient.post(
        "$_baseUrl/join",
        queryParameters: {'code': inviteCode},
      );

      try {
        return JoinApartmentResponse.fromJson(response);
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      if (e.response?.statusCode == 403) {
        throw AlreadyInApartmentFailure();
      }

      throw mapDioException(e);
    }
  }

  Future<void> leave() async {
    try {
      await _apiClient.post("$_baseUrl/leave");
    } on DioException catch (e) {
      if (e.response?.statusCode == 403) {
        throw NotInApartmentFailure();
      }

      throw mapDioException(e);
    }
  }

  Future<String> generateCode(int apartmentId) async {
    try {
      final response = await _apiClient.patch("$_baseUrl/$apartmentId/code");
      return response["inviteCode"];
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<void> setBudget(int apartmentId, int budget) async {
    try {
      await _apiClient.patch(
        "$_baseUrl/$apartmentId/budget",
        data: {"budget": budget},
      );
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<void> delete(int apartmentId) async {
    try {
      await _apiClient.delete("$_baseUrl/$apartmentId");
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }
}
