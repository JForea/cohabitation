import 'package:flutter/material.dart';
import 'package:frontend/shared/domain/models/profile/profile_base.dart';

class Event {
  const Event({
    required this.id,
    required this.createdBy,
    required this.name,
    this.description,
    this.time,
  });

  factory Event.fromJson(Map<String, dynamic> json) {
    return Event(
      id: json["id"],
      createdBy: ProfileBase.fromJson(json["createdBy"]),
      name: json["name"],
      time: json["time"] != null
          ? TimeOfDay(
              hour: int.parse(json["time"].split(":")[0]),
              minute: int.parse(json["time"].split(":")[1]),
            )
          : null,
      description: json["description"],
    );
  }

  final int id;
  final ProfileBase createdBy;
  final String name;
  final TimeOfDay? time;
  final String? description;

  Event copyWith({
    int? id,
    ProfileBase? createdBy,
    String? name,
    TimeOfDay? time,
    String? description,
    bool? clearTime,
    bool? clearDescription,
  }) {
    return Event(
      id: id ?? this.id,
      createdBy: createdBy ?? this.createdBy,
      name: name ?? this.name,
      time: clearTime != null && clearTime ? null : (time ?? this.time),
      description: clearDescription != null && clearDescription
          ? null
          : (description ?? this.description),
    );
  }
}
