import 'package:flutter/material.dart';
import 'package:frontend/shared/data/types/task_priority.dart';
import 'package:frontend/app/theme/app_colors.dart';
import 'package:frontend/app/theme/app_decorations.dart';
import 'package:frontend/core/utils/util_functions.dart';

class TaskPriorityChoiceChip extends StatelessWidget {
  const TaskPriorityChoiceChip({
    super.key,
    required this.priority,
    required this.selected,
    required this.onSelect,
  });

  final TaskPriority priority;
  final bool selected;
  final VoidCallback onSelect;

  Color getColor() {
    return switch (priority) {
      TaskPriority.low => AppColors.green,
      TaskPriority.medium => AppColors.yellow,
      TaskPriority.high => AppColors.orange,
    };
  }

  @override
  Widget build(BuildContext context) {
    final mediaQuery = MediaQuery.of(context);
    double width = (mediaQuery.size.width - 56) / 3;

    return GestureDetector(
      onTap: onSelect,
      child: Container(
        padding: .symmetric(vertical: 10),
        height: 80,
        width: width,
        decoration: AppDecorations.cardDecoration(
          context: context,
          selected: selected,
          borderRadius: 15,
        ),
        child: Column(
          children: [
            Container(
              width: 20,
              height: 20,
              decoration: BoxDecoration(
                color: getColor(),
                borderRadius: .circular(20),
              ),
              foregroundDecoration: BoxDecoration(
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withAlpha(64),
                    blurRadius: 4,
                    offset: Offset(2, 2),
                  ),
                ],
                borderRadius: .circular(20),
              ),
            ),
            Spacer(),
            Text(
              UtilFunctions.getDisplayNameFromT(priority),
              style: TextStyle(
                color: selected ? AppColors.blue : Colors.black,
                fontWeight: .w500,
                fontSize: 14,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
