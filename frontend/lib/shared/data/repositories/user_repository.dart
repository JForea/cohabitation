import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/failures/failures.dart';
import 'package:frontend/shared/data/failures/map_dio_exceptiond.dart';
import 'package:frontend/shared/data/models/user.dart';
import 'package:frontend/shared/data/network/dio_provider.dart';

final userRepositoryProvider = Provider<UserRepository>((ref) {
  final dio = ref.read(dioProvider);

  return UserRepository(dio);
});

class UserRepository {
  UserRepository(this._dio, {String baseUrl = "/users"}) : _baseUrl = baseUrl;

  final Dio _dio;
  final String _baseUrl;

  Future<User> authorize(
    bool register,
    String email,
    String password, {
    String? name,
    bool? male,
    String? deviceId,
    String? fcmToken,
    String? platform,
  }) async {
    if (register && (name == null || male == null)) {
      throw InvalidDataFailure();
    }

    try {
      final response = await _dio.post(
        '$_baseUrl/auth/${register ? "registry" : "login"}',
        data: {
          "email": email,
          "password": password,
          "name": name,
          "male": male,
          if (deviceId != null && fcmToken != null && platform != null)
            "deviceToken": {
              "deviceId": deviceId,
              "token": fcmToken,
              "platform": platform,
            },
        },
      );

      try {
        return User.fromJson(response.data);
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      if (!register && e.response?.statusCode == 404) {
        throw UserNotFoundFailure();
      }

      if (register && e.response?.statusCode == 409) {
        throw UserAlreadyExistsFailure();
      }

      throw mapDioException(e);
    }
  }

  Future<User> getMe() async {
    try {
      final response = await _dio.get("$_baseUrl/me");
      try {
        return User.fromJson(response.data);
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<void> logout(String deviceId) async {
    try {
      await _dio.post("$_baseUrl/auth/logout", data: {"deviceId": deviceId});
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }
}
