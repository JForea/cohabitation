import 'package:flutter/material.dart';
import 'package:frontend/shared/data/models/notification_value.dart';
import 'package:frontend/features/notifications/ui/presentation/widgets/cards/notification_card.dart';

class NotificationListPart extends StatelessWidget {
  const NotificationListPart({
    super.key,
    required this.notifications,
    required this.today,
    required this.onTap,
  });

  final List<NotificationValue> notifications;
  final bool today;
  final void Function(int) onTap;

  @override
  Widget build(BuildContext context) {
    if (notifications.isNotEmpty) {
      return Column(
        crossAxisAlignment: .start,
        spacing: 8,
        children: [
          Text(
            today ? "СЕГОДНЯ" : "РАНЕЕ",
            style: TextStyle(
              color: Theme.of(context).colorScheme.onSurfaceVariant,
              fontSize: 14,
              fontWeight: .w700,
            ),
          ),
          ...notifications.map(
            (n) => NotificationCard(notification: n, onTap: () => onTap(n.id)),
          ),
        ],
      );
    } else {
      return SizedBox();
    }
  }
}
