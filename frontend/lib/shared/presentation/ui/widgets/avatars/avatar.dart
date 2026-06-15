import 'package:flutter/material.dart';

class Avatar extends StatelessWidget {
  const Avatar({
    super.key,
    required this.name,
    required this.size,
    required this.color,
    this.active,
    this.onTap,
  });

  final double size;
  final Color color;
  final String name;
  final bool? active;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: size,
        height: size,
        decoration: BoxDecoration(
          color: color.withAlpha(active != null && !active! ? 105 : 255),
          borderRadius: .all(.circular(size)),
        ),
        child: Center(
          child: Text(
            name.substring(0, 1),
            style: TextStyle(
              color: Colors.white,
              fontWeight: .w600,
              fontSize: size * 0.5,
            ),
          ),
        ),
      ),
    );
  }
}
