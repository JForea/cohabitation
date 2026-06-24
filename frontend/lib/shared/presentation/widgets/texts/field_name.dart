import 'package:flutter/material.dart';

class FieldName extends StatelessWidget {
  const FieldName({super.key, required this.title, required this.require});

  final String title;
  final bool require;

  @override
  Widget build(BuildContext context) {
    final textColor = Theme.of(context).colorScheme.onSurfaceVariant;

    return Row(
      children: [
        Text(
          "${title.toUpperCase()} ${require ? "*" : ""}",
          style: TextStyle(fontSize: 12, fontWeight: .w700, color: textColor),
        ),
        if (!require)
          Text(
            "(необязательно)",
            style: TextStyle(fontSize: 12, fontWeight: .w500, color: textColor),
          ),
      ],
    );
  }
}
