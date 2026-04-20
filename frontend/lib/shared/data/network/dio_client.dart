import 'package:cookie_jar/cookie_jar.dart';
import 'package:dio/dio.dart';
import 'package:dio_cookie_manager/dio_cookie_manager.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class AppDio {
  static final Dio dio =
      Dio(
          BaseOptions(
            baseUrl: dotenv.env["BASE_URL"] ?? "",
            connectTimeout: const Duration(seconds: 5),
            receiveTimeout: const Duration(seconds: 5),
            headers: {'Content-Type': 'application/json'},
          ),
        )
        ..interceptors.add(
          LogInterceptor(requestBody: true, responseBody: true),
        )
        ..interceptors.add(CookieManager(CookieJar()));
}
