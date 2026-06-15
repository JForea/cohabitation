import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/providers/async_user_provider.dart';
import 'package:frontend/shared/data/providers/fcm_listener_provider.dart';
import 'package:frontend/shared/presentation/theme/custom_theme.dart';
import 'package:frontend/shared/router/router_provider.dart';

class App extends ConsumerWidget {
  const App({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    ref.watch(fcmListenerProvider);
    final authState = ref.watch(asyncUserProvider);
    final router = ref.watch(routerProvider);

    if (authState.isLoading) {
      return SafeArea(
        child: MaterialApp(
          theme: customTheme,
          title: 'Flatly',
          builder: (context, child) =>
              Scaffold(body: Center(child: CircularProgressIndicator())),
        ),
      );
    }

    return SafeArea(
      child: MaterialApp.router(
        debugShowCheckedModeBanner: false,
        title: 'Flatly',
        theme: customTheme,
        routerConfig: router,
      ),
    );
  }
}
