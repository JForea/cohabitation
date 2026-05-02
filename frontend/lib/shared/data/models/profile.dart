import 'package:flutter/material.dart';
import 'package:frontend/shared/data/types/role.dart';
import 'package:frontend/shared/utils/util_functions.dart';

class Profile {
  Profile({
    required this.id,
    required this.name,
    required this.points,
    required this.apartmentId,
    required this.role,
    required this.color,
  });

  factory Profile.fromJson(Map<String, dynamic> json) {
    return Profile(
      id: json['id'] as int,
      name: json['name'] as String,
      points: json['points'] as int,
      apartmentId: json['apartmentId'] as int,
      role: UtilFunctions.getTValueFromName<Role>(json['role'] as String),
      color: UtilFunctions.getTValueFromName<Color>(
        json['avatarColor'] as String,
      ),
    );
  }

  final int id;
  final String name;
  final int points;
  final int apartmentId;
  final Role role;
  final Color color;
}
