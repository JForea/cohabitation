import 'package:flutter/material.dart';

class CustomTextButton extends StatelessWidget {
  const CustomTextButton({
    super.key,
    required this.onPressed,
    required this.text,
    this.paddinH = 15,
    this.paddingW,
    this.borderRadius,
    this.fontSize = 16,
  });

  final VoidCallback onPressed;
  final String text;
  final double paddinH;
  final double? paddingW;
  final double? borderRadius;
  final double fontSize;

  @override
  Widget build(BuildContext context) {
    return Ink(
      child: InkWell(
        onTap: onPressed,
        borderRadius: .all(.circular(borderRadius ?? 15)),
        child: Container(
          padding: .symmetric(vertical: paddinH, horizontal: paddingW ?? 0),
          width: paddingW == null ? .infinity : null,
          alignment: .center,
          decoration: BoxDecoration(
            color: Theme.of(context).colorScheme.primary,
            borderRadius: .all(.circular(borderRadius ?? 15)),
          ),
          child: Text(
            text,
            style: TextStyle(
              color: Theme.of(context).colorScheme.onPrimary,
              fontSize: fontSize,
              fontWeight: .w700,
            ),
          ),
        ),
      ),
    );
  }
}
