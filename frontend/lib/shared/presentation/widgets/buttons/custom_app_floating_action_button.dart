import 'package:flutter/material.dart';
import 'package:frontend/app/theme/app_colors.dart';

class CustomAppFloatingActionButton extends StatelessWidget {
  const CustomAppFloatingActionButton({
    super.key,
    required this.onPressed,
    this.iconData = Icons.add,
    this.color = AppColors.blue,
  });

  final VoidCallback onPressed;
  final IconData iconData;
  final Color color;

  @override
  Widget build(BuildContext context) {
    return FloatingActionButton(
      onPressed: onPressed,
      backgroundColor: color,
      shape: CircleBorder(),
      child: Icon(iconData, size: 28),
    );
  }
}
