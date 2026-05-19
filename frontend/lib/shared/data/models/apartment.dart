class Apartment {
  Apartment({
    this.address,
    required this.budget,
    required this.id,
    required this.name,
    this.inviteCode,
    required this.createdAt,
  });

  factory Apartment.fromJson(Map<String, dynamic> json) {
    return Apartment(
      address: json["address"] as String?,
      budget: json["budget"] as int,
      id: json["id"] as int,
      name: json["name"] as String,
      inviteCode: json["inviteCode"] as String?,
      createdAt: DateTime.parse(json["createdAt"]),
    );
  }

  final int id;
  final String name;
  final String? address;
  final int budget;
  final String? inviteCode;
  final DateTime createdAt;

  Apartment copyWith({
    int? id,
    String? name,
    String? address,
    int? budget,
    String? inviteCode,
    DateTime? createdAt,
    bool? clearInviteCode,
  }) {
    return Apartment(
      budget: budget ?? this.budget,
      id: id ?? this.id,
      name: name ?? this.name,
      address: address ?? this.address,
      inviteCode: clearInviteCode != null && clearInviteCode
          ? null
          : inviteCode ?? this.inviteCode,
      createdAt: createdAt ?? this.createdAt,
    );
  }
}
