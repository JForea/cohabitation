import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/tasks/presentation/ui/widgets/lists/task_list.dart';
import 'package:frontend/shared/data/models/task.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/data/providers/selected_provider.dart';
import 'package:frontend/shared/data/providers/tasks_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/tab_wrapper.dart';

class TasksTab extends ConsumerWidget {
  const TasksTab({super.key});

  final String selectedKey = "tasks";

  Future<void> _refresh(WidgetRef ref, int apartmentId) async {
    await ref.read(tasksProvider.notifier).refresh();
    ref.invalidate(selectedProvider(selectedKey));
  }

  Future<void> switchStatus(WidgetRef ref, int apartmentId, Task task) async {
    final currentTaskDone = task.completedBy != null;

    final switched = await ref
        .read(tasksProvider.notifier)
        .switchTaskStatus(
          task.id,
          ref.read(authProvider).value!.user!.profile!,
        );

    if (switched) {
      ref
          .read(authProvider.notifier)
          .updatePoints(currentTaskDone ? -task.points : task.points);
    }
  }

  void onSelect(WidgetRef ref, int id) {
    ref.read(selectedProvider(selectedKey).notifier).select(id);
  }

  void onSelectCancel(WidgetRef ref, int id) {
    ref.read(selectedProvider(selectedKey).notifier).selectCancel(id);
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    int apartmentId = ref.watch(apartmentProvider.select((a) => a!.id));
    final selectedTasks = ref.watch(selectedProvider(selectedKey));
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
            data: (tasks) => TaskList(
              tasks: tasks,
              selected: selectedTasks,
              onSwitchStatus: (t) => switchStatus(ref, apartmentId, t),
              onSelect: (id) => onSelect(ref, id),
              onSelectCancel: (id) => onSelectCancel(ref, id),
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
