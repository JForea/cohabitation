class Apartment {
  Apartment({
    this.address,
    required this.budget,
    required this.currentExpenseSum,
    required this.id,
    required this.name,
    this.inviteCode,
  });

  factory Apartment.fromJson(Map<String, dynamic> json) {
    return Apartment(
      address: json["address"] as String?,
      budget: json["budget"] as int,
      currentExpenseSum: json["currentExpenseSum"] as int,
      id: json["id"] as int,
      name: json["name"] as String,
      inviteCode: json["inviteCode"] as String?,
    );
  }

  final int id;
  final String name;
  final String? address;
  final int budget;
  final int currentExpenseSum;
  final String? inviteCode;

  Apartment copyWith({
    int? id,
    String? name,
    String? address,
    int? budget,
    int? currentExpenseSum,
    String? inviteCode,
  }) {
    return Apartment(
      budget: budget ?? this.budget,
      currentExpenseSum: currentExpenseSum ?? this.currentExpenseSum,
      id: id ?? this.id,
      name: name ?? this.name,
      address: address ?? this.address,
      inviteCode: inviteCode ?? this.inviteCode,
    );
  }

  @override
  String toString() {
    return "id: $id";
  }
}
