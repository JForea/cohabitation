import 'package:flutter/material.dart';

class ChoiceWrapper extends StatelessWidget {
  const ChoiceWrapper({super.key, required this.name, required this.children});

  final String name;
  final List<Widget> children;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: .start,
      spacing: 8,
      children: [
        Text(
          name.toUpperCase(),
          style: TextStyle(
            fontSize: 12,
            fontWeight: .w700,
            color: Theme.of(context).colorScheme.onSurfaceVariant,
          ),
        ),
        Wrap(spacing: 8, runSpacing: 8, children: children),
      ],
    );
  }
}
