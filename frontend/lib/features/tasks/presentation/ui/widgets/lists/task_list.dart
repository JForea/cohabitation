import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/ui/widgets/cards/task_card.dart';
import 'package:frontend/shared/data/models/task.dart';

class TaskList extends StatelessWidget {
  const TaskList({
    super.key,
    required this.tasks,
    required this.onSwitchStatus,
    this.onSelect,
    this.onSelectCancel,
    this.selected,
  });

  final List<Task> tasks;
  final Set<int>? selected;
  final void Function(Task task) onSwitchStatus;
  final void Function(int)? onSelect;
  final void Function(int)? onSelectCancel;

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      itemCount: tasks.length,
      itemBuilder: (context, index) {
        final task = tasks[index];

        return Padding(
          padding: const EdgeInsets.only(bottom: 20),
          child: TaskCard(
            key: ValueKey(task.id),
            task: task,
            selectionMode: selected?.isNotEmpty,
            onStatusSwitch: onSwitchStatus,
            onSelect: onSelect,
            onSelectCancel: onSelectCancel,
          ),
        );
      },
    );
  }
}
