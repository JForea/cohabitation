import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/theme/app_decorations.dart';

class CustomSwitch extends StatelessWidget {
  const CustomSwitch({
    super.key,
    required this.turnedOn,
    required this.onSwitch,
  });

  final bool turnedOn;
  final VoidCallback onSwitch;

  @override
  Widget build(BuildContext context) {
    final duration = const Duration(milliseconds: 200);

    return GestureDetector(
      onTap: onSwitch,
      child: AnimatedContainer(
        duration: duration,
        curve: Curves.easeInOut,
        padding: .all(3),
        width: 44,
        decoration: AppDecorations.cardDecoration(
          context: context,
          selected: turnedOn,
          borderRadius: 20,
        ),
        child: AnimatedAlign(
          alignment: turnedOn ? .centerRight : .centerLeft,
          duration: duration,
          child: AnimatedContainer(
            duration: duration,
            curve: Curves.bounceInOut,
            width: 18,
            height: 18,
            decoration: BoxDecoration(
              borderRadius: .all(.circular(14)),
              color: turnedOn ? AppColors.blue : AppColors.greyBlue,
            ),
          ),
        ),
      ),
    );
  }
}
