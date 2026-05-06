import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:frontend/shared/data/models/expense.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/theme/app_shadows.dart';
import 'package:frontend/shared/presentation/ui/widgets/badges/expense_category_badge.dart';
import 'package:intl/intl.dart';

class ExpenseCard extends StatelessWidget {
  const ExpenseCard({super.key, required this.expense});

  final Expense expense;

  void showImage(BuildContext context) {
    if (expense.checkImageUrl != null) {
      showDialog(
        context: context,
        builder: (_) => Dialog(
          backgroundColor: Colors.transparent,
          child: InteractiveViewer(
            child: Image.network(expense.checkImageUrl!),
          ),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () => showImage(context),
      child: Container(
        padding: .symmetric(horizontal: 12, vertical: 15),
        decoration: BoxDecoration(
          color: Theme.of(context).colorScheme.surfaceContainer,
          boxShadow: [AppShadows.standard()],
          borderRadius: .all(.circular(15)),
        ),
        child: IntrinsicHeight(
          child: Row(
            crossAxisAlignment: .stretch,
            spacing: 12,
            children: [
              ExpenseCategoryBadge(category: expense.category),
              Column(
                crossAxisAlignment: .start,
                spacing: 4,
                children: [
                  Text(
                    expense.name,
                    maxLines: 1,
                    overflow: .ellipsis,
                    style: TextStyle(fontSize: 14, fontWeight: .w500),
                  ),
                  Text(
                    "${expense.createdBy.name} · ${DateFormat("d MMMM", "ru_RU").format(expense.createdAt)}",
                    maxLines: 1,
                    overflow: .ellipsis,
                    style: TextStyle(
                      color: Color(0xFFA3A3A3),
                      fontSize: 13,
                      fontWeight: .w500,
                    ),
                  ),
                ],
              ),
              Spacer(),
              Column(
                crossAxisAlignment: .end,
                mainAxisAlignment: .spaceBetween,
                children: [
                  if (expense.checkImageUrl != null)
                    SvgPicture.asset(
                      "assets/icons/attachment.svg",
                      height: 20,
                      width: 20,
                      colorFilter: ColorFilter.mode(AppColors.greyBlue, .srcIn),
                    ),
                  if (expense.checkImageUrl == null) SizedBox(),

                  Text(
                    "${NumberFormat("#,###", "ru_RU").format(expense.sum)} ₽",
                    style: TextStyle(
                      color: Theme.of(context).colorScheme.onSurface,
                      fontSize: 13,
                      fontWeight: .w500,
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
