import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/types/bubble.dart';
import 'package:frontend/shared/presentation/ui/widgets/other/bubble_widget.dart';
import 'package:frontend/shared/utils/util_functions.dart';

class HomeAppBar extends StatelessWidget {
  const HomeAppBar({super.key, required this.address, required this.userName});

  final String userName;
  final String? address;

  @override
  Widget build(BuildContext context) {
    final mediaQuery = MediaQuery.of(context);

    return Container(
      height: mediaQuery.size.height * 0.2,
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [AppColors.blue, Theme.of(context).colorScheme.secondary],
          begin: .topLeft,
          end: .bottomRight,
        ),
        borderRadius: .vertical(bottom: .circular(30)),
      ),
      child: Stack(
        children: [
          BubbleWidget(bubble: Bubble(x: 0.53, y: 0.03, size: 120)),
          BubbleWidget(bubble: Bubble(x: 0.7, y: 0.06, size: 30)),
          BubbleWidget(bubble: Bubble(x: 0.05, y: 0.13, size: 40)),
          Container(
            width: .infinity,
            margin: .only(top: 60, left: 20, right: 20),
            child: Column(
              spacing: 10,
              crossAxisAlignment: .start,
              children: [
                Text(
                  UtilFunctions.toHomeDateString(DateTime.now()),
                  style: TextStyle(
                    color: Color(0xFFD8D8D8),
                    fontSize: 14,
                    fontWeight: .w500,
                  ),
                ),
                Text(
                  "Привет, $userName! 👋",
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 18,
                    fontWeight: .w600,
                  ),
                ),
                Text(
                  address ?? "",
                  style: TextStyle(
                    color: Color(0xFFD8D8D8),
                    fontSize: 14,
                    fontWeight: .w500,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
