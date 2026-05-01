import 'package:flutter/material.dart';

class Avatar extends StatelessWidget {
  const Avatar({
    super.key,
    required this.name,
    required this.size,
    required this.color,
  });

  final double size;
  final Color color;
  final String name;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: size,
      height: size,
      decoration: BoxDecoration(
        color: color,
        borderRadius: .all(.circular(size)),
      ),
      child: Center(
        child: Text(
          name.substring(0, 1),
          style: TextStyle(
            color: Colors.white,
            fontWeight: .w600,
            fontSize: size * 0.7,
          ),
        ),
      ),
    );
  }
}
