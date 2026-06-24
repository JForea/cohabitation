import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/home/data/providers/unread_notifications_count_provider.dart';
import 'package:frontend/shared/data/providers/notifications_provider.dart';
import 'package:frontend/app/router/router_provider.dart';

final fcmListenerProvider = Provider<void>((ref) {
  FirebaseMessaging.onMessage.listen((_) {
    ref.invalidate(unreadNotificationsCountProvider);
    ref.invalidate(notificationsProvider);
  });

  FirebaseMessaging.onMessageOpenedApp.listen((_) {
    ref.invalidate(unreadNotificationsCountProvider);
    ref.invalidate(notificationsProvider);
    final router = ref.read(routerProvider);

    router.go("/notifications");
  });
});
