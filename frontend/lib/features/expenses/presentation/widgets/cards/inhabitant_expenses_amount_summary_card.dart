import 'package:flutter/material.dart';
import 'package:frontend/shared/domain/models/profile/profile.dart';
import 'package:frontend/app/theme/app_decorations.dart';

class InhabitantExpensesAmountSummaryCard extends StatelessWidget {
  const InhabitantExpensesAmountSummaryCard({
    super.key,
    required this.profile,
    required this.apartmentExpenseAmount,
  });

  final Profile profile;
  final int apartmentExpenseAmount;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: .symmetric(horizontal: 20, vertical: 10),
      decoration: AppDecorations.cardDecoration(
        context: context,
        selected: false,
      ),
      child: Column(
        spacing: 8,
        children: [
          Text.rich(
            TextSpan(
              children: [
                TextSpan(
                  text: profile.name,
                  style: TextStyle(
                    color: profile.color,
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                TextSpan(
                  text: " потратил в этом месяце",
                  style: TextStyle(
                    color: Theme.of(context).colorScheme.onSurface,
                    fontSize: 16,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ],
            ),
            textAlign: TextAlign.center,
          ),
          Text.rich(
            TextSpan(
              children: [
                TextSpan(
                  text: "${profile.monthlyExpensesAmount} ₽",
                  style: TextStyle(
                    color: profile.color,
                    fontSize: 20,
                    fontWeight: .w700,
                  ),
                ),
                TextSpan(
                  text: " / $apartmentExpenseAmount ₽",
                  style: TextStyle(
                    color: Theme.of(context).colorScheme.onSurfaceVariant,
                    fontSize: 20,
                    fontWeight: .w700,
                  ),
                ),
              ],
            ),
            textAlign: .center,
          ),
        ],
      ),
    );
  }
}
