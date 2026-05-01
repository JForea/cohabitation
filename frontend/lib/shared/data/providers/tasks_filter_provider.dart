import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/types/task_filter.dart';

final tasksFilterProvider = NotifierProvider<_TasksFilterNotifier, TaskFilter>(
  _TasksFilterNotifier.new,
);

class _TasksFilterNotifier extends Notifier<TaskFilter> {
  @override
  TaskFilter build() => TaskFilter.all;

  void setFilter(TaskFilter filter) => state = filter;
}
