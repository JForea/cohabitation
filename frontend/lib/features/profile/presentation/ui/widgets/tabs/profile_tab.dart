import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/profile/presentation/ui/widgets/app_bars/profile_app_bar.dart';
import 'package:frontend/features/profile/presentation/ui/widgets/lists/neighbours_top_list.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/data/providers/neighbours_provider.dart';
import 'package:frontend/shared/data/providers/rules_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/lists/rule_list.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/tab_wrapper.dart';

class ProfileTab extends ConsumerWidget {
  const ProfileTab({super.key});

  Future<void> _refresh(WidgetRef ref) async {
    ref.read(neighboursProvider.notifier).refresh();
    ref.read(rulesProvider.notifier).refresh();
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final profile = ref.watch(
      authProvider.select((s) => s.value!.user!.profile!),
    );
    final neighboursState = ref.watch(neighboursProvider);
    final rulesState = ref.watch(rulesProvider);

    return RefreshIndicator(
      onRefresh: () => _refresh(ref),
      child: Column(
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
              Column(
                spacing: 8,
                crossAxisAlignment: .start,
                children: [
                  Text(
                    "Правила",
                    style: TextStyle(
                      color: Theme.of(context).colorScheme.onSurface,
                      fontSize: 16,
                      fontWeight: .w500,
                    ),
                  ),
                  rulesState.when(
                    data: (rules) => RuleList(titleNeeded: false, rules: rules),
                    error: (e, _) => Text("Произошла ошибка при загрузке."),
                    loading: () => Center(child: CircularProgressIndicator()),
                  ),
                ],
              ),
            ],
          ),
        ],
      ),
    );
  }
}
