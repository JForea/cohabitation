import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/dtos/repeat_rule_dto.dart';
import 'package:frontend/core/failures/failures.dart';
import 'package:frontend/core/failures/map_dio_exception.dart';
import 'package:frontend/shared/domain/models/profile/profile.dart';
import 'package:frontend/shared/domain/models/profile/profile_base.dart';
import 'package:frontend/shared/domain/models/task.dart';
import 'package:frontend/core/network/api_client.dart';
import 'package:frontend/core/network/api_client_provider.dart';
import 'package:frontend/shared/domain/types/room.dart';
import 'package:frontend/shared/domain/types/task_priority.dart';
import 'package:frontend/core/utils/util_functions.dart';
import 'package:intl/intl.dart';

final taskRepositoryProvider = Provider<TaskRepository>((ref) {
  final apiClient = ref.read(apiClientProvider);

  return TaskRepository(apiClient);
});

class TaskRepository {
  TaskRepository(this._apiClient);

  final ApiClient _apiClient;

  String _baseUrl(int apartmentId) {
    return "/apartments/$apartmentId/tasks";
  }

  Future<List<Task>> getPage({
    required int apartmentId,
    required int page,
    required int pageSize,
    int? assignedTo,
    bool? done,
    DateTime? dueTime,
  }) async {
    try {
      final query = {
        'page': '$page',
        'size': '$pageSize',
        if (assignedTo != null) 'assignedTo': '$assignedTo',
        if (done != null) 'done': '$done',
        if (dueTime != null)
          'dueTime': DateFormat("yyyy-MM-dd").format(dueTime),
      };

      final response = await _apiClient.get(
        _baseUrl(apartmentId),
        queryParameters: query,
      );

      try {
        final data = response as List;

        return data.map((taskJson) => Task.fromJson(taskJson)).toList();
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<Task> save({
    required int apartmentId,
    int? taskId,
    required Profile createdBy,
    required String name,
    String? description,
    required List<ProfileBase> assignedTo,
    required bool autoAssign,
    required Room room,
    required TaskPriority priority,
    int? dueDateOffset,
    required int points,
    RepeatRuleDto? repeatRule,
  }) async {
    try {
      final date = dueDateOffset == null
          ? null
          : DateTime.now().add(Duration(days: dueDateOffset));

      final repeatable = repeatRule != null;

      final data = {
        "name": name,
        "description": description,
        "assignedTo": repeatable || autoAssign || assignedTo.isEmpty
            ? null
            : assignedTo[0].id,
        "autoAssign": repeatable ? false : autoAssign,
        "room": UtilFunctions.tValueToStringRequest(room),
        "priority": UtilFunctions.tValueToStringRequest(priority),
        "dueDate": date == null
            ? null
            : UtilFunctions.dateToStringRequest(date),
        "points": points,
        "repeatRule": repeatable
            ? {
                "intervalDays": repeatRule.intervalDays,
                "endDate": repeatRule.endDate == null
                    ? null
                    : UtilFunctions.dateToStringRequest(repeatRule.endDate!),
                "assignedIds": assignedTo.map((p) => p.id).toList(),
              }
            : null,
      };

      dynamic response = taskId == null
          ? await _apiClient.post(_baseUrl(apartmentId), data: data)
          : await _apiClient.put(
              "${_baseUrl(apartmentId)}/$taskId",
              data: data,
            );

      try {
        final assignedProfile = response != "" && response["assignedTo"] != null
            ? ProfileBase.fromJson(response["assignedTo"])
            : (!autoAssign && assignedTo.isNotEmpty && !repeatable
                  ? assignedTo[0]
                  : null);

        return Task(
          id: taskId ?? response["id"],
          createdBy: createdBy,
          name: name,
          description: description,
          assignedTo: assignedProfile,
          room: room,
          priority: priority,
          dueDate: date,
          points: points,
        );
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<void> switchTaskStatus(
    int apartmentId,
    int taskId,
    Profile userProfile,
  ) async {
    try {
      await _apiClient.patch("${_baseUrl(apartmentId)}/$taskId");
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<void> deleteMany(int apartmentId, List<int> ids) async {
    try {
      await _apiClient.delete(_baseUrl(apartmentId), data: ids);
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }
}
