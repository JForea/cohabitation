import 'package:frontend/shared/data/entities/user.dart';

class AuthState {
  final User? user;
  final String? token;
  final bool isLoading;
  final bool isError;

  AuthState({
    this.user,
    this.token,
    this.isLoading = false,
    this.isError = false,
  });

  bool get isAuthenticated => token != null;

  AuthState copyWith({
    User? user,
    String? token,
    bool? isLoading,
    bool? isError,
  }) {
    return AuthState(
      user: user ?? this.user,
      token: token ?? this.token,
      isLoading: isLoading ?? this.isLoading,
      isError: isError ?? this.isError,
    );
  }
}
