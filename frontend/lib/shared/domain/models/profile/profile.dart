import 'package:flutter/material.dart';
import 'package:frontend/shared/domain/models/profile/profile_base.dart';
import 'package:frontend/shared/domain/types/role.dart';
import 'package:frontend/core/utils/util_functions.dart';

class Profile extends ProfileBase {
  Profile({
    required super.id,
    required super.name,
    required super.color,
    required this.points,
    required this.apartmentId,
    required this.role,
    required this.monthlyExpensesAmount,
  });

  factory Profile.fromJson(Map<String, dynamic> json) {
    return Profile(
      id: json['id'] as int,
      name: json['name'] as String,
      color: UtilFunctions.getTValueFromName<Color>(
        json['avatarColor'] as String,
      ),
      points: json['points'] as int,
      apartmentId: json['apartmentId'] as int,
      role: UtilFunctions.getTValueFromName<Role>(json['role'] as String),
      monthlyExpensesAmount: json["monthlyExpensesAmount"] as int,
    );
  }

  final int points;
  final int apartmentId;
  final Role role;
  final int monthlyExpensesAmount;

  @override
  Profile copyWith({
    int? id,
    String? name,
    int? points,
    int? apartmentId,
    Role? role,
    Color? color,
    int? monthlyExpensesAmount,
  }) {
    return Profile(
      id: id ?? this.id,
      name: name ?? this.name,
      points: points ?? this.points,
      apartmentId: apartmentId ?? this.apartmentId,
      role: role ?? this.role,
      color: color ?? this.color,
      monthlyExpensesAmount:
          monthlyExpensesAmount ?? this.monthlyExpensesAmount,
    );
  }
}
