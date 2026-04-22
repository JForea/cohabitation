import 'package:flutter/material.dart';

Color getColorFromName(String name) {
  name = name.toLowerCase();
  switch (name) {
    case "blue":
      return Color(0xFF6C63FF);
    case "green":
      return Color(0xFF3EC98E);
    case "orange":
      return Color(0xFFFF7854);
    case "yellow":
      return Color(0xFFFFC107);
    case "purple":
      return Color(0xFFE155F6);
    case "brown":
      return Color(0xFF875B50);
    default:
      return Colors.black;
  }
}
