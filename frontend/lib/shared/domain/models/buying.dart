import 'package:frontend/shared/data/models/profile/profile_brief.dart';
import 'package:frontend/shared/data/types/buying_category.dart';
import 'package:frontend/core/utils/util_functions.dart';

class Buying {
  Buying({
    required this.id,
    required this.createdBy,
    this.assignedTo,
    this.completedBy,
    required this.name,
    required this.quantity,
    required this.category,
  });

  factory Buying.fromJson(Map<String, dynamic> json) {
    return Buying(
      id: json["id"] as int,
      createdBy: ProfileBrief.fromJson(json["createdBy"]),
      assignedTo: json["assignedTo"] != null
          ? ProfileBrief.fromJson(json["assignedTo"])
          : null,
      completedBy: json["completedBy"] != null
          ? ProfileBrief.fromJson(json["completedBy"])
          : null,
      name: json["name"] as String,
      quantity: json["quantity"] as String,
      category: UtilFunctions.getTValueFromName<BuyingCategory>(
        json["category"],
      ),
    );
  }

  final int id;
  final ProfileBrief createdBy;
  final ProfileBrief? assignedTo;
  final ProfileBrief? completedBy;
  final String name;
  final String quantity;
  final BuyingCategory category;

  Buying copyWith({
    int? id,
    ProfileBrief? createdBy,
    ProfileBrief? assignedTo,
    ProfileBrief? completedBy,
    String? name,
    String? quantity,
    BuyingCategory? category,
    bool clearCompletedBy = false,
  }) {
    return Buying(
      id: id ?? this.id,
      createdBy: createdBy ?? this.createdBy,
      assignedTo: assignedTo ?? this.assignedTo,
      completedBy: clearCompletedBy ? null : (completedBy ?? this.completedBy),
      name: name ?? this.name,
      quantity: quantity ?? this.quantity,
      category: category ?? this.category,
    );
  }
}
