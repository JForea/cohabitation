import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/tasks/presentation/ui/widgets/lists/task_list.dart';
import 'package:frontend/shared/data/models/task.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/providers/async_user_provider.dart';
import 'package:frontend/shared/data/providers/selected_provider.dart';
import 'package:frontend/shared/data/providers/tasks_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/other/empty_message_widget.dart';
import 'package:frontend/shared/presentation/ui/widgets/snack_bars/message_snack_bar.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/tab_wrapper.dart';

class TasksTab extends ConsumerStatefulWidget {
  const TasksTab({super.key});

  @override
  ConsumerState<ConsumerStatefulWidget> createState() => _TasksTabState();
}

class _TasksTabState extends ConsumerState<TasksTab> {
  late final ScrollController _scrollController;

  late final String _selectedKey;

  Future<void> _refresh(WidgetRef ref, int apartmentId) async {
    await ref.read(tasksProvider.notifier).refresh();
    ref.invalidate(selectedProvider(_selectedKey));
  }

  Future<void> switchStatus(
    BuildContext context,
    WidgetRef ref,
    int apartmentId,
    Task task,
  ) async {
    late final currentTaskDone = task.completedBy != null;

    try {
      await ref
          .read(tasksProvider.notifier)
          .switchTaskStatus(
            task.id,
            ref.read(asyncUserProvider).value!.profile!,
          );

      ref
          .read(asyncUserProvider.notifier)
          .updatePoints(currentTaskDone ? -task.points : task.points);
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          MessageSnackBar(message: "Произошла ошибка", error: true),
        );
      }
    }
  }

  void onSelect(WidgetRef ref, int id) {
    ref.read(selectedProvider(_selectedKey).notifier).select(id);
  }

  void onSelectCancel(WidgetRef ref, int id) {
    ref.read(selectedProvider(_selectedKey).notifier).selectCancel(id);
  }

  void onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      ref.read(tasksProvider.notifier).loadMore();
    }
  }

  @override
  void initState() {
    super.initState();

    _selectedKey = "tasks";
    _scrollController = ScrollController();
    _scrollController.addListener(onScroll);
  }

  @override
  Widget build(BuildContext context) {
    int apartmentId = ref.watch(apartmentProvider.select((a) => a!.id));
    final selectedTasks = ref.watch(selectedProvider(_selectedKey));
    final tasksState = ref.watch(tasksProvider);

    return RefreshIndicator(
      onRefresh: () => _refresh(ref, apartmentId),
      child: TabWrapper(
        appBarExists: false,
        scrollController: _scrollController,
        floatingButtonExists: true,
        children: [
          const Text(
            "Задачи",
            style: TextStyle(fontSize: 20, fontWeight: .w500),
          ),
          tasksState.when(
            data: (tasks) => tasks.isEmpty
                ? Center(
                    child: EmptyMessageWidget(
                      iconSize: 70,
                      fontSize: 16,
                      assetPath: "assets/icons/task_list.svg",
                      message: "Нет активных задач",
                    ),
                  )
                : TaskList(
                    tasks: tasks,
                    selected: selectedTasks,
                    onSwitchStatus: (t) =>
                        switchStatus(context, ref, apartmentId, t),
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
