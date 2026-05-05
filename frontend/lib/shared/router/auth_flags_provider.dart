import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';

class _AuthFlags {
  const _AuthFlags({
    required this.isLoading,
    required this.isLoggedIn,
    required this.isInApartment,
  });

  final bool isLoading;
  final bool isLoggedIn;
  final bool isInApartment;
}

final authFlagsProvider = Provider<_AuthFlags>((ref) {
  final isLoading = ref.watch(authProvider.select((s) => s.isLoading));

  final isLoggedIn = ref.watch(
    authProvider.select((s) => s.value?.token != null),
  );

  final isInApartment = ref.watch(
    authProvider.select((s) => s.value?.user?.profile != null),
  );

  return _AuthFlags(
    isLoading: isLoading,
    isLoggedIn: isLoggedIn,
    isInApartment: isInApartment,
  );
});
