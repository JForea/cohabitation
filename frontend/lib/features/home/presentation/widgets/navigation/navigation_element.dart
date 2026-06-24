import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';

class NavigationElement extends StatelessWidget {
  const NavigationElement({
    super.key,
    required this.onTap,
    required this.assetPath,
    required this.isActive,
    required this.text,
  });

  final VoidCallback onTap;
  final String assetPath;
  final bool isActive;
  final String text;

  @override
  Widget build(BuildContext context) {
    Color elementColor = isActive
        ? Theme.of(context).colorScheme.primary
        : Theme.of(context).colorScheme.onSurfaceVariant;

    return Ink(
      child: InkWell(
        onTap: onTap,
        child: Column(
          mainAxisSize: .min,
          spacing: 4,
          children: [
            Container(
              width: 40,
              height: 36,
              decoration: BoxDecoration(
                color: isActive
                    ? Theme.of(context).colorScheme.primary.withAlpha(37)
                    : Colors.transparent,
                borderRadius: .all(.circular(15)),
              ),
              child: Center(
                child: SvgPicture.asset(
                  assetPath,
                  colorFilter: .mode(elementColor, .srcIn),
                ),
              ),
            ),
            Text(
              text,
              style: TextStyle(
                color: elementColor,
                fontWeight: .w600,
                fontSize: 10,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
