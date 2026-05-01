import 'package:frontend/shared/data/models/profile.dart';
import 'package:frontend/shared/data/types/room.dart';
import 'package:frontend/shared/data/types/task_priority.dart';
import 'package:frontend/shared/utils/get_t_value_from_name.dart';

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
    this.dueTime,
  });

  factory Task.fromJson(Map<String, dynamic> json) {
    return Task(
      id: json["id"] as int,
      createdBy: Profile.fromJson(json["createdBy"]),
      assignedTo: json["assignedTo"] != null
          ? Profile.fromJson(json["assignedTo"])
          : null,
      completedBy: json["completedBy"] != null
          ? Profile.fromJson(json["completedBy"])
          : null,
      name: json["name"] as String,
      description: json["description"] as String?,
      room: getTValueFromName<Room>(json["room"] as String),
      priority: getTValueFromName<TaskPriority>(json["priority"] as String),
      points: json["points"] as int,
      dueTime: json["dueTime"] != null
          ? DateTime.parse(json["dueTime"] as String)
          : null,
    );
  }

  final int id;
  final Profile createdBy;
  final Profile? assignedTo;
  final Profile? completedBy;
  final String name;
  final String? description;
  final Room room;
  final TaskPriority priority;
  final int points;
  final DateTime? dueTime;
}
