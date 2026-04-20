import 'package:dio/dio.dart';

class AuthService {
  final Dio dio;

  AuthService(this.dio);

  Future<Response> register(
    String email,
    String password,
    String name,
    bool male,
  ) {
    return dio.post(
      '/api/users/auth/registry',
      data: {'email': email, 'password': password, 'name': name, 'male': male},
    );
  }
}
