import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/models/user.dart';
import 'package:frontend/shared/data/providers/async_user_provider.dart';

final userProvider = Provider<User?>((ref) {
  return ref.watch(asyncUserProvider.select((s) => s.value));
});
