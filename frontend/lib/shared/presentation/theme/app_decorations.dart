import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/theme/app_shadows.dart';

class AppDecorations {
  static BoxDecoration choiceChipBox({
    required BuildContext context,
    required bool selected,
    double borderRadius = 10,
  }) => BoxDecoration(
    color: selected
        ? AppColors.blue.withAlpha(37)
        : Theme.of(context).colorScheme.surfaceContainer,
    borderRadius: .all(.circular(borderRadius)),
    border: .all(color: selected ? AppColors.blue : Colors.transparent),
    boxShadow: [
      AppShadows.standard(
        color: selected
            ? AppColors.blue.withAlpha(37)
            : Colors.black.withAlpha(37),
      ),
    ],
  );
}
