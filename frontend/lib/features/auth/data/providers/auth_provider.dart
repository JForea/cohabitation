import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/auth/data/models/auth_state.dart';
import 'package:frontend/features/auth/data/notifier/auth_notifier.dart';

final authProvider = NotifierProvider<AuthNotifier, AuthState>(
  () => AuthNotifier(),
);
