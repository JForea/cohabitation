import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/models/task.dart';
import 'package:frontend/shared/data/network/dio_client.dart';
import 'package:frontend/shared/data/types/task_filter.dart';

final tasksProvider =
    AsyncNotifierProvider.family<_TasksNotifier, List<Task>, int>(
      _TasksNotifier.new,
    );

class _TasksNotifier extends AsyncNotifier<List<Task>> {
  _TasksNotifier(this.apartmentId);
  final int apartmentId;

  static const _pageSize = 20;

  int _page = 0;
  bool _hasMore = true;
  bool _isLoading = false;

  TaskFilter _filter = const TaskFilter();

  @override
  Future<List<Task>> build() async {
    _page = 0;
    _hasMore = true;

    return _fetchPage();
  }

  Future<List<Task>> _fetchPage() async {
    final query = {
      'page': '$_page',
      'size': '$_pageSize',
      if (_filter.assignedTo != null) 'assignedTo': '${_filter.assignedTo}',
      if (_filter.done != null) 'done': '${_filter.done}',
    };

    final response = await AppDio.dio.get(
      "/$apartmentId/tasks",
      queryParameters: query,
    );

    final data = response.data as List;

    return data.map((taskJson) => Task.fromJson(taskJson)).toList();
  }

  Future<void> loadMore() async {
    if (_isLoading || !_hasMore || state.isLoading) return;

    _isLoading = true;

    final previousValue = state.value ?? [];

    try {
      _page++;
      final newTasks = await _fetchPage();

      if (newTasks.length < _pageSize) {
        _hasMore = false;
      }

      state = AsyncData([...previousValue, ...newTasks]);
    } catch (e, st) {
      _page--;
      state = AsyncError<List<Task>>(e, st);
    } finally {
      _isLoading = false;
    }
  }

  Future<void> refresh() async {
    _page = 0;
    _hasMore = true;

    state = const AsyncLoading();
    state = await AsyncValue.guard(() async {
      return _fetchPage();
    });
  }

  Future<void> switchFilter({int? assignedTo, bool? done}) async {
    _filter = TaskFilter(assignedTo: assignedTo, done: done);

    await refresh();
  }
}
