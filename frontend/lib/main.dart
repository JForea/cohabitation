import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/presentation/theme/custom_theme.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:frontend/shared/router/router_provider.dart';

Future<void> main() async {
  await dotenv.load(fileName: ".env");
  runApp(const ProviderScope(child: MyApp()));
}

class MyApp extends ConsumerWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
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
      title: 'Flutter Demo',
      theme: customTheme,
      routerConfig: router,
    );
  }
}
