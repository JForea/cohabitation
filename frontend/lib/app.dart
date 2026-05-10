import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/data/providers/fcm_listener_provider.dart';
import 'package:frontend/shared/presentation/theme/custom_theme.dart';
import 'package:frontend/shared/router/router_provider.dart';

class App extends ConsumerWidget {
  const App({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    ref.watch(fcmListenerProvider);
    final authState = ref.watch(authProvider);
    final router = ref.watch(routerProvider);

    if (authState.isLoading) {
      return MaterialApp(
        theme: customTheme,
        builder: (context, child) =>
            Scaffold(body: Center(child: CircularProgressIndicator())),
      );
    }

    return MaterialApp.router(
      debugShowCheckedModeBanner: false,
      title: 'Flutter Demo',
      theme: customTheme,
      routerConfig: router,
    );
  }
}
