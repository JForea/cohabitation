import 'package:flutter/material.dart';

class AppShadows {
  static BoxShadow standard({Color color = Colors.black}) =>
      BoxShadow(color: color.withAlpha(38), blurRadius: 3.5);
}
