import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/types/bubble.dart';

class BubbleWidget extends StatelessWidget {
  const BubbleWidget({super.key, required this.bubble});

  final Bubble bubble;

  @override
  Widget build(BuildContext context) {
    final mediaQuery = MediaQuery.of(context);

    return Container(
      margin: .only(
        left: mediaQuery.size.width * bubble.x,
        top: mediaQuery.size.height * bubble.y,
      ),
      width: bubble.size,
      height: bubble.size,
      decoration: BoxDecoration(
        color: Colors.white.withAlpha(25),
        borderRadius: .all(.circular(bubble.size)),
      ),
    );
  }
}
