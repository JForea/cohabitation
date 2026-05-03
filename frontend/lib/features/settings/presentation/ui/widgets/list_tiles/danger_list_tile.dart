import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';

class DangerListTile extends StatelessWidget {
  const DangerListTile({
    super.key,
    required this.iconData,
    required this.onTap,
    required this.text,
  });

  final VoidCallback onTap;
  final IconData iconData;
  final String text;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: .symmetric(horizontal: 20, vertical: 15),
        child: Row(
          spacing: 10,
          children: [
            Icon(iconData, color: AppColors.red, size: 28),
            Text(
              text,
              style: TextStyle(
                fontSize: 14,
                fontWeight: .w600,
                color: AppColors.red,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
