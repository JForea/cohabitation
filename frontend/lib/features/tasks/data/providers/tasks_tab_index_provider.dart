import 'package:flutter_riverpod/flutter_riverpod.dart';

final tasksTabIndexProvider = NotifierProvider(TasksTabIndexNotifier.new);

class TasksTabIndexNotifier extends Notifier<int> {
  @override
  int build() => 0;

  void setIndex(int index) => state = index;
}
