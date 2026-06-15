import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

final authSessionProvider = Provider<AuthSession>((ref) {
  final session = AuthSession(FlutterSecureStorage());
  // ref.onDispose(session.clearCache);
  return session;
});

class AuthSession {
  AuthSession(this._storage);

  final FlutterSecureStorage _storage;

  String? _cachedToken;

  bool _initialized = false;

  String? get token => _cachedToken;

  Future<void> init() async {
    if (_initialized) return;

    _cachedToken = await _storage.read(key: 'token');
    _initialized = true;
  }

  Future<void> updateToken(String? token) async {
    _cachedToken = token;
    _initialized = true;

    if (token == null) {
      await _storage.delete(key: 'token');
    } else {
      await _storage.write(key: 'token', value: token);
    }
  }

  void clearCache() {
    _cachedToken = null;
    _initialized = false;
  }
}
