class Apartment {
  Apartment({
    this.address,
    required this.budget,
    required this.id,
    required this.name,
    this.inviteCode,
  });

  factory Apartment.fromJson(Map<String, dynamic> json) {
    return Apartment(
      address: json["address"] as String?,
      budget: json["budget"] as int,
      id: json["id"] as int,
      name: json["name"] as String,
      inviteCode: json["inviteCode"] as String?,
    );
  }

  final int id;
  final String name;
  final String? address;
  final int budget;
  final String? inviteCode;

  Apartment copyWith({
    int? id,
    String? name,
    String? address,
    int? budget,
    String? inviteCode,
  }) {
    return Apartment(
      budget: budget ?? this.budget,
      id: id ?? this.id,
      name: name ?? this.name,
      address: address ?? this.address,
      inviteCode: inviteCode ?? this.inviteCode,
    );
  }
}
