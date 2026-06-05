import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/network/api_client.dart';
import 'package:frontend/shared/data/network/auth_session_provider.dart';
import 'package:frontend/shared/data/network/dio_api_client.dart';

final apiClientProvider = Provider<ApiClient>((ref) {
  final authSession = ref.read(authSessionProvider);

  String baseUrl;

  if (kIsWeb && !kDebugMode) {
    baseUrl = "/api";
  } else {
    baseUrl = "${dotenv.env["BASE_URL"]}/api";
  }

  final dio = Dio(
    BaseOptions(
      baseUrl: baseUrl,
      connectTimeout: const Duration(seconds: 5),
      receiveTimeout: const Duration(seconds: 5),
      headers: {'Content-Type': 'application/json'},
      extra: {"withCredentials": true},
    ),
  );

  dio.interceptors.add(
    InterceptorsWrapper(
      onRequest: (options, handler) {
        final token = authSession.token;

        if (token != null) {
          options.headers['Authorization'] = token;
        }

        handler.next(options);
      },
      onResponse: (response, handler) async {
        final token = response.headers['Authorization'];

        if (token != null) {
          await authSession.updateToken(token.first);
        }

        handler.next(response);
      },
    ),
  );

  dio.interceptors.add(LogInterceptor(requestBody: false, responseBody: true));

  return DioApiClient(dio);
});
