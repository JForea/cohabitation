import 'package:flutter/material.dart';
import 'package:frontend/features/expenses/presentation/widgets/stats/inhabitant_expense_line.dart';
import 'package:frontend/shared/domain/models/profile/profile.dart';
import 'package:frontend/app/theme/app_decorations.dart';
import 'package:frontend/shared/presentation/widgets/avatars/avatar.dart';

class InhabitantExpensesAmountCard extends StatelessWidget {
  const InhabitantExpensesAmountCard({
    super.key,
    required this.inhabitant,
    required this.percent,
    required this.onTap,
  });

  final Profile inhabitant;
  final double percent;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: .all(15),
        decoration: AppDecorations.cardDecoration(
          context: context,
          selected: false,
        ),
        child: Row(
          children: [
            Avatar(name: inhabitant.name, size: 32, color: inhabitant.color),
            SizedBox(width: 15),
            Expanded(
              child: Column(
                crossAxisAlignment: .start,
                mainAxisSize: .min,
                spacing: 5,
                children: [
                  Text(
                    inhabitant.name,
                    maxLines: 1,
                    overflow: .ellipsis,
                    style: TextStyle(
                      color: Theme.of(context).colorScheme.onSurface,
                      fontSize: 14,
                      fontWeight: .w500,
                    ),
                  ),
                  InhabitantExpenseLine(
                    color: inhabitant.color,
                    percent: percent,
                  ),
                ],
              ),
            ),
            SizedBox(width: 25),
            SizedBox(
              width: 80,
              child: Text(
                "${inhabitant.monthlyExpensesAmount} ₽",
                style: TextStyle(
                  color: inhabitant.color,
                  fontSize: 14,
                  fontWeight: .w700,
                ),
                textAlign: .end,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
