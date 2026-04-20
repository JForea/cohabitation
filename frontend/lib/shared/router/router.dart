import 'package:frontend/features/auth/presentation/onboarding_page.dart';
import 'package:frontend/features/auth/presentation/registration_first_page.dart';
import 'package:frontend/features/auth/presentation/registration_second_page.dart';
import 'package:frontend/features/auth/presentation/welcome_page.dart';
import 'package:go_router/go_router.dart';

final GoRouter router = GoRouter(
  routes: [
    GoRoute(path: '/', builder: (context, state) => OnboardingPage()),
    GoRoute(
      path: '/auth',
      builder: (context, state) => WelcomePage(),
      routes: [
        GoRoute(
          path: 'register',
          redirect: (context, state) {
            final path = state.uri.path;

            final allowedPaths = {'/auth/register/1', '/auth/register/2'};

            if (!allowedPaths.contains(path)) {
              return '/auth/register/1';
            }

            return null;
          },
          routes: [
            GoRoute(
              path: '1',
              builder: (context, state) => RegistrationFirstPage(),
            ),
            GoRoute(
              path: '2',
              builder: (context, state) => RegistrationSecondPage(),
            ),
          ],
        ),
      ],
    ),
  ],
);
