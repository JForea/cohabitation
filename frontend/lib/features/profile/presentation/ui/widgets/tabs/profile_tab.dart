import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/profile/presentation/ui/widgets/app_bars/profile_app_bar.dart';
import 'package:frontend/features/profile/presentation/ui/widgets/lists/neighbours_top_list.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/data/providers/neighbours_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/tab_wrapper.dart';

class ProfileTab extends ConsumerWidget {
  const ProfileTab({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authProvider);
    final neighboursState = ref.watch(neighboursProvider);

    final profile = authState.value!.user!.profile!;

    return Column(
      children: [
        ProfileAppBar(profile: profile),
        TabWrapper(
          floatingButtonExists: false,
          appBarExists: true,
          children: [
            neighboursState.when(
              data: (neighbours) => NeighboursTopList(
                userProfile: profile,
                neighbours: neighbours,
              ),
              error: (e, _) => Text("Произошла ошибка."),
              loading: () => Center(child: CircularProgressIndicator()),
            ),
          ],
        ),
      ],
    );
  }
}
