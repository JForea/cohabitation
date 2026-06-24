import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/core/failures/failures.dart';
import 'package:frontend/core/failures/map_dio_exception.dart';
import 'package:frontend/shared/domain/models/profile/profile.dart';
import 'package:frontend/core/network/api_client.dart';
import 'package:frontend/core/network/api_client_provider.dart';
import 'package:frontend/shared/domain/types/role.dart';

final profileRepositoryProvider = Provider<ProfileRepository>((ref) {
  final apiClient = ref.read(apiClientProvider);

  return ProfileRepository(apiClient);
});

class ProfileRepository {
  ProfileRepository(this._apiClient);

  final ApiClient _apiClient;

  String _baseUrl(int apartmentId) {
    return "/apartments/$apartmentId/profiles";
  }

  Future<List<Profile>> getAll({
    required int apartmentId,
    required bool excludeMe,
  }) async {
    try {
      final query = {"excludeMe": "$excludeMe"};

      final response = await _apiClient.get(
        _baseUrl(apartmentId),
        queryParameters: query,
      );

      try {
        return (response as List)
            .map((json) => Profile.fromJson(json))
            .toList();
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<void> kick(int apartmentId, int profileId) async {
    try {
      await _apiClient.post("${_baseUrl(apartmentId)}/$profileId/kick");
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<void> setRole(int apartmentId, int profileId, Role role) async {
    try {
      await _apiClient.patch(
        "${_baseUrl(apartmentId)}/$profileId",
        queryParameters: {"role": role.name.toUpperCase()},
      );
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }
}
