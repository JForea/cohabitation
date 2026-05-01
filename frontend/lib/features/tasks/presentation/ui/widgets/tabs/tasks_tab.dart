import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/tasks/presentation/ui/widgets/cards/task_card.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/data/providers/tasks_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/tab_wrapper.dart';

class TasksTab extends ConsumerStatefulWidget {
  const TasksTab({super.key});

  @override
  ConsumerState<ConsumerStatefulWidget> createState() => _TasksTabState();
}

class _TasksTabState extends ConsumerState<TasksTab> {
  @override
  Widget build(BuildContext context) {
    final tasks = ref.watch(
      tasksProvider(ref.read(authProvider).user!.profile!.apartmentId),
    );

    return TabWrapper(
      floatingButtonExists: true,
      children: [
        const Text("Задачи", style: TextStyle(fontSize: 20, fontWeight: .w600)),
        ...?tasks.value?.map((t) => TaskCard(task: t)),
      ],
    );
  }
}
