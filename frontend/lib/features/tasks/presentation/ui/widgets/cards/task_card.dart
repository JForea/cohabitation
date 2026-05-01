import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:frontend/shared/data/models/task.dart';
import 'package:frontend/shared/data/types/task_priority.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/ui/widgets/avatars/avatar.dart';
import 'package:frontend/shared/presentation/ui/widgets/badges/points_badge.dart';
import 'package:frontend/shared/utils/get_date_display_from_date_time.dart';
import 'package:frontend/shared/utils/get_display_name_from_t.dart';

class TaskCard extends StatelessWidget {
  const TaskCard({super.key, required this.task, required this.onStatusSwitch});

  final Task task;
  final Future<void> Function() onStatusSwitch;

  Color _getTaskColor() {
    return switch (task.priority) {
      TaskPriority.low => AppColors.green,
      TaskPriority.medium => AppColors.yellow,
      TaskPriority.high => AppColors.orange,
    };
  }

  @override
  Widget build(BuildContext context) {
    final taskColor = _getTaskColor();
    final overtimed = task.dueTime?.compareTo(DateTime.now()) == -1;

    return Container(
      padding: .symmetric(horizontal: 18, vertical: 12),
      width: .infinity,
      height: 105,
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surfaceContainer,
        borderRadius: .all(.circular(15)),
      ),
      child: Row(
        crossAxisAlignment: .start,
        children: [
          GestureDetector(
            onTap: onStatusSwitch,
            child: Container(
              width: 20,
              height: 20,
              decoration: BoxDecoration(
                border: task.completedBy == null
                    ? .all(color: taskColor, width: 1)
                    : null,
                color: task.completedBy != null ? AppColors.green : null,
                borderRadius: .all(.circular(20)),
              ),
              child: task.completedBy != null
                  ? Center(
                      child: Icon(Icons.done, color: Colors.white, size: 15),
                    )
                  : null,
            ),
          ),
          SizedBox(width: 15),
          Column(
            crossAxisAlignment: .start,
            children: [
              Text(
                task.name,
                maxLines: 1,
                overflow: .ellipsis,
                style: TextStyle(
                  color: Theme.of(context).colorScheme.onSurface,
                  fontSize: 15,
                  fontWeight: .w500,
                ),
              ),
              SizedBox(height: 2),
              Text(
                task.description ?? '',
                maxLines: 2,
                overflow: .ellipsis,
                style: TextStyle(
                  fontSize: 12,
                  fontWeight: .w500,
                  color: Color(0xFFA3A3A3),
                ),
              ),
              Spacer(),
              Row(
                spacing: 6,
                children: [
                  Container(
                    padding: .symmetric(horizontal: 8, vertical: 2),
                    decoration: BoxDecoration(
                      color: AppColors.blue.withAlpha(37),
                      borderRadius: .all(.circular(20)),
                    ),
                    child: Text(
                      getDisplayNameFromT(task.room),
                      style: TextStyle(
                        color: AppColors.blue,
                        fontSize: 12,
                        fontWeight: .w500,
                      ),
                    ),
                  ),
                  if (task.dueTime != null) ...[
                    SvgPicture.asset(
                      "assets/icons/calendar.svg",
                      width: 20,
                      height: 20,
                      colorFilter: .mode(
                        overtimed ? AppColors.orange : Color(0xFF474747),
                        .srcIn,
                      ),
                    ),
                    Text(
                      getDateDisplayFromDateTime(task.dueTime!),
                      style: TextStyle(
                        color: overtimed ? AppColors.orange : Color(0xFFA3A3A3),
                        fontSize: 12,
                      ),
                    ),
                  ],
                ],
              ),
            ],
          ),
          Spacer(),
          Column(
            children: [
              PointsBadge(color: taskColor, value: task.points),
              Spacer(),
              if (task.assignedTo != null)
                Avatar(
                  name: task.assignedTo!.name,
                  size: 24,
                  color: task.assignedTo!.color,
                ),
            ],
          ),
        ],
      ),
    );
  }
}
