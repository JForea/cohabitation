import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/core/network/auth_session_provider.dart';
import 'package:frontend/shared/data/providers/async_apartment_provider.dart';
import 'package:frontend/shared/data/providers/async_user_provider.dart';

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
  final isAuthLoading = ref.watch(asyncUserProvider.select((s) => s.isLoading));

  final isApartmentLoading = ref.watch(
    asyncApartmentProvider.select((s) => s.isLoading),
  );

  final isLoggedIn = ref.watch(
    authSessionProvider.select((s) => s.token != null),
  );

  final isInApartment = ref.watch(
    asyncUserProvider.select((s) => s.value?.profile != null),
  );

  return _AuthFlags(
    isLoading: isAuthLoading,
    isApartmentLoading: isApartmentLoading,
    isLoggedIn: isLoggedIn,
    isInApartment: isInApartment,
  );
});
