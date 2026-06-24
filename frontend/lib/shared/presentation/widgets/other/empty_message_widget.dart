import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:frontend/app/theme/app_colors.dart';

class EmptyMessageWidget extends StatelessWidget {
  const EmptyMessageWidget({
    super.key,
    required this.assetPath,
    required this.message,
    this.fontSize = 18,
    this.iconSize = 90,
  });

  final double iconSize;
  final double fontSize;
  final String assetPath;
  final String message;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        SvgPicture.asset(
          assetPath,
          colorFilter: ColorFilter.mode(AppColors.greyBlue, .srcIn),
          width: iconSize,
          height: iconSize,
        ),
        SizedBox(height: 20),
        Text(
          message,
          textAlign: .center,
          style: TextStyle(
            color: AppColors.greyBlue,
            fontSize: fontSize,
            fontWeight: .w700,
          ),
        ),
      ],
    );
  }
}
