import 'package:flutter/material.dart';
import 'package:frontend/app/theme/app_colors.dart';
import 'package:frontend/shared/presentation/widgets/buttons/item_control_button.dart';

class AddListTile extends StatelessWidget {
  const AddListTile({super.key, required this.onAdd, required this.text});

  final String text;
  final VoidCallback onAdd;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onAdd,
      child: Container(
        padding: .symmetric(vertical: 10),
        child: Row(
          mainAxisAlignment: .center,
          spacing: 8,
          children: [
            Text(
              text,
              style: TextStyle(
                fontSize: 14,
                fontWeight: .w500,
                color: AppColors.blue,
              ),
            ),
            ItemControlButton(onTap: onAdd, size: 32, add: true),
          ],
        ),
      ),
    );
  }
}
