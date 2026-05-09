import 'package:flutter/material.dart';
import 'package:frontend/features/profile/presentation/ui/widgets/badges/role_badge.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/presentation/types/bubble.dart';
import 'package:frontend/shared/presentation/ui/widgets/avatars/avatar.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_icon_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/other/bubble_widget.dart';
import 'package:go_router/go_router.dart';

class ProfileAppBar extends StatelessWidget {
  const ProfileAppBar({super.key, required this.profile});

  final Profile profile;

  @override
  Widget build(BuildContext context) {
    final mediaQuery = MediaQuery.of(context);

    return Container(
      height: mediaQuery.size.height * 0.2,
      width: .infinity,
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [Color(0xFF1B1B30), Color(0xFF2D2B5E)],
          begin: .topLeft,
          end: .bottomRight,
        ),
        borderRadius: .vertical(bottom: .circular(20)),
      ),
      child: Stack(
        children: [
          BubbleWidget(
            bubble: Bubble(
              x: 0.57,
              y: 0.02,
              size: mediaQuery.size.height * 0.15,
            ),
          ),
          BubbleWidget(
            bubble: Bubble(
              x: 0.2,
              y: 0.02,
              size: mediaQuery.size.height * 0.04,
            ),
          ),
          Container(
            margin: .only(
              top: mediaQuery.size.height * 0.06,
              left: 20,
              right: 30,
            ),
            child: Row(
              crossAxisAlignment: .start,
              children: [
                Avatar(name: profile.name, size: 64, color: profile.color),
                SizedBox(width: 15),
                Expanded(
                  child: Column(
                    crossAxisAlignment: .start,
                    children: [
                      Text(
                        profile.name,
                        maxLines: 1,
                        overflow: .ellipsis,
                        style: TextStyle(
                          fontSize: 16,
                          color: Colors.white,
                          fontWeight: .w600,
                        ),
                      ),
                      SizedBox(height: 12),
                      RoleBadge(role: profile.role),
                    ],
                  ),
                ),
                CustomIconButton(
                  color: Color(0xFF464566),
                  icon: Icons.settings_outlined,
                  size: 40,
                  onPressed: () => context.push('/settings'),
                  iconColor: Colors.white,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
