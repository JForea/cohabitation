import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:frontend/shared/data/entities/profile.dart';
import 'package:frontend/shared/data/entities/user.dart';
import 'package:frontend/shared/data/network/dio_client.dart';
import 'package:frontend/shared/data/models/auth_state.dart';

class AuthNotifier extends Notifier<AuthState> {
  final FlutterSecureStorage _storage = FlutterSecureStorage();

  @override
  AuthState build() {
    _init();
    return AuthState(isLoading: true);
  }

  Future<void> _init() async {
    final token = await _storage.read(key: "token");

    if (token == null) {
      state = state.copyWith(isLoading: false);
      return;
    } else {
      state = state.copyWith(token: token);
    }

    try {
      await whoAmI();
    } catch (_) {
      await logout();
    } finally {
      state = state.copyWith(isLoading: false);
    }
  }

  Future<void> _parseResponse(Response<dynamic> response) async {
    final token = response.headers['Authorization'];

    if (token == null) {
      print("Authorization failed.");
      state = state.copyWith(isError: true);
      return;
    }

    await AppDio.updateToken(token.first);

    final user = User.fromJson(response.data);

    state = state.copyWith(token: token.first, user: user, isLoading: false);
  }

  Future<void> register(
    String email,
    String password,
    String name,
    bool male,
  ) async {
    state = state.copyWith(isLoading: true);

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
      state = state.copyWith(isError: true, isLoading: false);
    }
  }

  Future<void> login(String email, String password) async {
    state = state.copyWith(isLoading: true);

    try {
      final response = await AppDio.dio.post(
        '/users/auth/login',
        data: {"email": email, "password": password},
      );

      _parseResponse(response);
    } catch (e) {
      print(e.toString());
      state = state.copyWith(isError: true, isLoading: false);
    }
  }

  Future<void> whoAmI() async {
    try {
      final response = await AppDio.dio.get("/users/me");

      final user = User.fromJson(response.data);

      state = state.copyWith(user: user);
    } catch (e) {
      print(e.toString());
      state = state.copyWith(isError: true);
      throw e;
    }
  }

  Future<void> createApartment(String name, String address) async {
    try {
      final response = await AppDio.dio.post(
        "/apartments",
        data: {"name": name, "address": address == "" ? null : address},
      );

      final token = response.headers['Authorization'];

      if (token == null) {
        state = state.copyWith(isError: true);
        return;
      }

      await AppDio.updateToken(token.first);

      final profile = Profile.fromJson(response.data);

      final user = state.user!;
      user.profile = profile;

      state = state.copyWith(token: token.first, user: user, isLoading: false);
    } catch (e) {
      print("Error during creating apartment");
      print(e);
    }
  }

  Future<void> logout() async {
    await AppDio.updateToken(null);
    print("LOGGED OUT");
    state = AuthState();
  }
}
