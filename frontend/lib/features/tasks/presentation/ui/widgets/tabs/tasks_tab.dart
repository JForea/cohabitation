import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/tasks/presentation/ui/widgets/cards/task_card.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/data/providers/tasks_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/tab_wrapper.dart';

class TasksTab extends ConsumerWidget {
  const TasksTab({super.key});

  Future<void> _refresh(WidgetRef ref, int apartmentId) async {
    await ref.read(tasksProvider(apartmentId).notifier).refresh();
  }

  Future<void> switchStatus(WidgetRef ref, int apartmentId, int taskId) async {
    ref
        .read(tasksProvider(apartmentId).notifier)
        .switchTaskStatus(taskId, ref.read(authProvider).user!.profile!);
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    int apartmentId = ref.read(authProvider).user!.profile!.apartmentId;
    final tasks = ref.watch(tasksProvider(apartmentId));

    return RefreshIndicator(
      onRefresh: () => _refresh(ref, apartmentId),
      child: PageWrapper(
        floatingButtonExists: true,
        children: [
          const Text(
            "Задачи",
            style: TextStyle(fontSize: 20, fontWeight: .w600),
          ),
          ...?tasks.value?.map(
            (t) => TaskCard(
              task: t,
              onStatusSwitch: () => switchStatus(ref, apartmentId, t.id),
            ),
          ),
        ],
      ),
    );
  }
}
