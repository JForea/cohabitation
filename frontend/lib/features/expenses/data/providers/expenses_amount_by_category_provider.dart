import 'dart:async';
import 'dart:math';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/core/failures/failures.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/repositories/expense_repository.dart';
import 'package:frontend/shared/data/types/expense_category.dart';

final expensesAmountByCategoryProvider = AsyncNotifierProvider.family(
  ExpensesAmountByCategoryNotifier.new,
);

class ExpensesAmountByCategoryNotifier
    extends AsyncNotifier<Map<ExpenseCategory, int>> {
  ExpensesAmountByCategoryNotifier(DateTime month) : _month = month;

  final DateTime _month;

  late ExpenseRepository _expenseRepository;

  late int? _apartmentId;

  @override
  FutureOr<Map<ExpenseCategory, int>> build() {
    _expenseRepository = ref.read(expenseRepositoryProvider);
    _apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));

    if (_apartmentId == null) throw NotInApartmentFailure();

    return _expenseRepository.getAmountStatsByCategoryAndMonth(
      _apartmentId!,
      _month,
    );
  }

  Future<void> refresh() async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    state = AsyncData(
      await _expenseRepository.getAmountStatsByCategoryAndMonth(
        _apartmentId!,
        _month,
      ),
    );
  }

  void addExpense(ExpenseCategory category, int value) {
    final updated = {...?state.value};

    if (updated.containsKey(category)) {
      updated[category] = max(updated[category]! + value, 0);
    } else {
      updated[category] = value;
    }

    state = AsyncData(updated);
  }
}
