import 'package:frontend/shared/data/models/profile.dart';
import 'package:frontend/shared/data/types/buying_category.dart';
import 'package:frontend/shared/utils/util_functions.dart';

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
      createdBy: Profile.fromJson(json["createdBy"]),
      assignedTo: json["assignedTo"] != null
          ? Profile.fromJson(json["assignedTo"])
          : null,
      completedBy: json["completedBy"] != null
          ? Profile.fromJson(json["completedBy"])
          : null,
      name: json["name"] as String,
      quantity: json["quantity"] as String,
      category: UtilFunctions.getTValueFromName<BuyingCategory>(
        json["category"],
      ),
    );
  }

  final int id;
  final Profile createdBy;
  final Profile? assignedTo;
  final Profile? completedBy;
  final String name;
  final String quantity;
  final BuyingCategory category;
}
