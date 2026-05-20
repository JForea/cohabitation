import 'package:flutter/material.dart';

class FilterButtonWide extends StatelessWidget {
  const FilterButtonWide({
    super.key,
    required this.height,
    required this.color,
    required this.name,
    required this.onTap,
    required this.isSelected,
  });

  final double height;
  final String name;
  final Color color;
  final bool isSelected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: .symmetric(horizontal: 12),
        height: height,
        decoration: BoxDecoration(
          color: color.withAlpha(isSelected ? 255 : 102),
          borderRadius: .circular(20),
        ),
        child: Center(
          child: Text(
            name,
            style: TextStyle(
              color: Colors.white,
              fontSize: 14,
              fontWeight: .w600,
            ),
          ),
        ),
      ),
    );
  }
}
