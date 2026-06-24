import 'package:flutter/material.dart';
import 'package:frontend/app/theme/app_shadows.dart';

class CustomIconButton extends StatelessWidget {
  const CustomIconButton({
    super.key,
    required this.color,
    required this.icon,
    required this.size,
    required this.onPressed,
    this.shadow,
    required this.iconColor,
  });

  final Color color;
  final IconData icon;
  final Color iconColor;
  final double size;
  final VoidCallback onPressed;
  final bool? shadow;

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.transparent,
      child: Ink(
        width: size,
        height: size,
        decoration: BoxDecoration(
          color: color,
          borderRadius: .all(.circular(size / 4)),
          boxShadow: (shadow != null
              ? [AppShadows.standard(color: color)]
              : []),
        ),
        child: InkWell(
          onTap: onPressed,
          splashColor: color.withAlpha(204),
          borderRadius: .all(.circular(size / 4)),
          child: Center(
            child: Icon(icon, size: size * 0.6, color: iconColor),
          ),
        ),
      ),
    );
  }
}
