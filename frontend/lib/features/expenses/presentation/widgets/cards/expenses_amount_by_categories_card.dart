import 'dart:math';

import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:frontend/shared/domain/types/expense_category.dart';
import 'package:frontend/app/theme/app_colors.dart';
import 'package:frontend/app/theme/app_decorations.dart';
import 'package:frontend/core/utils/util_functions.dart';

class _ExpenseAmountInCategory {
  _ExpenseAmountInCategory({
    required this.category,
    required this.color,
    required this.percent,
    required this.value,
  });

  final String category;
  final Color color;
  final double value;
  double percent;
}

class ExpensesAmountByCategoriesCard extends StatelessWidget {
  const ExpensesAmountByCategoriesCard({
    super.key,
    required this.expensesAmountByCategories,
  });

  final Map<ExpenseCategory, int> expensesAmountByCategories;

  Color _getColor(ExpenseCategory category) {
    return switch (category) {
      .householdGoods => AppColors.purple,
      .housinAndCommunalServices => AppColors.green,
      .other => AppColors.brown,
      .products => AppColors.orange,
      .rent => AppColors.blue,
      .services => AppColors.yellow,
    };
  }

  @override
  Widget build(BuildContext context) {
    List<_ExpenseAmountInCategory> expensesInCategories = [];
    int sum = 0;
    for (final entry in expensesAmountByCategories.entries) {
      expensesInCategories.add(
        _ExpenseAmountInCategory(
          category: UtilFunctions.getDisplayNameFromT(entry.key),
          color: _getColor(entry.key),
          percent: 0,
          value: entry.value.toDouble(),
        ),
      );
      sum += entry.value;
    }

    for (final expensesInCategory in expensesInCategories) {
      expensesInCategory.percent =
          ((expensesInCategory.value / sum) * 1000).round() / 10;
    }

    expensesInCategories.sort(
      (first, second) => (second.value - first.value).round(),
    );

    return Container(
      padding: .all(15),
      decoration: AppDecorations.cardDecoration(
        context: context,
        selected: false,
      ),
      child: Column(
        spacing: 15,
        mainAxisSize: .min,
        crossAxisAlignment: .start,
        children: [
          Text(
            "По категориям",
            style: TextStyle(
              color: Theme.of(context).colorScheme.onSurface,
              fontSize: 16,
              fontWeight: .w500,
            ),
          ),
          Row(
            spacing: 20,
            children: [
              SizedBox(
                height: 100,
                width: 100,
                child: PieChart(
                  PieChartData(
                    sectionsSpace: 2,
                    centerSpaceRadius: 25,
                    startDegreeOffset: -90,
                    sections: expensesInCategories
                        .map(
                          (e) => PieChartSectionData(
                            value: e.value != 0 ? max(e.percent, 3.0) : 0,
                            color: e.color,
                            radius: 25,
                            showTitle: false,
                          ),
                        )
                        .toList(),
                  ),
                ),
              ),
              Expanded(
                child: Column(
                  crossAxisAlignment: .start,
                  spacing: 8,
                  children: expensesInCategories
                      .map(
                        (e) => Row(
                          spacing: 8,
                          children: [
                            Container(
                              width: 8,
                              height: 8,
                              decoration: BoxDecoration(
                                shape: .circle,
                                color: e.color,
                              ),
                            ),
                            Expanded(
                              child: Text(
                                e.category,
                                maxLines: 1,
                                overflow: .ellipsis,
                                style: TextStyle(
                                  color: Theme.of(
                                    context,
                                  ).colorScheme.onSurfaceVariant,
                                  fontSize: 12,
                                  fontWeight: .w600,
                                ),
                              ),
                            ),
                            Text(
                              "${e.percent}%",
                              style: TextStyle(
                                color: Theme.of(context).colorScheme.onSurface,
                                fontSize: 12,
                                fontWeight: .w600,
                              ),
                            ),
                          ],
                        ),
                      )
                      .toList(),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
