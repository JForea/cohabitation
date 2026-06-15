import 'package:flutter/material.dart';

class DetailsTextButton extends StatelessWidget {
  const DetailsTextButton({super.key, required this.onTap, required this.text});

  final String text;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Text(
        text,
        style: TextStyle(
          color: Theme.of(context).colorScheme.primary,
          fontSize: 14,
          fontWeight: .w500,
        ),
      ),
    );
  }
}
