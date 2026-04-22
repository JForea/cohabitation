import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/entities/user.dart';
import 'package:frontend/shared/data/network/dio_client.dart';
import 'package:frontend/features/auth/data/models/auth_state.dart';

class AuthNotifier extends Notifier<AuthState> {
  @override
  AuthState build() {
    return AuthState();
  }

  Future<void> _parseResponse(Response<dynamic> response) async {
    final token = response.headers['Authorization'];

    if (token == null) {
      print("Authorization failed.");
      state = AuthState(isError: true);
      return;
    }

    await AppDio.updateToken(token.first);

    final user = User.fromJson(response.data);

    state = AuthState(token: token.first, user: user);
  }

  Future<void> register(
    String email,
    String password,
    String name,
    bool male,
  ) async {
    state = AuthState(isLoading: true);

    try {
      final response = await AppDio.dio.post(
        '/users/auth/registry',
        data: {
          "email": email,
          "password": password,
          "name": name,
          "male": male,
        },
      );

      _parseResponse(response);
    } catch (e) {
      print(e.toString());
      state = AuthState(isError: true);
    }
  }

  Future<void> login(String email, String password) async {
    state = AuthState(isLoading: true);

    final response = await AppDio.dio.post(
      '/users/auth/login',
      data: {"email": email, "password": password},
    );

    _parseResponse(response);
  }

  Future<void> whoAmI() async {
    final response = await AppDio.dio.get("/users/me");

    _parseResponse(response);
  }

  Future<void> logout() async {
    await AppDio.updateToken(null);
    state = AuthState();
  }
}
