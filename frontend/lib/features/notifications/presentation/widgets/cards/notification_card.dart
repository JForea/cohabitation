import 'package:flutter/material.dart';
import 'package:frontend/shared/data/models/notification_value.dart';
import 'package:frontend/features/notifications/presentation/widgets/badges/notification_type_badge.dart';
import 'package:frontend/app/theme/app_colors.dart';
import 'package:frontend/app/theme/app_shadows.dart';
import 'package:frontend/core/utils/util_functions.dart';

class NotificationCard extends StatelessWidget {
  const NotificationCard({
    super.key,
    required this.notification,
    required this.onTap,
  });

  final NotificationValue notification;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: .infinity,
        padding: .all(15),
        decoration: BoxDecoration(
          color: Theme.of(context).colorScheme.surfaceContainer,
          borderRadius: .all(.circular(15)),
          boxShadow: [AppShadows.standard()],
        ),
        child: Row(
          crossAxisAlignment: .start,
          spacing: 15,
          children: [
            NotificationTypeBadge(type: notification.type),
            Expanded(
              child: Column(
                spacing: 8,
                crossAxisAlignment: .start,
                children: [
                  Text(
                    notification.text,
                    style: TextStyle(
                      color: notification.isRead
                          ? Theme.of(context).colorScheme.onSurfaceVariant
                          : Theme.of(context).colorScheme.onSurface,
                      fontSize: 14,
                      fontWeight: .w500,
                    ),
                  ),
                  Text(
                    UtilFunctions.timeAgo(notification.createdAt),
                    style: TextStyle(
                      color: Theme.of(context).colorScheme.onSurfaceVariant,
                      fontSize: 14,
                      fontWeight: .w400,
                    ),
                  ),
                ],
              ),
            ),
            Container(
              width: 8,
              height: 8,
              decoration: BoxDecoration(
                color: notification.isRead ? null : AppColors.blue,
                shape: .circle,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
