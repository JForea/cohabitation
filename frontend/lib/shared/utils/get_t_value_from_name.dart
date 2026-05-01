import 'package:flutter/material.dart';
import 'package:frontend/shared/data/types/role.dart';
import 'package:frontend/shared/data/types/room.dart';
import 'package:frontend/shared/data/types/task_priority.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';

T getTValueFromName<T>(String name) {
  name = name.toLowerCase();
  dynamic res;
  switch (T) {
    case const (Color):
      res = switch (name) {
        "blue" => AppColors.blue,
        "green" => AppColors.green,
        "orange" => AppColors.orange,
        "yellow" => AppColors.yellow,
        "purple" => AppColors.purple,
        "brown" => AppColors.brown,
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
