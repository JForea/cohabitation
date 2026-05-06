import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:frontend/features/profile/presentation/ui/widgets/indicators/points_indicator.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/ui/widgets/avatars/avatar.dart';

class NeighboursTopListTile extends StatelessWidget {
  const NeighboursTopListTile({
    super.key,
    required this.position,
    required this.profile,
    required this.isActiveUser,
  });

  final Profile profile;
  final int position;
  final bool isActiveUser;

  Widget _getPlaceIndicator(BuildContext context) {
    final svgSize = 20.0;

    return switch (position) {
      1 => SvgPicture.asset(
        "assets/icons/top/first.svg",
        width: svgSize,
        height: svgSize,
      ),
      2 => SvgPicture.asset(
        "assets/icons/top/second.svg",
        width: svgSize,
        height: svgSize,
      ),
      3 => SvgPicture.asset(
        "assets/icons/top/third.svg",
        width: svgSize,
        height: svgSize,
      ),
      _ => SizedBox(
        width: svgSize,
        child: Text(
          "#$position",
          textAlign: .center,
          style: TextStyle(
            color: Theme.of(context).colorScheme.onSurfaceVariant,
            fontSize: 10,
            fontWeight: .w600,
          ),
        ),
      ),
    };
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: .symmetric(vertical: 8, horizontal: 15),
      width: .infinity,
      decoration: BoxDecoration(
        color: isActiveUser ? AppColors.blue.withAlpha(37) : Colors.transparent,
      ),
      child: Row(
        children: [
          _getPlaceIndicator(context),
          SizedBox(width: 16),
          Avatar(name: profile.name, size: 32, color: profile.color),
          SizedBox(width: 12),
          Text(
            "${profile.name}${isActiveUser ? " (вы)" : ""}",
            style: TextStyle(
              color: isActiveUser
                  ? AppColors.blue
                  : Theme.of(context).colorScheme.onSurface,
              fontSize: 14,
              fontWeight: .w500,
            ),
          ),
          Spacer(),
          PointsIndicator(pointsCount: profile.points),
        ],
      ),
    );
  }
}
