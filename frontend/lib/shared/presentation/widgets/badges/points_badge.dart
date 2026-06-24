import 'package:flutter/material.dart';

class PointsBadge extends StatelessWidget {
  const PointsBadge({super.key, required this.color, required this.value});

  final int value;
  final Color color;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: .symmetric(horizontal: 6, vertical: 3),
      decoration: BoxDecoration(
        color: color.withAlpha(37),
        borderRadius: .all(.circular(20)),
      ),
      child: Text(
        "+$value",
        style: TextStyle(color: color, fontSize: 12, fontWeight: .w500),
      ),
    );
  }
}
