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
}
