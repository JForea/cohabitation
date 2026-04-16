import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/theme/custom_theme.dart';
import 'package:frontend/features/auth/presentation/onboarding_page.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: customTheme,
      home: OnboardingPage(),
    );
  }
}
