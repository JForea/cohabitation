import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/apartment_enter/presentation/ui/widgets/pages/join_apartment_page.dart';
import 'package:frontend/features/auth/login/presentation/ui/widgets/pages/login_page.dart';
import 'package:frontend/features/buyings/presentation/ui/widgets/pages/create_buying_page.dart';
import 'package:frontend/features/events/presentation/ui/widgets/pages/events_page.dart';
import 'package:frontend/features/expenses/presentation/ui/widgets/pages/create_expense_page.dart';
import 'package:frontend/features/notifications/ui/presentation/widgets/pages/notifications_page.dart';
import 'package:frontend/features/settings/presentation/ui/widgets/pages/settings_page.dart';
import 'package:frontend/features/tasks/presentation/ui/widgets/pages/create_task_page.dart';
import 'package:frontend/features/onboarding/presentation/ui/widgets/pages/onboarding_page.dart';
import 'package:frontend/features/auth/register/presentation/ui/widgets/pages/registration_second_page.dart';
import 'package:frontend/features/auth/presentation/ui/widgets/pages/welcome_page.dart';
import 'package:frontend/features/home/presentation/ui/widgets/pages/home_page.dart';
import 'package:frontend/features/apartment_enter/presentation/ui/widgets/pages/create_apartment_page.dart';
import 'package:frontend/features/apartment_enter/presentation/ui/widgets/pages/option_page.dart';
import 'package:frontend/shared/router/auth_flags_provider.dart';
import 'package:go_router/go_router.dart';

final routerProvider = Provider<GoRouter>((ref) {
  final notifier = ValueNotifier(0);

  ref.listen(authFlagsProvider, (_, _) {
    notifier.value++;
  });

  return GoRouter(
    initialLocation: '/',
    refreshListenable: notifier,
    redirect: (context, state) {
      final flags = ref.read(authFlagsProvider);

      if (flags.isLoading || flags.isApartmentLoading) return null;

      final path = state.uri.path;

      final isAtAuth = path.startsWith('/auth');
      final isAtEnter = path.startsWith('/enter');

      if (!flags.isLoggedIn) {
        if (!isAtAuth) return '/auth/onboarding';

        if (path.startsWith('/auth/register')) {
          const allowed = ['/auth/register/1', '/auth/register/2'];
          if (!allowed.contains(path)) {
            return '/auth/register/1';
          }
        }

        return null;
      }

      if (!flags.isInApartment) {
        if (!isAtEnter) return '/enter';
        return null;
      }

      if (isAtAuth || isAtEnter) {
        return '/';
      }

      return null;
    },
    routes: [
      GoRoute(path: '/', builder: (context, state) => HomePage()),
      GoRoute(path: '/auth', builder: (context, state) => WelcomePage()),
      GoRoute(
        path: '/auth/login',
        builder: (context, state) => LoginPage(register: false),
      ),
      GoRoute(
        path: '/auth/onboarding',
        builder: (context, state) => OnboardingPage(),
      ),
      GoRoute(
        path: '/auth/register/1',
        builder: (context, state) => LoginPage(register: true),
      ),
      GoRoute(
        path: '/auth/register/2',
        builder: (context, state) => RegistrationSecondPage(),
      ),
      GoRoute(path: '/enter', builder: (context, state) => OptionPage()),
      GoRoute(
        path: '/enter/create',
        builder: (context, state) => CreateApartmentPage(),
      ),
      GoRoute(
        path: '/enter/join',
        builder: (context, state) => JoinApartmentPage(),
      ),
      GoRoute(
        path: "/notifications",
        builder: (context, state) => NotificationsPage(),
      ),
      GoRoute(
        path: "/events/day/:date",
        builder: (context, state) {
          final dateString = state.pathParameters["date"];

          final date = DateTime.tryParse(dateString ?? '') ?? DateTime.now();

          return EventsPage(date: date);
        },
      ),
      GoRoute(
        path: '/tasks/create',
        builder: (context, state) => CreateTaskPage(),
      ),
      GoRoute(
        path: '/buyings/create',
        builder: (context, state) => CreateBuyingPage(),
      ),
      GoRoute(
        path: "/expenses/create",
        builder: (context, state) => CreateExpensePage(),
      ),
      GoRoute(path: "/settings", builder: (context, state) => SettingsPage()),
    ],
  );
});
