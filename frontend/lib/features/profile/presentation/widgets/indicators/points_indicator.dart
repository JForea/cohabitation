import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class PointsIndicator extends StatelessWidget {
  const PointsIndicator({super.key, required this.pointsCount});

  final int pointsCount;

  @override
  Widget build(BuildContext context) {
    final svgSize = 12.0;

    return Row(
      spacing: 2,
      children: [
        SvgPicture.asset(
          "assets/icons/star.svg",
          width: svgSize,
          height: svgSize,
        ),
        Text("$pointsCount", style: TextStyle(fontSize: 12, fontWeight: .w700)),
      ],
    );
  }
}
