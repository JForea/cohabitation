import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/theme/app_decorations.dart';

class CustomChoiceChip extends StatelessWidget {
  const CustomChoiceChip({
    super.key,
    this.icon,
    required this.name,
    required this.selected,
    required this.checkMark,
    required this.onSelect,
    this.wPadding = 8,
  });

  final Widget? icon;
  final String name;
  final bool selected;
  final bool checkMark;
  final VoidCallback onSelect;
  final double wPadding;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onSelect,
      child: Container(
        padding: .symmetric(horizontal: wPadding, vertical: 6),
        decoration: AppDecorations.cardDecoration(
          context: context,
          selected: selected,
        ),
        child: Row(
          spacing: 6,
          mainAxisSize: .min,
          children: [
            ?icon,
            Text(
              name,
              style: TextStyle(
                color: selected ? AppColors.blue : Colors.black,
                fontSize: 14,
                fontWeight: .w500,
              ),
            ),
            if (checkMark && selected)
              Icon(Icons.check, size: 12, color: AppColors.blue),
          ],
        ),
      ),
    );
  }
}
