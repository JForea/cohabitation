import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/models/task.dart';
import 'package:frontend/shared/data/network/dio_client.dart';

final tasksProvider = AsyncNotifierProvider<_TasksNotifier, List<Task>>(
  _TasksNotifier.new,
);

class _TasksNotifier extends AsyncNotifier<List<Task>> {
  int _page = 0;
  bool _hasMore = true;
  bool _isLoading = false;

  static const _pageSize = 20;

  @override
  Future<List<Task>> build() async {
    _page = 0;
    _hasMore = true;

    final tasks = await _fetchPage(_page);
    return tasks;
  }

  Future<List<Task>> _fetchPage(int page) async {
    final response = await AppDio.dio.get("/tasks?page=$_page&size=$_pageSize");

    final data = response.data as List;

    return data.map((taskJson) => Task.fromJson(taskJson)).toList();
  }

  Future<void> loadMore() async {
    if (_isLoading || !_hasMore) return;

    _isLoading = true;

    final current = state.value ?? [];

    final nextPage = _page + 1;

    try {
      final newTasks = await _fetchPage(nextPage);

      if (newTasks.length < _pageSize) {
        _hasMore = false;
      }

      _page = nextPage;

      state = AsyncData([...current, ...newTasks]);
    } catch (e, st) {
      state = AsyncError(e, st);
    } finally {
      _isLoading = false;
    }
  }

  Future<void> refresh() async {
    _page = 0;
    _hasMore = true;

    state = const AsyncLoading();

    state = await AsyncValue.guard(() async {
      return _fetchPage(0);
    });
  }
}
