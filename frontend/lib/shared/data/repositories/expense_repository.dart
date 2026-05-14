import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/failures/failures.dart';
import 'package:frontend/shared/data/failures/map_dio_exceptiond.dart';
import 'package:frontend/shared/data/models/expense.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/data/models/profile/profile_brief.dart';
import 'package:frontend/shared/data/network/dio_provider.dart';
import 'package:frontend/shared/data/types/expense_category.dart';
import 'package:frontend/shared/utils/util_functions.dart';
import 'package:image_picker/image_picker.dart';

final expenseRepositoryProvider = Provider<ExpenseRepository>((ref) {
  final dio = ref.read(dioProvider);

  return ExpenseRepository(dio);
});

class ExpenseRepository {
  ExpenseRepository(this._dio);

  final Dio _dio;

  String _baseUrl(int apartmentId) {
    return "/apartments/$apartmentId/expenses";
  }

  Future<int> getExpenseAmount(int apartmentId) async {
    try {
      final response = await _dio.get("${_baseUrl(apartmentId)}/amount");

      try {
        return response.data;
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
  }) async {
    try {
      final query = {'page': '$page', 'size': '$pageSize'};

      final response = await _dio.get(
        _baseUrl(apartmentId),
        queryParameters: query,
      );

      final data = response.data as List;

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

      final response = await _dio.post(_baseUrl(apartmentId), data: formData);

      try {
        return Expense(
          id: response.data["id"],
          category: category,
          checkImageUrl: response.data["checkImageUrl"],
          name: name,
          createdBy: ProfileBrief.fromFullProfile(createdBy),
          sum: amount,
          createdAt: DateTime.now(),
        );
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<void> deleteMany(int apartmentId, List<int> ids) async {
    try {
      await _dio.delete(_baseUrl(apartmentId), data: ids);
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }
}
