import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/apartment_enter/presentation/pages/join_apartment_page.dart';
import 'package:frontend/features/auth/presentation/pages/login_page.dart';
import 'package:frontend/features/buyings/presentation/pages/create_buying_page.dart';
import 'package:frontend/features/events/presentation/pages/events_page.dart';
import 'package:frontend/features/expenses/presentation/pages/create_expense_page.dart';
import 'package:frontend/features/expenses/presentation/pages/expenses_details_by_month_page.dart';
import 'package:frontend/features/expenses/presentation/pages/expenses_details_by_profile_page.dart';
import 'package:frontend/features/notifications/presentation/pages/notifications_page.dart';
import 'package:frontend/features/settings/presentation/pages/settings_page.dart';
import 'package:frontend/features/tasks/presentation/pages/redact_task_page.dart';
import 'package:frontend/features/onboarding/presentation/pages/onboarding_page.dart';
import 'package:frontend/features/auth/presentation/pages/registration_second_page.dart';
import 'package:frontend/features/auth/presentation/pages/welcome_page.dart';
import 'package:frontend/features/home/presentation/pages/home_page.dart';
import 'package:frontend/features/apartment_enter/presentation/pages/create_apartment_page.dart';
import 'package:frontend/features/apartment_enter/presentation/pages/option_page.dart';
import 'package:frontend/app/router/auth_flags_provider.dart';
import 'package:frontend/shared/domain/models/task.dart';
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
        path: '/tasks/redact',
        builder: (context, state) => RedactTaskPage(task: state.extra as Task?),
      ),
      GoRoute(
        path: '/buyings/create',
        builder: (context, state) => CreateBuyingPage(),
      ),
      GoRoute(
        path: "/expenses/create",
        builder: (context, state) => CreateExpensePage(),
      ),
      GoRoute(
        path: "/expenses/details/by-profile",
        builder: (context, state) => ExpensesDetailsByProfilePage(),
      ),
      GoRoute(
        path: "/expenses/details/by-month",
        builder: (context, state) => ExpensesDetailsByMonthPage(),
      ),
      GoRoute(path: "/settings", builder: (context, state) => SettingsPage()),
    ],
  );
});
