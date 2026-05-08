import 'package:flutter/material.dart';

class CustomAppFloatingActionButton extends StatelessWidget {
  const CustomAppFloatingActionButton({
    super.key,
    required this.onPressed,
    this.iconData = Icons.add,
  });

  final VoidCallback onPressed;
  final IconData iconData;

  @override
  Widget build(BuildContext context) {
    return FloatingActionButton(
      onPressed: onPressed,
      shape: CircleBorder(),
      child: Icon(iconData, size: 28),
    );
  }
}
