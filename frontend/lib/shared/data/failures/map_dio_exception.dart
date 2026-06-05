import 'package:dio/dio.dart';
import 'package:frontend/shared/data/failures/failures.dart';

Failure mapDioException(DioException e) {
  final status = e.response?.statusCode;

  switch (status) {
    case 400:
      return const InvalidDataFailure();

    case 401:
      return const UnauthorizedFailure();

    case 403:
      return const ForbiddenFailure();

    case 404:
      return const NotFoundFailure();

    case 409:
      return const ConflictFailure();

    case 500:
      return const ServerFailure();
  }

  switch (e.type) {
    case DioExceptionType.connectionError:
    case DioExceptionType.connectionTimeout:
    case DioExceptionType.receiveTimeout:
      return const NetworkFailure();

    default:
      return const UnknownFailure();
  }
}
