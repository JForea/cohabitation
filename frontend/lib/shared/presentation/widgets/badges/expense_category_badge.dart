import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:frontend/shared/domain/types/expense_category.dart';
import 'package:frontend/app/theme/app_colors.dart';
import 'package:frontend/core/utils/util_functions.dart';

class ExpenseCategoryBadge extends StatelessWidget {
  const ExpenseCategoryBadge({super.key, required this.category});

  final ExpenseCategory category;

  Color _getColor() {
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
    final Color color = _getColor();
    final double iconSize = 24;

    return Container(
      padding: .symmetric(vertical: 6, horizontal: 8),
      decoration: BoxDecoration(
        color: color.withAlpha(37),
        borderRadius: .all(.circular(10)),
      ),
      child: SvgPicture.asset(
        "assets/icons/expense_categories/${UtilFunctions.tValueToStringRequest(category).toLowerCase()}.svg",
        width: iconSize,
        height: iconSize,
        colorFilter: ColorFilter.mode(color, .srcIn),
      ),
    );
  }
}
