import 'package:flutter/material.dart';
import 'package:frontend/core/utils/util_functions.dart';

class ProfileBase {
  ProfileBase({required this.id, required this.name, required this.color});

  factory ProfileBase.fromJson(Map<String, dynamic> json) {
    return ProfileBase(
      id: json['id'] as int,
      name: json['name'] as String,
      color: UtilFunctions.getTValueFromName<Color>(
        json['avatarColor'] as String,
      ),
    );
  }

  final int id;
  final String name;
  final Color color;

  ProfileBase copyWith({int? id, String? name, Color? color}) {
    return ProfileBase(
      id: id ?? this.id,
      name: name ?? this.name,
      color: color ?? this.color,
    );
  }
}
