import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_svg/svg.dart';
import 'package:frontend/shared/data/models/expense.dart';
import 'package:frontend/shared/data/providers/selected_provider.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/theme/app_decorations.dart';
import 'package:frontend/shared/presentation/ui/widgets/badges/expense_category_badge.dart';
import 'package:intl/intl.dart';

class ExpenseCard extends ConsumerWidget {
  const ExpenseCard({
    super.key,
    required this.expense,
    this.onSelect,
    this.onSelectCancel,
    this.selectionMode,
  });

  final Expense expense;
  final void Function(int)? onSelect;
  final void Function(int)? onSelectCancel;
  final bool? selectionMode;

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
  Widget build(BuildContext context, WidgetRef ref) {
    final isSelected = ref.watch(
      selectedProvider("expenses").select((s) => s.contains(expense.id)),
    );

    void handleTap() {
      if (isSelected) {
        onSelectCancel?.call(expense.id);
        return;
      }

      if (selectionMode != null && selectionMode!) {
        onSelect?.call(expense.id);
        return;
      }

      showImage(context);
    }

    void handleLongPress() {
      if (!isSelected) {
        onSelect?.call(expense.id);
      }
    }

    return GestureDetector(
      onTap: handleTap,
      onLongPress: handleLongPress,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 15),
        decoration: AppDecorations.cardDecoration(
          context: context,
          selected: isSelected,
          borderRadius: 15,
        ),
        child: IntrinsicHeight(
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              ExpenseCategoryBadge(category: expense.category),

              const SizedBox(width: 12),

              Flexible(
                fit: FlexFit.tight,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      expense.name,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: TextStyle(
                        fontSize: 14,
                        fontWeight: isSelected
                            ? FontWeight.w600
                            : FontWeight.w500,
                        color: isSelected
                            ? AppColors.blue
                            : Theme.of(context).colorScheme.onSurface,
                      ),
                    ),

                    const SizedBox(height: 4),

                    Text(
                      "${expense.createdBy.name} · "
                      "${DateFormat("d MMMM", "ru_RU").format(expense.createdAt)}",
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(
                        color: Color(0xFFA3A3A3),
                        fontSize: 13,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ],
                ),
              ),

              const SizedBox(width: 12),

              Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  if (expense.checkImageUrl != null)
                    SvgPicture.asset(
                      "assets/icons/attachment.svg",
                      height: 20,
                      width: 20,
                      colorFilter: ColorFilter.mode(
                        AppColors.greyBlue,
                        BlendMode.srcIn,
                      ),
                    )
                  else
                    const SizedBox(height: 20),

                  Text(
                    "${NumberFormat("#,###", "ru_RU").format(expense.sum)} ₽",
                    style: TextStyle(
                      color: Theme.of(context).colorScheme.onSurface,
                      fontSize: 13,
                      fontWeight: FontWeight.w500,
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
