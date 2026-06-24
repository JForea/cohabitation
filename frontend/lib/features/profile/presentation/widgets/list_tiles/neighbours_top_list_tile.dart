import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:frontend/features/profile/presentation/widgets/indicators/points_indicator.dart';
import 'package:frontend/shared/domain/models/profile/profile.dart';
import 'package:frontend/app/theme/app_colors.dart';
import 'package:frontend/shared/presentation/widgets/avatars/avatar.dart';
import 'package:frontend/shared/presentation/widgets/dialogs/profile_dialog.dart';

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
    final svgSize = 24.0;

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

  void _showProfileDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => ProfileDialog(profile: profile),
    );
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: isActiveUser ? null : () => _showProfileDialog(context),
      child: Container(
        padding: .symmetric(vertical: 8, horizontal: 15),
        width: .infinity,
        decoration: BoxDecoration(
          color: isActiveUser
              ? AppColors.blue.withAlpha(37)
              : Colors.transparent,
        ),
        child: Row(
          children: [
            _getPlaceIndicator(context),
            SizedBox(width: 16),
            Avatar(name: profile.name, size: 32, color: profile.color),
            SizedBox(width: 12),
            Expanded(
              child: Text(
                "${profile.name}${isActiveUser ? " (вы)" : ""}",
                maxLines: 1,
                overflow: .ellipsis,
                style: TextStyle(
                  color: isActiveUser
                      ? AppColors.blue
                      : Theme.of(context).colorScheme.onSurface,
                  fontSize: 14,
                  fontWeight: .w500,
                ),
              ),
            ),
            PointsIndicator(pointsCount: profile.points),
          ],
        ),
      ),
    );
  }
}
