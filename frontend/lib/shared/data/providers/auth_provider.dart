import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:frontend/shared/data/models/auth_state.dart';
import 'package:frontend/shared/data/models/profile.dart';
import 'package:frontend/shared/data/models/user.dart';
import 'package:frontend/shared/data/network/dio_client.dart';

final authProvider = AsyncNotifierProvider<_AuthNotifier, AuthState>(
  _AuthNotifier.new,
);

class _AuthNotifier extends AsyncNotifier<AuthState> {
  final FlutterSecureStorage _storage = FlutterSecureStorage();

  @override
  Future<AuthState> build() async {
    final token = await _storage.read(key: "token");

    if (token == null) {
      return AuthState();
    }

    try {
      final user = await _fetchUser();
      return AuthState(token: token, user: user);
    } catch (_) {
      await logout();
      return AuthState();
    }
  }

  Future<void> register(
    String email,
    String password,
    String name,
    bool male,
  ) async {
    state = const AsyncValue.loading();

    state = await AsyncValue.guard(() async {
      final response = await AppDio.dio.post(
        '/users/auth/registry',
        data: {
          "email": email,
          "password": password,
          "name": name,
          "male": male,
        },
      );

      final token = response.headers['Authorization']?.first;
      if (token == null) throw Exception("No token");

      await _storage.write(key: "token", value: token);
      await AppDio.updateToken(token);

      final user = User.fromJson(response.data);

      return AuthState(token: token, user: user);
    });
  }

  Future<void> login(String email, String password) async {
    state = const AsyncValue.loading();

    state = await AsyncValue.guard(() async {
      final response = await AppDio.dio.post(
        '/users/auth/login',
        data: {"email": email, "password": password},
      );

      final token = response.headers['Authorization']?.first;
      if (token == null) throw Exception("No token");

      await _storage.write(key: "token", value: token);
      await AppDio.updateToken(token);

      final user = User.fromJson(response.data);

      return AuthState(token: token, user: user);
    });
  }

  Future<User> _fetchUser() async {
    final response = await AppDio.dio.get("/users/me");
    return User.fromJson(response.data);
  }

  void setProfile(Profile profile) {
    final current = state.value;
    if (current == null || current.user == null) return;

    final updatedUser = current.user!.copyWith(profile: profile);

    state = AsyncValue.data(current.copyWith(user: updatedUser));
  }

  Future<void> logout() async {
    await _storage.delete(key: "token");
    await AppDio.updateToken(null);

    state = AsyncValue.data(AuthState());
  }
}
