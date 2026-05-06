import 'package:flutter/material.dart';
import 'package:frontend/shared/data/models/expense.dart';
import 'package:frontend/shared/presentation/ui/widgets/cards/expense_card.dart';

class ExpenseList extends StatelessWidget {
  const ExpenseList({super.key, required this.expenses});

  final List<Expense> expenses;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: .start,
      spacing: 10,
      children: [
        Text("История", style: TextStyle(fontWeight: .w500, fontSize: 16)),
        ...expenses.map((e) => ExpenseCard(expense: e)),
      ],
    );
  }
}
