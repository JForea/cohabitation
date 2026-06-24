import 'package:flutter/material.dart';
import 'package:frontend/app/theme/app_colors.dart';
import 'package:frontend/shared/presentation/types/bubble.dart';
import 'package:frontend/shared/presentation/widgets/buttons/custom_icon_button.dart';
import 'package:frontend/shared/presentation/widgets/other/bubble_widget.dart';
import 'package:frontend/core/utils/util_functions.dart';
import 'package:go_router/go_router.dart';

class HomeAppBar extends StatelessWidget {
  const HomeAppBar({
    super.key,
    required this.address,
    required this.userName,
    required this.unreadNotificationsCount,
  });

  final String userName;
  final String? address;
  final int unreadNotificationsCount;

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
            child: Row(
              mainAxisAlignment: .spaceBetween,
              crossAxisAlignment: .start,
              children: [
                Expanded(
                  child: Column(
                    spacing: 6,
                    crossAxisAlignment: .start,
                    children: [
                      Text(
                        UtilFunctions.toHomeDateString(DateTime.now()),
                        style: TextStyle(
                          color: Color(0xFFD8D8D8),
                          fontSize: 12,
                          fontWeight: .w500,
                        ),
                      ),
                      Text(
                        "Привет, $userName!",
                        maxLines: 1,
                        overflow: .ellipsis,
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 16,
                          fontWeight: .w600,
                        ),
                      ),

                      Text(
                        address ?? "",
                        style: TextStyle(
                          color: Color(0xFFD8D8D8),
                          fontSize: 12,
                          fontWeight: .w500,
                        ),
                      ),
                    ],
                  ),
                ),
                SizedBox(
                  width: 70,
                  height: 70,
                  child: Stack(
                    children: [
                      Center(
                        child: CustomIconButton(
                          color: Color(0xFFAAA3FF),
                          icon: Icons.notifications_outlined,
                          size: 40,
                          onPressed: () => context.push("/notifications"),
                          iconColor: Colors.white,
                        ),
                      ),
                      if (unreadNotificationsCount > 0)
                        Align(
                          alignment: .topRight,
                          child: Container(
                            padding: .all(8),
                            decoration: BoxDecoration(
                              color: AppColors.red,
                              border: .all(color: Colors.white),
                              shape: .circle,
                            ),
                            child: Text(
                              "$unreadNotificationsCount",
                              style: TextStyle(
                                color: Colors.white,
                                fontSize: 10,
                                fontWeight: .w600,
                              ),
                            ),
                          ),
                        ),
                    ],
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
