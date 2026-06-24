import 'package:flutter/material.dart';
import 'package:frontend/shared/data/models/expense.dart';
import 'package:frontend/shared/presentation/ui/widgets/cards/expense_card.dart';

class ExpenseList extends StatelessWidget {
  const ExpenseList({
    super.key,
    required this.expenses,
    this.onSelect,
    this.onSelectCancel,
    this.selected,
  });

  final List<Expense> expenses;
  final Set<int>? selected;
  final void Function(int)? onSelect;
  final void Function(int)? onSelectCancel;

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      itemCount: expenses.length,
      itemBuilder: (context, index) {
        final expense = expenses[index];
        return Padding(
          padding: const EdgeInsets.only(bottom: 10),
          child: ExpenseCard(
            key: ValueKey(expense.id),
            expense: expense,
            selectionMode: selected?.isNotEmpty,
            onSelect: onSelect,
            onSelectCancel: onSelectCancel,
          ),
        );
      },
    );
  }
}
