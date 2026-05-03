import 'package:frontend/shared/data/models/profile.dart';

class CreateApartmentResponse {
  CreateApartmentResponse({
    required this.id,
    required this.budget,
    required this.profile,
  });

  factory CreateApartmentResponse.fromJson(Map<String, dynamic> json) {
    return CreateApartmentResponse(
      id: json["id"] as int,
      budget: json["budget"] as int,
      profile: Profile.fromJson(json["profile"]),
    );
  }

  final int id;
  final int budget;
  final Profile profile;
}
