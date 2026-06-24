import 'package:frontend/shared/domain/models/apartment.dart';
import 'package:frontend/shared/domain/models/profile/profile.dart';

class JoinApartmentResponse {
  const JoinApartmentResponse({required this.apartment, required this.profile});

  factory JoinApartmentResponse.fromJson(Map<String, dynamic> json) {
    return JoinApartmentResponse(
      apartment: Apartment.fromJson(json),
      profile: Profile.fromJson(json["profile"]),
    );
  }

  final Apartment apartment;
  final Profile profile;
}
