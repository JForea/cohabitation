import 'package:frontend/shared/data/entities/profile.dart';

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
  final Profile? profile;
}
