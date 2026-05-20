import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/failures/failures.dart';
import 'package:frontend/shared/data/failures/map_dio_exceptiond.dart';
import 'package:frontend/shared/data/models/rule.dart';
import 'package:frontend/shared/data/network/api_client.dart';
import 'package:frontend/shared/data/network/api_client_provider.dart';

final ruleRepositoryProvider = Provider<RuleRepository>((ref) {
  final apiClient = ref.read(apiClientProvider);

  return RuleRepository(apiClient);
});

class RuleRepository {
  RuleRepository(this._apiClient);

  final ApiClient _apiClient;

  String _baseUrl(int apartmentId) {
    return "/apartments/$apartmentId/rules";
  }

  Future<List<Rule>> getAll(int apartmentId) async {
    try {
      final response = await _apiClient.get(_baseUrl(apartmentId));

      return (response as List).map((json) => Rule.fromJson(json)).toList();
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<Rule> create(int apartmentId, String text) async {
    try {
      final response = await _apiClient.post(
        _baseUrl(apartmentId),
        data: {"text": text},
      );

      try {
        return Rule(id: response["id"] as int, text: text);
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<void> delete(int apartmentId, int ruleId) async {
    try {
      await _apiClient.delete("${_baseUrl(apartmentId)}/$ruleId");
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }
}
