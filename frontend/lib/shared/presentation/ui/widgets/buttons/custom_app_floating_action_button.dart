import 'package:flutter/material.dart';

class CustomAppFloatingActionButton extends StatelessWidget {
  const CustomAppFloatingActionButton({super.key, required this.onPressed});

  final VoidCallback onPressed;

  @override
  Widget build(BuildContext context) {
    return FloatingActionButton(
      onPressed: onPressed,
      shape: CircleBorder(),
      child: Icon(Icons.add, size: 28),
    );
  }
}
