import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/failures/failures.dart';
import 'package:frontend/shared/data/failures/map_dio_exceptiond.dart';
import 'package:frontend/shared/data/models/rule.dart';
import 'package:frontend/shared/data/network/dio_provider.dart';

final ruleRepositoryProvider = Provider<RuleRepository>((ref) {
  final dio = ref.read(dioProvider);

  return RuleRepository(dio);
});

class RuleRepository {
  RuleRepository(this._dio);

  final Dio _dio;

  String _baseUrl(int apartmentId) {
    return "/apartments/$apartmentId/rules";
  }

  Future<List<Rule>> getAll(int apartmentId) async {
    try {
      final response = await _dio.get(_baseUrl(apartmentId));

      return (response.data as List)
          .map((json) => Rule.fromJson(json))
          .toList();
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<Rule> create(int apartmentId, String text) async {
    try {
      final response = await _dio.post(
        _baseUrl(apartmentId),
        data: {"text": text},
      );

      try {
        return Rule(id: response.data["id"] as int, text: text);
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<void> delete(int apartmentId, int ruleId) async {
    try {
      await _dio.delete("${_baseUrl(apartmentId)}/$ruleId");
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }
}
