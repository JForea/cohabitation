import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/models/profile.dart';
import 'package:frontend/shared/data/models/task.dart';
import 'package:frontend/shared/data/network/dio_client.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/types/role.dart';
import 'package:frontend/shared/data/types/room.dart';
import 'package:frontend/shared/data/types/task_filter.dart';
import 'package:frontend/shared/data/types/task_priority.dart';
import 'package:frontend/shared/utils/util_functions.dart';

final tasksProvider = AsyncNotifierProvider<_TasksNotifier, List<Task>>(
  _TasksNotifier.new,
);

class _TasksNotifier extends AsyncNotifier<List<Task>> {
  late String baseUrl;

  static const _pageSize = 20;

  int _page = 0;
  bool _hasMore = true;
  bool _isLoading = false;

  TaskFilter _filter = const TaskFilter();

  @override
  Future<List<Task>> build() async {
    final apartment = ref.watch(apartmentProvider);

    if (apartment == null) {
      throw Exception("Not in apartment.");
    }

    baseUrl = "/apartments/${apartment.id}/tasks";

    return _fetchPage();
  }

  Future<List<Task>> _fetchPage() async {
    final query = {
      'page': '$_page',
      'size': '$_pageSize',
      if (_filter.assignedTo != null) 'assignedTo': '${_filter.assignedTo}',
      if (_filter.done != null) 'done': '${_filter.done}',
    };

    final response = await AppDio.dio.get(baseUrl, queryParameters: query);

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

  Future<bool> create({
    required Profile userProfile,
    required String name,
    String? description,
    Profile? assignedTo,
    required Room room,
    required TaskPriority priority,
    int? dueDateOffset,
    required int points,
  }) async {
    print(dueDateOffset);

    if (_isLoading) {
      return false;
    }

    try {
      _isLoading = true;

      final date = dueDateOffset == null
          ? null
          : DateTime.now().add(Duration(days: dueDateOffset));
      final response = await AppDio.dio.post(
        baseUrl,
        data: {
          "name": name,
          "description": description == "" ? null : description,
          "assignedTo": assignedTo?.id,
          "room": UtilFunctions.tValueToStringRequest(room),
          "priority": UtilFunctions.tValueToStringRequest(priority),
          "dueDate": date == null
              ? null
              : UtilFunctions.dateToStringRequest(date),
          "points": points,
        },
      );

      final task = Task(
        id: response.data["id"] as int,
        createdBy: userProfile,
        name: name,
        description: description,
        assignedTo: assignedTo,
        room: room,
        priority: priority,
        dueDate: date,
        points: points,
      );

      state = AsyncData([task, ...?state.value]);

      _isLoading = false;

      return true;
    } catch (e) {
      print(e);
      _isLoading = false;
      return false;
    }
  }

  Future<bool> switchTaskStatus(int taskId, Profile userProfile) async {
    final previous = state.value ?? [];

    try {
      bool ok = true;

      final updated = previous.map((t) {
        if (t.id == taskId) {
          if (t.completedBy != null) {
            if (t.completedBy!.id == userProfile.id ||
                userProfile.role != Role.inhabitant) {
              return t.copyWith(clearCompletedBy: true);
            } else {
              ok = false;
            }
          } else if (t.completedBy == null) {
            return t.copyWith(completedBy: userProfile);
          }
        }

        return t;
      }).toList();

      if (!ok) {
        return false;
      }

      state = AsyncData(updated);

      _isLoading = true;

      await AppDio.dio.patch("$baseUrl/$taskId");

      _isLoading = false;

      return true;
    } catch (e) {
      state = AsyncData(previous);
      _isLoading = false;

      return false;
    }
  }
}
