import 'package:flutter/material.dart';
import 'package:frontend/features/expenses/presentation/ui/widgets/cards/inhabitant_expenses_amount_card.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';

class InhabitantsExpensesAmountList extends StatelessWidget {
  const InhabitantsExpensesAmountList({
    super.key,
    required this.inhabitants,
    required this.onTap,
  });

  final List<Profile> inhabitants;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    inhabitants.sort(
      (first, second) =>
          second.monthlyExpensesAmount - first.monthlyExpensesAmount,
    );
    final maxExpensesAmount = inhabitants[0].monthlyExpensesAmount;

    return Column(
      spacing: 8,
      children: inhabitants.map((inhabitant) {
        final percent = maxExpensesAmount != 0
            ? inhabitant.monthlyExpensesAmount / maxExpensesAmount
            : 0.0;

        return InhabitantExpensesAmountCard(
          inhabitant: inhabitant,
          percent: percent,
          onTap: onTap,
        );
      }).toList(),
    );
  }
}
