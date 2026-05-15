import 'package:frontend/shared/data/models/profile/profile_brief.dart';
import 'package:frontend/shared/data/types/expense_category.dart';
import 'package:frontend/shared/utils/util_functions.dart';

class Expense {
  const Expense({
    required this.id,
    required this.category,
    this.checkImageUrl,
    required this.name,
    required this.createdBy,
    required this.amount,
    required this.createdAt,
  });

  factory Expense.fromJson(Map<String, dynamic> json) {
    return Expense(
      id: json["id"] as int,
      category: UtilFunctions.getTValueFromName<ExpenseCategory>(
        json["category"] as String,
      ),
      checkImageUrl: json["checkImageUrl"] as String?,
      name: json["name"] as String,
      createdBy: ProfileBrief.fromJson(json["createdBy"]),
      amount: json["amount"] as int,
      createdAt: DateTime.parse(json["createdAt"] as String),
    );
  }

  final int id;
  final String name;
  final int amount;
  final ExpenseCategory category;
  final String? checkImageUrl;
  final ProfileBrief createdBy;
  final DateTime createdAt;

  Expense copyWith({
    int? id,
    String? name,
    int? amount,
    ExpenseCategory? category,
    String? checkImageUrl,
    ProfileBrief? createdBy,
    DateTime? createdAt,
    bool? clearCheckImageUrl,
  }) {
    return Expense(
      category: category ?? this.category,
      id: id ?? this.id,
      name: name ?? this.name,
      amount: amount ?? this.amount,
      checkImageUrl: clearCheckImageUrl == null || clearCheckImageUrl
          ? null
          : (checkImageUrl ?? this.checkImageUrl),
      createdBy: createdBy ?? this.createdBy,
      createdAt: createdAt ?? this.createdAt,
    );
  }
}
