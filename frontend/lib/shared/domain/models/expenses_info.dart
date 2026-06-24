import 'package:frontend/shared/domain/models/expense.dart';

class ExpensesInfo {
  const ExpensesInfo({
    required this.currentExpenseAmount,
    required this.currentExpenseQuantity,
    required this.expenses,
  });

  factory ExpensesInfo.fromJson(Map<String, dynamic> json) {
    return ExpensesInfo(
      currentExpenseAmount: json["currentExpenseAmount"],
      currentExpenseQuantity: json["currentExpenseQuantity"],
      expenses: (json["expenses"] as List)
          .map((e) => Expense.fromJson(e))
          .toList(),
    );
  }

  final int currentExpenseAmount;
  final int currentExpenseQuantity;
  final List<Expense> expenses;

  ExpensesInfo copyWith({
    int? currentExpenseAmount,
    int? currentExpenseQuantity,
    List<Expense>? expenses,
  }) {
    return ExpensesInfo(
      currentExpenseAmount: currentExpenseAmount ?? this.currentExpenseAmount,
      currentExpenseQuantity:
          currentExpenseQuantity ?? this.currentExpenseQuantity,
      expenses: expenses ?? this.expenses,
    );
  }
}
