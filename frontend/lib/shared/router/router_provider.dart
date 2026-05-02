import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/auth/login/presentation/ui/widgets/pages/login_page.dart';
import 'package:frontend/features/tasks/presentation/ui/widgets/pages/create_task_page.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/features/onboarding/presentation/ui/widgets/pages/onboarding_page.dart';
import 'package:frontend/features/auth/register/presentation/ui/widgets/pages/registration_first_page.dart';
import 'package:frontend/features/auth/register/presentation/ui/widgets/pages/registration_second_page.dart';
import 'package:frontend/features/auth/presentation/ui/widgets/pages/welcome_page.dart';
import 'package:frontend/features/home/presentation/ui/widgets/pages/home_page.dart';
import 'package:frontend/features/apartment_enter/presentation/ui/widgets/pages/create_apartment_page.dart';
import 'package:frontend/features/apartment_enter/presentation/ui/widgets/pages/option_page.dart';
import 'package:go_router/go_router.dart';

final routerProvider = Provider<GoRouter>((ref) {
  final authState = ref.watch(authProvider);

  return GoRouter(
    initialLocation: '/',
    redirect: (context, state) {
      if (authState.isLoading) return null;

      print(state.uri.path);

      final path = state.uri.path;

      final bool isLoggedIn = authState.token != null;
      final bool isInApartment = authState.user?.profile != null;

      final bool isAtAuth = path.startsWith('/auth');
      final bool isAtEnter = path.startsWith('/enter');

      if (!isLoggedIn) {
        if (!isAtAuth) return '/auth/onboarding';

        if (path.startsWith('/auth/register')) {
          const allowed = ['/auth/register/1', '/auth/register/2'];
          if (!allowed.contains(path)) {
            return '/auth/register/1';
          }
        }

        return null;
      }

      if (!isInApartment) {
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
      GoRoute(path: '/auth/login', builder: (context, state) => LoginPage()),
      GoRoute(
        path: '/auth/onboarding',
        builder: (context, state) => OnboardingPage(),
      ),
      GoRoute(
        path: '/auth/register/1',
        builder: (context, state) => RegistrationFirstPage(),
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
        path: '/tasks/create',
        builder: (context, state) => CreateTaskPage(),
      ),
    ],
  );
});
