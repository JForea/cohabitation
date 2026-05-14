import 'package:dio/dio.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/network/auth_session_provider.dart';

final dioProvider = Provider<Dio>((ref) {
  final authSession = ref.read(authSessionProvider);

  final dio = Dio(
    BaseOptions(
      baseUrl: dotenv.env["BASE_URL"] ?? "",
      connectTimeout: const Duration(seconds: 5),
      receiveTimeout: const Duration(seconds: 5),
      headers: {'Content-Type': 'application/json'},
      extra: {"withCredentials": true},
    ),
  );

  dio.interceptors.add(
    InterceptorsWrapper(
      onRequest: (options, handler) async {
        final token = await authSession.token;

        if (token != null) {
          options.headers['Authorization'] = token;
        }

        handler.next(options);
      },
      onResponse: (response, handler) {
        final token = response.headers['Authorization'];

        if (token != null) {
          authSession.updateToken(token.first);
        }

        handler.next(response);
      },
    ),
  );

  dio.interceptors.add(LogInterceptor(requestBody: false, responseBody: true));

  return dio;
});
