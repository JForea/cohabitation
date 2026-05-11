import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/providers/async_apartment_provider.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';

class _AuthFlags {
  const _AuthFlags({
    required this.isLoading,
    required this.isApartmentLoading,
    required this.isLoggedIn,
    required this.isInApartment,
  });

  final bool isLoading;
  final bool isApartmentLoading;
  final bool isLoggedIn;
  final bool isInApartment;
}

final authFlagsProvider = Provider<_AuthFlags>((ref) {
  final isAuthLoading = ref.watch(authProvider.select((s) => s.isLoading));

  final isApartmentLoading = ref.watch(
    asyncApartmentProvider.select((s) => s.isLoading),
  );

  final isLoggedIn = ref.watch(
    authProvider.select((s) => s.value?.token != null),
  );

  final isInApartment = ref.watch(
    authProvider.select((s) => s.value?.user?.profile != null),
  );

  return _AuthFlags(
    isLoading: isAuthLoading,
    isApartmentLoading: isApartmentLoading,
    isLoggedIn: isLoggedIn,
    isInApartment: isInApartment,
  );
});
