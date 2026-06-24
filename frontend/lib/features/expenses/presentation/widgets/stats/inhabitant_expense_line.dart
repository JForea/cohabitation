import 'package:flutter/material.dart';

class InhabitantExpenseLine extends StatelessWidget {
  const InhabitantExpenseLine({
    super.key,
    required this.color,
    required this.percent,
  });

  final Color color;
  final double percent;

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final width = constraints.maxWidth;

        return Stack(
          children: [
            Container(
              height: 4,
              width: width,
              decoration: BoxDecoration(
                color: Colors.grey.shade300,
                borderRadius: .circular(2),
              ),
            ),
            Container(
              height: 4,
              width: width * percent,
              decoration: BoxDecoration(
                color: color,
                borderRadius: .circular(2),
              ),
            ),
          ],
        );
      },
    );
  }
}
