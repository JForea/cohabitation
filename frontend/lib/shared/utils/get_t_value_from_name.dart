import 'package:flutter/material.dart';
import 'package:frontend/shared/data/types/role.dart';
import 'package:frontend/shared/data/types/room.dart';
import 'package:frontend/shared/data/types/task_priority.dart';

T getTValueFromName<T>(String name) {
  name = name.toLowerCase();
  dynamic res;
  switch (T) {
    case const (Color):
      res = switch (name) {
        "blue" => const Color(0xFF6C63FF),
        "green" => const Color(0xFF3EC98E),
        "orange" => const Color(0xFFFF7854),
        "yellow" => const Color(0xFFFFC107),
        "purple" => const Color(0xFFE155F6),
        "brown" => const Color(0xFF875B50),
        _ => Colors.black,
      };
      break;

    case const (Role):
      res = switch (name) {
        "inhabitant" => Role.inhabitant,
        "admin" => Role.admin,
        "creator" => Role.creator,
        _ => null,
      };
      break;

    case const (Room):
      res = switch (name) {
        "common" => Room.common,
        "kitchen" => Room.kitchen,
        "living_room" => Room.livingRoom,
        "bathroom" => Room.bathroom,
        "bedroom" => Room.bedroom,
        "corridor" => Room.corridor,
        _ => null,
      };
      break;

    case const (TaskPriority):
      res = switch (name) {
        "low" => TaskPriority.low,
        "medium" => TaskPriority.medium,
        "high" => TaskPriority.high,
        _ => null,
      };
      break;

    default:
      throw UnsupportedError("Provided type $T is not supported.");
  }

  if (res == null) {
    throw UnsupportedError("Unknown name value '$name' for type $T.");
  }
  return res as T;
}
