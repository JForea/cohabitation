import 'package:flutter/material.dart';

class CustomTextButton extends StatelessWidget {
  const CustomTextButton({
    super.key,
    required this.onPressed,
    required this.text,
  });

  final VoidCallback onPressed;
  final String text;

  @override
  Widget build(BuildContext context) {
    return Ink(
      child: InkWell(
        onTap: onPressed,
        borderRadius: .all(.circular(15)),
        child: Container(
          padding: .symmetric(vertical: 15),
          width: .infinity,
          alignment: .center,
          decoration: BoxDecoration(
            color: Theme.of(context).colorScheme.primary,
            borderRadius: .all(.circular(15)),
          ),
          child: Text(
            text,
            style: TextStyle(
              color: Theme.of(context).colorScheme.onPrimary,
              fontSize: 16,
              fontWeight: .w700,
            ),
          ),
        ),
      ),
    );
  }
}
