import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/home/data/providers/unread_notifications_count_provider.dart';
import 'package:frontend/shared/data/providers/notifications_provider.dart';
import 'package:frontend/features/notifications/ui/presentation/widgets/lists/notification_list.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_app_floating_action_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/snack_bars/message_snack_bar.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/page_wrapper.dart';

class NotificationsPage extends ConsumerStatefulWidget {
  const NotificationsPage({super.key});

  @override
  ConsumerState<ConsumerStatefulWidget> createState() =>
      _NotificationsPageState();
}

class _NotificationsPageState extends ConsumerState<NotificationsPage> {
  late final ScrollController _scrollController;

  Future<void> _refresh(WidgetRef ref) async {
    await ref.read(notificationsProvider.notifier).refresh();
    await ref.read(unreadNotificationsCountProvider.notifier).refresh();
  }

  void _markAsRead(
    BuildContext context,
    WidgetRef ref,
    int notificationId,
  ) async {
    final countNotifier = ref.read(unreadNotificationsCountProvider.notifier);
    final notificationsNotifier = ref.read(notificationsProvider.notifier);

    final unreadCount = ref.read(unreadNotificationsCountProvider).value ?? 0;

    countNotifier.decrement();

    try {
      await notificationsNotifier.markAsRead(notificationId);
    } catch (e) {
      countNotifier.set(unreadCount);
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          MessageSnackBar(message: "Произошла ошибка", error: true),
        );
      }
    }
  }

  void _markAsReadAll(BuildContext context, WidgetRef ref) async {
    final countNotifier = ref.read(unreadNotificationsCountProvider.notifier);

    final notificationsNotifier = ref.read(notificationsProvider.notifier);

    final unreadCount = ref.read(unreadNotificationsCountProvider).value ?? 0;

    countNotifier.clear();

    try {
      await notificationsNotifier.markAsReadAll();
    } catch (e) {
      countNotifier.set(unreadCount);
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          MessageSnackBar(message: "Произошла ошибка", error: true),
        );
      }
    }
  }

  void onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      ref.read(notificationsProvider.notifier).loadMore();
    }
  }

  @override
  void initState() {
    super.initState();

    _scrollController = ScrollController();
    _scrollController.addListener(onScroll);
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final notifications = ref.watch(notificationsProvider);

    return RefreshIndicator(
      onRefresh: () => _refresh(ref),
      child: Scaffold(
        floatingActionButton: CustomAppFloatingActionButton(
          onPressed: () => _markAsReadAll(context, ref),
          iconData: Icons.visibility,
        ),
        body: PageWrapper(
          backButton: true,
          pathIfCantPop: "/",
          pageName: "Уведомления",
          controller: _scrollController,
          bottomFloatingButtonExists: true,
          children: [
            notifications.when(
              data: (notifications) => NotificationList(
                notifications: notifications,
                onNotificationTap: (id) => _markAsRead(context, ref, id),
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
