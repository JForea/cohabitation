import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/core/failures/failures.dart';
import 'package:frontend/core/failures/map_dio_exception.dart';
import 'package:frontend/shared/data/filters/expenses_filter.dart';
import 'package:frontend/shared/domain/models/expense.dart';
import 'package:frontend/shared/domain/models/profile/profile.dart';
import 'package:frontend/shared/domain/models/profile/profile_brief.dart';
import 'package:frontend/core/network/api_client.dart';
import 'package:frontend/core/network/api_client_provider.dart';
import 'package:frontend/shared/domain/types/expense_category.dart';
import 'package:frontend/core/utils/util_functions.dart';
import 'package:image_picker/image_picker.dart';
import 'package:intl/intl.dart';

final expenseRepositoryProvider = Provider<ExpenseRepository>((ref) {
  final apiClient = ref.read(apiClientProvider);

  return ExpenseRepository(apiClient);
});

class ExpenseRepository {
  ExpenseRepository(this._apiClient);

  final ApiClient _apiClient;

  String _baseUrl(int apartmentId) {
    return "/apartments/$apartmentId/expenses";
  }

  Future<int> getExpenseAmount(int apartmentId) async {
    try {
      final response = await _apiClient.get("${_baseUrl(apartmentId)}/amount");

      try {
        return response;
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<List<Expense>> getPage({
    required int apartmentId,
    required int page,
    required int pageSize,
    required ExpensesFilter filter,
  }) async {
    try {
      final query = {
        'page': '$page',
        'size': '$pageSize',
        if (filter.category != null)
          'category': UtilFunctions.tValueToStringRequest(filter.category!),
        if (filter.profileId != null) 'profileId': '${filter.profileId}',
        'period': DateFormat("yyyy-MM").format(filter.month),
      };

      final response = await _apiClient.get(
        _baseUrl(apartmentId),
        queryParameters: query,
      );

      final data = response as List;

      try {
        return data.map((json) => Expense.fromJson(json)).toList();
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<Expense> create({
    required int apartmentId,
    required String name,
    required int amount,
    required ExpenseCategory category,
    required Profile createdBy,
    XFile? image,
  }) async {
    try {
      final formData = FormData.fromMap({
        "data": MultipartFile.fromString('''
          {
            "name": "$name",
            "amount": $amount,
            "category": "${UtilFunctions.tValueToStringRequest(category)}"
          }
          ''', contentType: .parse('application/json')),
        if (image != null && kIsWeb)
          "image": MultipartFile.fromBytes(
            await image.readAsBytes(),
            filename: image.name,
          )
        else if (image != null)
          "image": await MultipartFile.fromFile(
            image.path,
            filename: image.name,
          ),
      });

      final response = await _apiClient.post(
        _baseUrl(apartmentId),
        data: formData,
      );

      try {
        return Expense(
          id: response["id"],
          category: category,
          checkImageUrl: response["checkImageUrl"],
          name: name,
          createdBy: ProfileBrief.fromFullProfile(createdBy),
          amount: amount,
          createdAt: DateTime.now(),
        );
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<Map<ExpenseCategory, int>> getAmountStatsByCategoryAndMonth(
    int apartmentId,
    DateTime month,
  ) async {
    try {
      final response = await _apiClient.get(
        "${_baseUrl(apartmentId)}/stats/categories",
        queryParameters: {"period": DateFormat("yyyy-MM").format(month)},
      );

      try {
        Map<ExpenseCategory, int> result = {};
        for (final category in ExpenseCategory.values) {
          final key = UtilFunctions.tValueToStringRequest(category);
          result[category] = response[key] ?? 0;
        }

        return result;
      } catch (_) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<void> deleteMany(int apartmentId, List<int> ids) async {
    try {
      await _apiClient.delete(_baseUrl(apartmentId), data: ids);
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<int> getCount(int apartmentId, DateTime month) async {
    try {
      return await _apiClient.get(
        "${_baseUrl(apartmentId)}/count",
        queryParameters: {"period": DateFormat("yyyy-MM").format(month)},
      );
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }
}
