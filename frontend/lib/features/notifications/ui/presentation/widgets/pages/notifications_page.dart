import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/notifications/data/providers/notifications_provider.dart';
import 'package:frontend/features/notifications/ui/presentation/widgets/lists/notification_list.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_app_floating_action_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/page_wrapper.dart';

class NotificationsPage extends ConsumerWidget {
  const NotificationsPage({super.key});

  Future<void> _refresh(WidgetRef ref) async {
    ref.read(notificationsProvider.notifier).refresh();
  }

  void _markAsRead(WidgetRef ref, int notificationId) {
    ref.read(notificationsProvider.notifier).markAsRead(notificationId);
  }

  void _markAsReadAll(WidgetRef ref) {
    ref.read(notificationsProvider.notifier).markAsReadAll();
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final notifications = ref.watch(notificationsProvider);

    return RefreshIndicator(
      onRefresh: () => _refresh(ref),
      child: Scaffold(
        floatingActionButton: CustomAppFloatingActionButton(
          onPressed: () => _markAsReadAll(ref),
          iconData: Icons.visibility,
        ),
        body: PageWrapper(
          backButton: true,
          pageName: "Уведомления",
          bottomFloatingButtonExists: true,
          children: [
            notifications.when(
              data: (notifications) => NotificationList(
                notifications: notifications,
                onNotificationTap: (id) => _markAsRead(ref, id),
              ),
              error: (_, _) => Text("Произошла ошибка при загрузке"),
              loading: () => Center(child: CircularProgressIndicator()),
            ),
          ],
        ),
      ),
    );
  }
}
