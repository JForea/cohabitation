import 'package:flutter/material.dart';

enum PageContolButtonType { previous, next }

class PageControlButton extends StatelessWidget {
  const PageControlButton({
    super.key,
    required this.size,
    required this.active,
    required this.onTap,
    required this.type,
  });

  final double size;
  final PageContolButtonType type;
  final bool active;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final color = active
        ? Theme.of(context).colorScheme.primary
        : Theme.of(context).colorScheme.onSurfaceVariant;

    return Material(
      color: Colors.transparent,
      shape: const CircleBorder(),
      child: Ink(
        width: size,
        height: size,
        decoration: BoxDecoration(
          shape: BoxShape.circle,
          color: color.withAlpha(37),
          border: Border.all(color: color),
        ),
        child: InkWell(
          customBorder: const CircleBorder(),
          splashColor: Colors.black.withAlpha(20),
          onTap: active ? onTap : null,
          child: Center(
            child: Icon(
              switch (type) {
                PageContolButtonType.previous => Icons.chevron_left,
                PageContolButtonType.next => Icons.chevron_right,
              },
              size: size * 0.8,
              color: color,
            ),
          ),
        ),
      ),
    );
  }
}
