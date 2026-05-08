import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:frontend/shared/data/types/notification_type.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';

class NotificationTypeBadge extends StatelessWidget {
  const NotificationTypeBadge({super.key, required this.type});

  final NotificationType type;

  Color _getColor() {
    return switch (type) {
      .task => AppColors.blue,
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
        height: 16,
        width: 16,
      ),
    );
  }
}
