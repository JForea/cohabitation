import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:frontend/shared/data/models/profile.dart';
import 'package:frontend/shared/presentation/theme/app_shadows.dart';
import 'package:frontend/shared/presentation/ui/widgets/avatars/avatar.dart';

class NeighbourPreviewCard extends StatelessWidget {
  const NeighbourPreviewCard({
    super.key,
    required this.profile,
    required this.width,
  });

  final Profile profile;
  final double width;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: .symmetric(vertical: 15),
      width: width,
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surfaceContainer,
        borderRadius: .all(.circular(20)),
        boxShadow: [AppShadows.standard()],
      ),
      child: Column(
        spacing: 8,
        children: [
          Avatar(name: profile.name, size: 32, color: profile.color),
          Text(
            profile.name,
            maxLines: 1,
            overflow: .ellipsis,
            style: TextStyle(fontSize: 14, fontWeight: .w500),
          ),
          Row(
            mainAxisSize: .min,
            spacing: 5,
            children: [
              SvgPicture.asset("icons/star.svg", width: 12, height: 12),
              Text(
                "${profile.points}",
                style: TextStyle(fontSize: 12, fontWeight: .w500),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
