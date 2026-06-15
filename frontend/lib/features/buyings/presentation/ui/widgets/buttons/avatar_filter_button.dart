import 'package:flutter/material.dart';

class AvatarFilterButton extends StatelessWidget {
  const AvatarFilterButton({
    super.key,
    required this.size,
    required this.color,
    required this.name,
    required this.onTap,
    required this.isSelected,
  });

  final double size;
  final String name;
  final Color color;
  final bool isSelected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: size,
        height: size,
        decoration: BoxDecoration(
          color: color.withAlpha(isSelected ? 255 : 102),
          shape: .circle,
        ),
        child: Center(
          child: Text(
            name.substring(0, 1),
            style: TextStyle(
              color: Colors.white,
              fontSize: size * 0.5,
              fontWeight: .w600,
            ),
          ),
        ),
      ),
    );
  }
}
