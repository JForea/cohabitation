import 'package:flutter/material.dart';
import 'package:frontend/shared/data/models/notification_value.dart';
import 'package:frontend/features/notifications/presentation/widgets/lists/notification_list_part.dart';

class NotificationList extends StatelessWidget {
  const NotificationList({
    super.key,
    required this.notifications,
    required this.onNotificationTap,
  });

  final List<NotificationValue> notifications;
  final void Function(int) onNotificationTap;

  @override
  Widget build(BuildContext context) {
    List<NotificationValue> todayNotifications = notifications
        .where(
          (n) =>
              DateTime.now()
                  .difference(n.createdAt)
                  .compareTo(Duration(days: 1)) ==
              -1,
        )
        .toList();
    List<NotificationValue> beforeNotifications = notifications.sublist(
      todayNotifications.length,
    );

    return Column(
      crossAxisAlignment: .start,
      spacing: 20,
      children: [
        NotificationListPart(
          notifications: todayNotifications,
          today: true,
          onTap: onNotificationTap,
        ),
        NotificationListPart(
          notifications: beforeNotifications,
          today: false,
          onTap: onNotificationTap,
        ),
      ],
    );
  }
}
