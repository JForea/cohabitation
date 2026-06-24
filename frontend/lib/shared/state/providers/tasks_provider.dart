import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/dtos/repeat_rule_dto.dart';
import 'package:frontend/core/failures/failures.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/data/models/profile/profile_brief.dart';
import 'package:frontend/shared/data/models/task.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/repositories/task_repository.dart';
import 'package:frontend/shared/data/types/room.dart';
import 'package:frontend/shared/data/filters/task_filter.dart';
import 'package:frontend/shared/data/types/task_priority.dart';

final tasksProvider = AsyncNotifierProvider<_TasksNotifier, List<Task>>(
  _TasksNotifier.new,
);

class _TasksNotifier extends AsyncNotifier<List<Task>> {
  late TaskRepository _taskRepository;

  late int? _apartmentId;

  static const _pageSize = 20;

  int _page = 0;
  bool _hasMore = true;
  bool _isLoading = false;

  late TaskFilter _filter = TaskFilter();

  @override
  Future<List<Task>> build() async {
    _apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));
    _taskRepository = ref.read(taskRepositoryProvider);

    if (_apartmentId == null) throw NotInApartmentFailure();

    _page = 0;
    _hasMore = true;
    _isLoading = false;

    return _taskRepository.getPage(
      apartmentId: _apartmentId!,
      page: _page,
      pageSize: _pageSize,
      assignedTo: _filter.assignedTo,
      done: _filter.done,
    );
  }

  Future<void> loadMore() async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    if (_isLoading || !_hasMore) return;

    _isLoading = true;

    final previousValue = state.value ?? [];

    try {
      final newTasks = await _taskRepository.getPage(
        apartmentId: _apartmentId!,
        page: _page + 1,
        pageSize: _pageSize,
        assignedTo: _filter.assignedTo,
        done: _filter.done,
      );

      _page++;

      if (newTasks.length < _pageSize) {
        _hasMore = false;
      }

      state = AsyncData([...previousValue, ...newTasks]);
    } finally {
      _isLoading = false;
    }
  }

  Future<void> refresh({bool? fullRefresh}) async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    if (_isLoading) return;

    final previous = state;
    final previousPage = _page;
    final previousHasMore = _hasMore;

    try {
      _isLoading = true;

      if (fullRefresh == true) {
        state = const AsyncLoading();
      }

      final tasks = await _taskRepository.getPage(
        apartmentId: _apartmentId!,
        page: 0,
        pageSize: _pageSize,
        assignedTo: _filter.assignedTo,
        done: _filter.done,
      );

      _page = 0;
      _hasMore = tasks.length >= _pageSize;

      state = AsyncData(tasks);
    } catch (e) {
      _page = previousPage;
      _hasMore = previousHasMore;
      state = previous;
      rethrow;
    } finally {
      _isLoading = false;
    }
  }

  Future<void> setFilter({int? assignedTo, bool? done}) async {
    _filter = TaskFilter(assignedTo: assignedTo, done: done);

    await refresh(fullRefresh: true);
  }

  Future<void> create({
    required Profile userProfile,
    required String name,
    String? description,
    required List<Profile> assignedTo,
    required bool autoAssign,
    required Room room,
    required TaskPriority priority,
    int? dueDateOffset,
    required int points,
    RepeatRuleDto? repeatRule,
  }) async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    try {
      _isLoading = true;

      final task = await _taskRepository.create(
        apartmentId: _apartmentId!,
        craeatedBy: userProfile,
        name: name,
        description: description,
        assignedTo: assignedTo,
        autoAssign: autoAssign,
        room: room,
        priority: priority,
        dueDateOffset: dueDateOffset,
        points: points,
        repeatRule: repeatRule,
      );

      state = AsyncData([task, ...?state.value]);
    } finally {
      _isLoading = false;
    }
  }

  Future<void> switchTaskStatus(int taskId, Profile userProfile) async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    final previous = state.value ?? [];

    final index = previous.indexWhere((t) => t.id == taskId);

    if (index == -1) return;

    final task = previous[index];

    final updatedTask = task.completedBy == null
        ? task.copyWith(completedBy: ProfileBrief.fromFullProfile(userProfile))
        : task.copyWith(clearCompletedBy: true);

    List<Task> updated = [...previous];
    updated[index] = updatedTask;

    state = AsyncData(updated);

    try {
      await _taskRepository.switchTaskStatus(
        _apartmentId!,
        taskId,
        userProfile,
      );
    } catch (e) {
      state = AsyncData(previous);
      rethrow;
    }
  }

  Future<void> deleteMany(List<int> ids) async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    final previous = state.value;

    if (previous == null) return;

    final current = previous.where((e) => !ids.contains(e.id)).toList();

    state = AsyncData(current);

    try {
      await _taskRepository.deleteMany(_apartmentId!, ids);
    } catch (e) {
      state = AsyncData(previous);

      rethrow;
    }
  }
}
