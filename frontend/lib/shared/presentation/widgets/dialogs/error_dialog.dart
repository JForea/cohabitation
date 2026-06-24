import 'package:flutter/material.dart';
import 'package:frontend/app/theme/app_colors.dart';

void showErrorDialog(BuildContext context, String message) {
  showDialog(
    context: context,
    builder: (context) => Dialog(
      child: Padding(
        padding: .all(20),
        child: Column(
          mainAxisSize: .min,
          crossAxisAlignment: .start,
          spacing: 10,
          children: [
            Text(
              "Произошла ошибка",
              style: TextStyle(
                color: Theme.of(context).colorScheme.onSurface,
                fontSize: 18,
                fontWeight: .w500,
              ),
            ),
            Text(message, style: TextStyle(color: AppColors.red)),
          ],
        ),
      ),
    ),
  );
}
