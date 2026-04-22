import 'package:flutter/material.dart';
import 'package:frontend/shared/types/role.dart';
import 'package:frontend/shared/utils/get_color_from_name.dart';

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
      role: json['role'] as Role,
      color: getColorFromName(json['avatarColor'] as String),
    );
  }

  final int id;
  final String name;
  final int points;
  final int apartmentId;
  final Role role;
  final Color color;
}
