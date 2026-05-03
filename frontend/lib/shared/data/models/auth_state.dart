import 'package:frontend/shared/data/models/profile.dart';
import 'package:frontend/shared/data/models/user.dart';

class AuthState {
  final User? user;
  final String? token;

  AuthState({this.user, this.token});

  bool get isAuthenticated => token != null;

  AuthState copyWith({Profile? profile, String? token}) {
    return AuthState(
      user: profile != null && user != null
          ? User(
              id: user!.id,
              email: user!.email,
              name: user!.name,
              profile: profile,
            )
          : user,
      token: token ?? this.token,
    );
  }
}
