import 'package:frontend/shared/data/models/profile/profile.dart';

class User {
  User({
    required this.id,
    required this.email,
    required this.name,
    required this.profile,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'] as int,
      email: json['email'] as String,
      name: json['name'] as String,
      profile: json['profile'] != null
          ? Profile.fromJson(json['profile'] as Map<String, dynamic>)
          : null,
    );
  }

  final int id;
  final String email;
  final String name;
  Profile? profile;

  User copyWith({
    int? id,
    String? email,
    String? name,
    Profile? profile,
    bool? clearProfile,
  }) {
    return User(
      id: id ?? this.id,
      email: email ?? this.email,
      name: name ?? this.name,
      profile: clearProfile != null && clearProfile
          ? null
          : (profile ?? this.profile),
    );
  }
}
