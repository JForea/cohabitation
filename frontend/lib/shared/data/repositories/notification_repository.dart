import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/failures/failures.dart';
import 'package:frontend/shared/data/failures/map_dio_exceptiond.dart';
import 'package:frontend/shared/data/models/notification_value.dart';
import 'package:frontend/shared/data/network/dio_provider.dart';

final notificationRepositoryProvider = Provider<NotificationRepository>((ref) {
  final dio = ref.read(dioProvider);

  return NotificationRepository(dio);
});

class NotificationRepository {
  NotificationRepository(this._dio);

  final Dio _dio;

  String _baseUrl(int apartmentId) {
    return "/apartments/$apartmentId/notifications";
  }

  Future<List<NotificationValue>> getPage({
    required int apartmentId,
    required int page,
    required int pageSize,
  }) async {
    try {
      final query = {"page": page, "size": pageSize};

      final response = await _dio.get(
        _baseUrl(apartmentId),
        queryParameters: query,
      );

      try {
        return (response.data as List)
            .map((n) => NotificationValue.fromJson(n))
            .toList();
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<void> markAsRead(int apartmentId, int notificationId) async {
    try {
      await _dio.patch("${_baseUrl(apartmentId)}/$notificationId");
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<void> markAsReadAll(int apartmentId) async {
    try {
      await _dio.patch(_baseUrl(apartmentId));
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<int> getUnreadCount(int apartmentId) async {
    try {
      final response = await _dio.get("${_baseUrl(apartmentId)}/unread-count");
      try {
        return response.data;
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }
}
