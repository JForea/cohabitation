import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/core/failures/failures.dart';
import 'package:frontend/shared/domain/models/profile/profile.dart';
import 'package:frontend/shared/domain/models/user.dart';
import 'package:frontend/core/network/auth_session_provider.dart';
import 'package:frontend/shared/data/repositories/user_repository.dart';

final asyncUserProvider = AsyncNotifierProvider<AsyncUserNotifier, User?>(
  AsyncUserNotifier.new,
);

class AsyncUserNotifier extends AsyncNotifier<User?> {
  late UserRepository _userRepository;
  late AuthSession _authSession;

  @override
  Future<User?> build() async {
    _userRepository = ref.read(userRepositoryProvider);
    _authSession = ref.read(authSessionProvider);

    try {
      return await _userRepository.getMe();
    } on Failure catch (e) {
      _localLogout();
      if (e is UnauthorizedFailure || e is ForbiddenFailure) {
        return null;
      }
      rethrow;
    }
  }

  Future<void> authorize(
    bool register,
    String email,
    String password, {
    String? name,
    bool? male,
    String? deviceId,
    String? fcmToken,
    String? platform,
  }) async {
    if (state.value != null) {
      throw AlreadyAuthorizedFailure();
    }

    try {
      state = AsyncLoading();

      final user = await _userRepository.authorize(
        register,
        email,
        password,
        name: name,
        male: male,
        deviceId: deviceId,
        fcmToken: fcmToken,
        platform: platform,
      );

      state = AsyncData(user);
    } catch (e, st) {
      state = AsyncError(e, st);
      rethrow;
    }
  }

  void setProfile(Profile? profile) {
    final previous = state.value;

    if (previous == null) return;

    state = AsyncData(
      previous.copyWith(clearProfile: profile == null, profile: profile),
    );
  }

  void updatePoints(int pointsAdd) {
    final current = state.value;
    if (current == null || current.profile == null) {
      return;
    }

    final newPoints = current.profile!.points + pointsAdd;

    state = AsyncValue.data(
      current.copyWith(profile: current.profile!.copyWith(points: newPoints)),
    );
  }

  void addExpenseAmount(int expenseAmount) {
    final previous = state.value;

    state = AsyncData(
      previous?.copyWith(
        profile: previous.profile?.copyWith(
          monthlyExpensesAmount:
              previous.profile!.monthlyExpensesAmount + expenseAmount,
        ),
      ),
    );
  }

  Future<void> _localLogout() async {
    await _authSession.updateToken(null);
  }

  Future<void> logout(String deviceId) async {
    if (state.value == null) {
      throw UnauthorizedFailure();
    }

    try {
      await _userRepository.logout(deviceId);
    } catch (_) {}

    await _localLogout();

    state = const AsyncData(null);
  }

  Future<void> refresh() async {
    try {
      state = AsyncData(await _userRepository.getMe());
    } on Failure catch (e) {
      _localLogout();
      if (e is UnauthorizedFailure || e is ForbiddenFailure) {
        return;
      }
      rethrow;
    }
  }
}
