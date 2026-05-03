import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';

class AppStyles {
  static TextStyle surfaceTitle({double fontSize = 14}) => TextStyle(
    fontSize: fontSize,
    fontWeight: .w700,
    color: AppColors.greyBlue,
  );
}
