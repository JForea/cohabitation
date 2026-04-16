import 'package:flutter/material.dart';

class TextIconButton extends StatelessWidget {
  const TextIconButton({
    super.key,
    required this.color,
    required this.icon,
    required this.text,
    required this.height,
    required this.onPressed,
    this.shadow,
    required this.contentColor,
  });

  final Color color;
  final String text;
  final IconData icon;
  final Color contentColor;
  final double height;
  final VoidCallback onPressed;
  final bool? shadow;

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.transparent,
      child: Ink(
        padding: .symmetric(horizontal: height * 0.4),
        height: height,
        decoration: BoxDecoration(
          color: color,
          borderRadius: .all(.circular(height / 4)),
          boxShadow: (shadow != null
              ? [BoxShadow(color: color.withAlpha(38), blurRadius: 3.5)]
              : []),
        ),
        child: InkWell(
          onTap: onPressed,
          splashColor: color.withAlpha(204),
          borderRadius: .all(.circular(height / 4)),
          child: Center(
            child: Row(
              spacing: 5,
              children: [
                Text(
                  text,
                  style: TextStyle(
                    fontSize: 14,
                    color: contentColor,
                    fontWeight: .w600,
                  ),
                ),
                Icon(icon, size: height * 0.6, color: contentColor),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
