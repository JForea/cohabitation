import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/models/auth_state.dart';
import 'package:frontend/shared/data/notifiers/auth_notifier.dart';

final authProvider = NotifierProvider<AuthNotifier, AuthState>(
  () => AuthNotifier(),
);
