import 'package:flutter/material.dart';
import 'package:frontend/shared/domain/models/task.dart';
import 'package:frontend/app/theme/app_colors.dart';
import 'package:frontend/app/theme/app_decorations.dart';
import 'package:frontend/shared/presentation/widgets/badges/points_badge.dart';
import 'package:frontend/core/utils/util_functions.dart';

class TaskPreviewCard extends StatelessWidget {
  const TaskPreviewCard({super.key, required this.task});

  final Task task;

  @override
  Widget build(BuildContext context) {
    final overtimed = task.dueDate?.compareTo(DateTime.now()) == -1;

    return Container(
      padding: .symmetric(horizontal: 18, vertical: 12),
      decoration: AppDecorations.cardDecoration(
        context: context,
        selected: false,
      ),
      child: Row(
        mainAxisAlignment: .spaceBetween,
        children: [
          Column(
            spacing: 4,
            crossAxisAlignment: .start,
            children: [
              Text(
                task.name,
                style: TextStyle(
                  color: Colors.black,
                  fontSize: 14,
                  fontWeight: .w500,
                ),
              ),
              Row(
                children: [
                  Text(
                    "${UtilFunctions.getDisplayNameFromT(task.room)} · ",
                    style: TextStyle(
                      color: Color(0xFFA3A3A3),
                      fontSize: 12,
                      fontWeight: .w500,
                    ),
                  ),
                  Text(
                    UtilFunctions.getDateDisplayFromDateTime(task.dueDate!),
                    style: TextStyle(
                      color: overtimed ? AppColors.orange : Color(0xFFA3A3A3),
                      fontSize: 12,
                      fontWeight: .w500,
                    ),
                  ),
                ],
              ),
            ],
          ),
          PointsBadge(
            color: UtilFunctions.getColorFromPriotiry(task.priority),
            value: task.points,
          ),
        ],
      ),
    );
  }
}
