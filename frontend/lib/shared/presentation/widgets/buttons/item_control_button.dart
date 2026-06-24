import 'package:flutter/material.dart';
import 'package:frontend/app/theme/app_colors.dart';

class ItemControlButton extends StatelessWidget {
  const ItemControlButton({
    super.key,
    required this.onTap,
    required this.size,
    required this.add,
  });

  final VoidCallback onTap;
  final double size;
  final bool add;

  @override
  Widget build(BuildContext context) {
    Color color = add ? AppColors.blue : AppColors.red;

    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: size,
        height: size,
        decoration: BoxDecoration(
          color: color.withAlpha(37),
          borderRadius: .all(.circular(size / 2)),
        ),
        child: Icon(
          add ? Icons.add : Icons.remove,
          color: color,
          size: size * 0.7,
        ),
      ),
    );
  }
}
