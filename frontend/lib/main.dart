import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/theme/custom_theme.dart';
import 'package:frontend/shared/router/router.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

Future<void> main() async {
  await dotenv.load(fileName: ".env");
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'Flutter Demo',
      theme: customTheme,
      routerConfig: router,
    );
  }
}
