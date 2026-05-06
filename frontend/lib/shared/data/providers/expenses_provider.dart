import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/models/expense.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/data/models/profile/profile_brief.dart';
import 'package:frontend/shared/data/network/dio_client.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/types/expense_category.dart';
import 'package:frontend/shared/utils/util_functions.dart';
import 'package:image_picker/image_picker.dart';

final expensesProvider =
    AsyncNotifierProvider<_ExpensesNotifier, List<Expense>>(
      _ExpensesNotifier.new,
    );

class _ExpensesNotifier extends AsyncNotifier<List<Expense>> {
  late String baseUrl;

  static const _pageSize = 20;

  int _page = 0;
  bool _hasMore = true;
  bool _isLoading = false;

  @override
  Future<List<Expense>> build() async {
    final apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));

    if (apartmentId == null) {
      throw Exception("Not in apartment.");
    }

    baseUrl = "/apartments/$apartmentId/expenses";

    return _fetchPage();
  }

  Future<List<Expense>> _fetchPage() async {
    final query = {'page': '$_page', 'size': '$_pageSize'};

    final response = await AppDio.dio.get(baseUrl, queryParameters: query);

    final data = response.data as List;

    return data.map((json) => Expense.fromJson(json)).toList();
  }

  Future<void> loadMore() async {
    if (_isLoading || !_hasMore || state.isLoading) return;

    _isLoading = true;

    final previousValue = state.value ?? [];

    try {
      _page++;
      final newExpenses = await _fetchPage();

      if (newExpenses.length < _pageSize) {
        _hasMore = false;
      }

      state = AsyncData([...previousValue, ...newExpenses]);
    } catch (e, st) {
      _page--;
      state = AsyncError<List<Expense>>(e, st);
    } finally {
      _isLoading = false;
    }
  }

  Future<void> refresh() async {
    _page = 0;
    _hasMore = true;

    state = const AsyncLoading();
    state = await AsyncValue.guard(() async {
      return _fetchPage();
    });
  }

  Future<bool> create({
    required String name,
    required int amount,
    required ExpenseCategory category,
    required Profile createdBy,
    XFile? image,
  }) async {
    if (_isLoading) {
      return false;
    }

    try {
      _isLoading = true;

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

      final response = await AppDio.dio.post(baseUrl, data: formData);

      final expense = Expense(
        id: response.data["id"],
        category: category,
        checkImageUrl: response.data["checkImageUrl"],
        name: name,
        createdBy: ProfileBrief.fromFullProfile(createdBy),
        sum: amount,
        createdAt: DateTime.now(),
      );

      state = AsyncData([expense, ...?state.value]);

      _isLoading = false;

      return true;
    } catch (e) {
      print(e);
      _isLoading = false;
      return false;
    }
  }
}
