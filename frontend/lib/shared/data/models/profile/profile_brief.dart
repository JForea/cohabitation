import 'package:flutter/material.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/utils/util_functions.dart';

class ProfileBrief {
  ProfileBrief({required this.id, required this.name, required this.color});

  factory ProfileBrief.fromJson(Map<String, dynamic> json) {
    return ProfileBrief(
      id: json['id'] as int,
      name: json['name'] as String,
      color: UtilFunctions.getTValueFromName<Color>(
        json['avatarColor'] as String,
      ),
    );
  }

  factory ProfileBrief.fromFullProfile(Profile profile) {
    return ProfileBrief(
      id: profile.id,
      name: profile.name,
      color: profile.color,
    );
  }

  final int id;
  final String name;
  final Color color;

  ProfileBrief copyWith({int? id, String? name, Color? color}) {
    return ProfileBrief(
      id: id ?? this.id,
      name: name ?? this.name,
      color: color ?? this.color,
    );
  }
}
