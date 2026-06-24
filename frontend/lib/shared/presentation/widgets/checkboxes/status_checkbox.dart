import 'package:flutter/material.dart';
import 'package:frontend/app/theme/app_colors.dart';

class StatusCheckbox extends StatelessWidget {
  const StatusCheckbox({
    super.key,
    required this.checked,
    required this.uncheckedColor,
    required this.onCheck,
  });

  final bool checked;
  final Color uncheckedColor;
  final VoidCallback onCheck;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onCheck,
      child: Container(
        width: 20,
        height: 20,
        decoration: BoxDecoration(
          border: checked ? null : .all(color: uncheckedColor, width: 1),
          color: checked ? AppColors.green : null,
          borderRadius: .all(.circular(20)),
        ),
        child: checked
            ? Center(child: Icon(Icons.done, color: Colors.white, size: 15))
            : null,
      ),
    );
  }
}
