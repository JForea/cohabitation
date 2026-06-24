import 'package:frontend/shared/domain/models/profile/profile.dart';

class CreateApartmentResponse {
  CreateApartmentResponse({
    required this.id,
    required this.budget,
    required this.createdAt,
    required this.profile,
  });

  factory CreateApartmentResponse.fromJson(Map<String, dynamic> json) {
    return CreateApartmentResponse(
      id: json["id"] as int,
      budget: json["budget"] as int,
      createdAt: DateTime.parse(json["createdAt"]),
      profile: Profile.fromJson(json["profile"]),
    );
  }

  final int id;
  final DateTime createdAt;
  final int budget;
  final Profile profile;
}
