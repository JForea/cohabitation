import 'package:flutter/material.dart';
import 'package:frontend/shared/domain/models/profile/profile.dart';
import 'package:frontend/shared/presentation/widgets/avatars/avatar.dart';

class ProfileList extends StatelessWidget {
  const ProfileList({
    super.key,
    required this.activeId,
    required this.onSelect,
    required this.profiles,
  });

  final List<Profile> profiles;
  final int activeId;
  final void Function(Profile) onSelect;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 40,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        itemCount: profiles.length,
        itemBuilder: (context, index) {
          final p = profiles[index];

          return Padding(
            padding: EdgeInsets.only(right: 8),
            child: Avatar(
              name: p.name,
              size: 40,
              color: p.color,
              active: p.id == activeId,
              onTap: () => onSelect(p),
            ),
          );
        },
      ),
    );
  }
}
