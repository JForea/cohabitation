import 'package:frontend/shared/domain/models/profile/profile_base.dart';
import 'package:frontend/shared/domain/types/room.dart';
import 'package:frontend/shared/domain/types/task_priority.dart';
import 'package:frontend/core/utils/util_functions.dart';

class Task {
  Task({
    required this.id,
    required this.createdBy,
    this.assignedTo,
    this.completedBy,
    required this.name,
    this.description,
    required this.room,
    required this.priority,
    required this.points,
    this.dueDate,
  });

  factory Task.fromJson(Map<String, dynamic> json) {
    return Task(
      id: json["id"] as int,
      createdBy: ProfileBase.fromJson(json["createdBy"]),
      assignedTo: json["assignedTo"] != null
          ? ProfileBase.fromJson(json["assignedTo"])
          : null,
      completedBy: json["completedBy"] != null
          ? ProfileBase.fromJson(json["completedBy"])
          : null,
      name: json["name"] as String,
      description: json["description"] as String?,
      room: UtilFunctions.getTValueFromName<Room>(json["room"] as String),
      priority: UtilFunctions.getTValueFromName<TaskPriority>(
        json["priority"] as String,
      ),
      points: json["points"] as int,
      dueDate: json["dueDate"] != null
          ? DateTime.parse(json["dueDate"] as String)
          : null,
    );
  }

  final int id;
  final ProfileBase createdBy;
  final ProfileBase? assignedTo;
  final ProfileBase? completedBy;
  final String name;
  final String? description;
  final Room room;
  final TaskPriority priority;
  final int points;
  final DateTime? dueDate;

  Task copyWith({
    int? id,
    ProfileBase? createdBy,
    ProfileBase? assignedTo,
    ProfileBase? completedBy,
    String? name,
    String? description,
    Room? room,
    TaskPriority? priority,
    int? points,
    DateTime? dueDate,
    bool clearCompletedBy = false,
  }) {
    return Task(
      id: id ?? this.id,
      createdBy: createdBy ?? this.createdBy,
      assignedTo: assignedTo ?? this.assignedTo,
      completedBy: clearCompletedBy ? null : (completedBy ?? this.completedBy),
      name: name ?? this.name,
      description: description ?? this.description,
      room: room ?? this.room,
      priority: priority ?? this.priority,
      points: points ?? this.points,
      dueDate: dueDate ?? this.dueDate,
    );
  }
}
