import 'package:flutter/material.dart';

class TaskFilterChip extends StatelessWidget {
  const TaskFilterChip({
    super.key,
    required this.onTap,
    required this.text,
    required this.selected,
  });

  final String text;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final boxColor = selected
        ? Theme.of(context).colorScheme.primary
        : Theme.of(context).colorScheme.surfaceContainer;

    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: .symmetric(horizontal: 12, vertical: 6),
        decoration: BoxDecoration(color: boxColor, borderRadius: .circular(15)),
        child: Text(
          text,
          style: TextStyle(
            color: selected
                ? Theme.of(context).colorScheme.onPrimary
                : Theme.of(context).colorScheme.onSurfaceVariant,
            fontSize: 14,
            fontWeight: .w500,
          ),
        ),
      ),
    );
  }
}
