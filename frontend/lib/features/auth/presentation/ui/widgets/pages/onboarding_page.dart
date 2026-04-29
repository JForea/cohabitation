import 'package:flutter/material.dart';
import 'package:frontend/features/auth/presentation/ui/widgets/models/onboarding_model.dart';
import 'package:frontend/features/auth/types/bubble.dart';

class OnboardingPage extends StatefulWidget {
  OnboardingPage({super.key});

  @override
  State<OnboardingPage> createState() => _OnboardingPageState();
}

class _OnboardingPageState extends State<OnboardingPage> {
  int _currentScreen = 0;

  void _incrementCurrentPage() {
    setState(() {
      _currentScreen++;
    });
  }

  late final List<Widget> screens = [
    OnboardingModel(
      primaryColor: Color(0xFF6C63FF),
      gradientEndColor: Color(0xFF998DFF),
      bubbles: [
        Bubble(x: 0.12, y: 0.055, size: 140),
        Bubble(x: 0.763, y: 0.351, size: 45),
        Bubble(x: 0.73, y: 0.52, size: 80),
      ],
      icon: .asset('assets/icons/onboarding_home.svg'),
      mainText: "Добро пожаловать в Flatly",
      secondaryText:
          "Управляйте домашними делами вместе с соседями — просто и удобно",
      pagesCnt: 3,
      currentPage: 1,
      onButtonClick: _incrementCurrentPage,
    ),
    OnboardingModel(
      primaryColor: Color(0xFF3DC990),
      gradientEndColor: Color(0xFF2ED3BC),
      bubbles: [
        Bubble(x: 0.15, y: 0.17, size: 40),
        Bubble(x: 0.55, y: 0.05, size: 130),
        Bubble(x: 0.06, y: 0.55, size: 65),
      ],
      icon: .asset('assets/icons/onboarding_meeting.svg'),
      mainText: "Распределяйте задачи",
      secondaryText:
          "Назначайте обязанности соседям, отслеживайте выполнение и зарабатывайте очки",
      pagesCnt: 3,
      currentPage: 2,
      onButtonClick: _incrementCurrentPage,
    ),
    OnboardingModel(
      primaryColor: Color(0xFFFF7A55),
      gradientEndColor: Color(0xFFFFB269),
      bubbles: [
        Bubble(x: 0.03, y: 0.05, size: 80),
        Bubble(x: 0.68, y: 0.11, size: 55),
        Bubble(x: 0.6, y: 0.5, size: 120),
      ],
      icon: .asset('assets/icons/onboarding_savings.svg'),
      mainText: "Честные расходы",
      secondaryText:
          "Прозрачный учёт трат и автоматическое разделение счетов между жильцами",
      pagesCnt: 3,
      currentPage: 3,
      onButtonClick: _incrementCurrentPage,
    ),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(body: screens[_currentScreen]);
  }
}
