import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:frontend/shared/data/types/notification_type.dart';
import 'package:frontend/app/theme/app_colors.dart';

class NotificationTypeBadge extends StatelessWidget {
  const NotificationTypeBadge({super.key, required this.type});

  final NotificationType type;

  Color _getColor() {
    return switch (type) {
      .task => AppColors.blue,
      .buying => AppColors.yellow,
      .event => AppColors.purple,
      .expense => AppColors.orange,
      .rule => AppColors.brown,
      .user => AppColors.green,
    };
  }

  @override
  Widget build(BuildContext context) {
    final color = _getColor();

    return Container(
      padding: .all(8),
      decoration: BoxDecoration(
        color: color.withAlpha(37),
        borderRadius: .all(.circular(10)),
      ),
      child: SvgPicture.asset(
        "assets/icons/notification_types/${type.name}.svg",
        colorFilter: ColorFilter.mode(color, .srcIn),
        height: 24,
        width: 24,
      ),
    );
  }
}
