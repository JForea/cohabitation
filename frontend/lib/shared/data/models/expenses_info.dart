import 'package:frontend/shared/data/models/expense.dart';

class ExpensesInfo {
  const ExpensesInfo({
    required this.currentExpenseAmount,
    required this.expenses,
  });

  factory ExpensesInfo.fromJson(Map<String, dynamic> json) {
    return ExpensesInfo(
      currentExpenseAmount: json["currentExpenseAmount"],
      expenses: (json["expenses"] as List)
          .map((e) => Expense.fromJson(e))
          .toList(),
    );
  }

  final int currentExpenseAmount;
  final List<Expense> expenses;

  ExpensesInfo copyWith({int? currentExpenseAmount, List<Expense>? expenses}) {
    return ExpensesInfo(
      currentExpenseAmount: currentExpenseAmount ?? this.currentExpenseAmount,
      expenses: expenses ?? this.expenses,
    );
  }
}
