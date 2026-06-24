import 'package:dio/dio.dart';
import 'package:frontend/core/network/api_client.dart';

class DioApiClient implements ApiClient {
  const DioApiClient(this.dio);

  final Dio dio;

  @override
  Future<dynamic> get(
    String path, {
    Map<String, dynamic>? queryParameters,
    dynamic data,
  }) async {
    final response = await dio.get(
      path,
      queryParameters: queryParameters,
      data: data,
    );

    return response.data;
  }

  @override
  Future<dynamic> post(
    String path, {
    Map<String, dynamic>? queryParameters,
    dynamic data,
  }) async {
    final response = await dio.post(
      path,
      queryParameters: queryParameters,
      data: data,
    );

    return response.data;
  }

  @override
  Future<dynamic> put(
    String path, {
    Map<String, dynamic>? queryParameters,
    dynamic data,
  }) async {
    final response = await dio.put(
      path,
      queryParameters: queryParameters,
      data: data,
    );

    return response.data;
  }

  @override
  Future<dynamic> patch(
    String path, {
    Map<String, dynamic>? queryParameters,
    dynamic data,
  }) async {
    final response = await dio.patch(
      path,
      queryParameters: queryParameters,
      data: data,
    );

    return response.data;
  }

  @override
  Future<dynamic> delete(
    String path, {
    Map<String, dynamic>? queryParameters,
    dynamic data,
  }) async {
    final response = await dio.delete(
      path,
      queryParameters: queryParameters,
      data: data,
    );

    return response.data;
  }
}
