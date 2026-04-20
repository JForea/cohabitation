import 'package:flutter/material.dart';

class _AnimatedGenderTitle extends StatelessWidget {
  const _AnimatedGenderTitle({
    required this.forMale,
    required this.duration,
    required this.male,
  });

  final bool male;
  final bool forMale;
  final int duration;

  @override
  Widget build(BuildContext context) {
    final Color first = forMale
        ? Theme.of(context).colorScheme.onPrimary
        : Theme.of(context).colorScheme.onSurfaceVariant;
    final Color second = forMale
        ? Theme.of(context).colorScheme.onSurfaceVariant
        : Theme.of(context).colorScheme.onPrimary;

    return Expanded(
      child: Center(
        child: AnimatedDefaultTextStyle(
          duration: Duration(milliseconds: duration),
          curve: Curves.easeInOut,
          style: TextStyle(
            color: male ? first : second,
            fontSize: 14,
            fontWeight: FontWeight.w700,
          ),
          child: Text(forMale ? "МУЖ" : "ЖЕН"),
        ),
      ),
    );
  }
}

class GenderSwitch extends StatelessWidget {
  const GenderSwitch({super.key, required this.male, required this.onPressed});

  final bool male;
  final VoidCallback onPressed;

  @override
  Widget build(BuildContext context) {
    final double borderRadius = 20;
    final int duration = 300;

    return Column(
      crossAxisAlignment: .start,
      spacing: 8,
      children: [
        Text(
          "ВАШ ПОЛ *",
          style: TextStyle(
            fontSize: 12,
            fontWeight: .w700,
            color: Theme.of(context).colorScheme.onSurfaceVariant,
          ),
        ),
        GestureDetector(
          onTap: onPressed,
          child: Container(
            height: 48,
            width: 140,
            decoration: BoxDecoration(
              color: Theme.of(context).colorScheme.surface,
              borderRadius: .all(.circular(borderRadius)),
            ),
            child: Stack(
              children: [
                AnimatedAlign(
                  duration: Duration(milliseconds: duration),
                  curve: Curves.easeInOut,
                  alignment: male
                      ? Alignment.centerLeft
                      : Alignment.centerRight,
                  child: AnimatedContainer(
                    duration: Duration(milliseconds: duration),
                    curve: Curves.easeInOut,
                    width: 70,
                    decoration: BoxDecoration(
                      color: Theme.of(context).colorScheme.primary,
                      borderRadius: BorderRadius.only(
                        topLeft: Radius.circular(male ? borderRadius : 0),
                        bottomLeft: Radius.circular(male ? borderRadius : 0),
                        topRight: Radius.circular(male ? 0 : borderRadius),
                        bottomRight: Radius.circular(male ? 0 : borderRadius),
                      ),
                    ),
                  ),
                ),
                Row(
                  children: [
                    _AnimatedGenderTitle(
                      forMale: true,
                      duration: duration,
                      male: male,
                    ),
                    _AnimatedGenderTitle(
                      forMale: false,
                      duration: duration,
                      male: male,
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}
