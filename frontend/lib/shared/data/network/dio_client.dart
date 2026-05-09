import 'package:dio/dio.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class AppDio {
  static final FlutterSecureStorage _storage = FlutterSecureStorage();
  static String? _cachedToken;

  static final Dio dio =
      Dio(
          BaseOptions(
            baseUrl: dotenv.env["BASE_URL"] ?? "",
            connectTimeout: const Duration(seconds: 5),
            receiveTimeout: const Duration(seconds: 5),
            headers: {'Content-Type': 'application/json'},
            extra: {"withCredentials": true},
          ),
        )
        ..interceptors.add(
          InterceptorsWrapper(
            onRequest: (options, handler) async {
              _cachedToken ??= await _storage.read(key: "token");

              if (_cachedToken != null) {
                options.headers['Authorization'] = _cachedToken;
              }

              handler.next(options);
            },
          ),
        )
        ..interceptors.add(
          LogInterceptor(requestBody: false, responseBody: true),
        );

  static Future<void> updateToken(String? token) async {
    _cachedToken = token;

    if (token == null) {
      await _storage.delete(key: 'token');
    } else {
      await _storage.write(key: 'token', value: token);
    }
  }
}
