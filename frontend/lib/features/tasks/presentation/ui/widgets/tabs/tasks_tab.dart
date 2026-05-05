import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/tasks/presentation/ui/widgets/cards/task_card.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/data/providers/tasks_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/tab_wrapper.dart';

class TasksTab extends ConsumerWidget {
  const TasksTab({super.key});

  Future<void> _refresh(WidgetRef ref, int apartmentId) async {
    await ref.read(tasksProvider.notifier).refresh();
  }

  Future<void> switchStatus(WidgetRef ref, int apartmentId, int taskId) async {
    ref
        .read(tasksProvider.notifier)
        .switchTaskStatus(taskId, ref.read(authProvider).value!.user!.profile!);
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    int apartmentId = ref.read(authProvider).value!.user!.profile!.apartmentId;
    final tasksState = ref.watch(tasksProvider);

    return RefreshIndicator(
      onRefresh: () => _refresh(ref, apartmentId),
      child: TabWrapper(
        appBarExists: false,
        floatingButtonExists: true,
        children: [
          const Text(
            "Задачи",
            style: TextStyle(fontSize: 20, fontWeight: .w500),
          ),
          tasksState.when(
            data: (tasks) => Column(
              children: [
                ...tasks.map(
                  (t) => TaskCard(
                    task: t,
                    onStatusSwitch: () => switchStatus(ref, apartmentId, t.id),
                  ),
                ),
              ],
            ),
            error: (e, _) => Text("При загрузке произошла ошибка"),
            loading: () =>
                Center(child: Center(child: CircularProgressIndicator())),
          ),
        ],
      ),
    );
  }
}
