import 'package:frontend/shared/data/models/user.dart';

class AuthState {
  final User? user;
  final String? token;

  AuthState({this.user, this.token});

  bool get isAuthenticated => token != null;

  AuthState copyWith({
    User? user,
    String? token,
    bool? clearUser,
    bool? clearToken,
  }) {
    return AuthState(
      user: clearUser != null && clearUser ? null : (user ?? this.user),
      token: clearToken != null && clearToken ? null : (token ?? this.token),
    );
  }
}
