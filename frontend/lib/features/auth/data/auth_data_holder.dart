class AuthDataHolder {
  AuthDataHolder._internal();

  static final AuthDataHolder _instance = AuthDataHolder._internal();
  static AuthDataHolder get instance => _instance;

  String email = '';
  String password = '';
  String name = '';
  bool male = true;
}
