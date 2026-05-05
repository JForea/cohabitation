import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/models/user.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';

final userProvider = Provider<User?>((ref) {
  final auth = ref.watch(authProvider);
  print("user changed.");
  return auth.value?.user;
});
