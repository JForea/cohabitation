import 'package:flutter/material.dart';
import 'package:frontend/features/profile/presentation/ui/widgets/list_tiles/neighbours_top_list_tile.dart';
import 'package:frontend/shared/data/models/profile.dart';
import 'package:frontend/shared/presentation/ui/widgets/lists/custom_widget_list.dart';

class NeighboursTopList extends StatelessWidget {
  const NeighboursTopList({
    super.key,
    required this.userProfile,
    required this.neighbours,
  });

  final Profile userProfile;
  final List<Profile> neighbours;

  @override
  Widget build(BuildContext context) {
    final profiles = [userProfile, ...neighbours];

    profiles.sort((first, second) => second.points - first.points);

    return CustomWidgetList(
      danger: false,
      children: List.generate(
        profiles.length,
        (i) => NeighboursTopListTile(
          position: i + 1,
          profile: profiles[i],
          isActiveUser: profiles[i].id == userProfile.id,
        ),
      ),
    );
  }
}
