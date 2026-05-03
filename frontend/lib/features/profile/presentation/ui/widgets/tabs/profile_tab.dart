import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/profile/presentation/ui/widgets/app_bars/profile_app_bar.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';

class ProfileTab extends ConsumerWidget {
  const ProfileTab({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final profile = ref.read(authProvider).user!.profile!;

    return Column(children: [ProfileAppBar(profile: profile)]);
  }
}
