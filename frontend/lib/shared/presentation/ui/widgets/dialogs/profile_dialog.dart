import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_svg/svg.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/data/providers/async_user_provider.dart';
import 'package:frontend/shared/data/providers/neighbours_provider.dart';
import 'package:frontend/shared/data/types/role.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/ui/widgets/avatars/avatar.dart';
import 'package:frontend/shared/presentation/ui/widgets/badges/role_badge.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_icon_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/snack_bars/message_snack_bar.dart';

class ProfileDialog extends ConsumerWidget {
  const ProfileDialog({super.key, required this.profile});

  final Profile profile;

  Future<void> _kick(BuildContext context, WidgetRef ref, int profileId) async {
    if (context.mounted) {
      Navigator.pop(context);
    }

    try {
      await ref.read(neighboursProvider.notifier).kick(profileId);
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          MessageSnackBar(message: "Произошла ошибка", error: true),
        );
      }
    }
  }

  Future<void> _setRole(
    BuildContext context,
    WidgetRef ref,
    int profileId,
    Role role,
  ) async {
    try {
      await ref.read(neighboursProvider.notifier).setRole(profileId, role);
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          MessageSnackBar(message: "Произошла ошибка", error: true),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final userRole = ref.read(
      asyncUserProvider.select((s) => s.value?.profile?.role),
    );
    final targetRole = ref.watch(
      neighboursProvider.select(
        (s) => s.value?.firstWhere((p) => p.id == profile.id).role,
      ),
    );

    return Dialog(
      child: Padding(
        padding: .symmetric(vertical: 20),
        child: Column(
          mainAxisSize: .min,
          spacing: 10,
          children: [
            Avatar(name: profile.name, size: 64, color: profile.color),
            Text(
              profile.name,
              maxLines: 2,
              overflow: .ellipsis,
              style: TextStyle(
                color: Theme.of(context).colorScheme.onSurface,
                fontSize: 16,
                fontWeight: .w500,
              ),
            ),
            RoleBadge(role: targetRole ?? profile.role),
            Row(
              mainAxisSize: .min,
              spacing: 5,
              children: [
                SvgPicture.asset(
                  "assets/icons/star.svg",
                  width: 18,
                  height: 18,
                ),
                Text(
                  "${profile.points}",
                  style: TextStyle(fontSize: 16, fontWeight: .w500),
                ),
              ],
            ),
            Row(
              spacing: 20,
              mainAxisSize: .min,
              children: [
                if (userRole == .creator ||
                    userRole == .admin && targetRole == .inhabitant)
                  CustomIconButton(
                    color: AppColors.red,
                    icon: Icons.person_remove,
                    size: 36,
                    onPressed: () => _kick(context, ref, profile.id),
                    iconColor: Colors.white,
                  ),
                if (userRole == .creator)
                  targetRole == .admin
                      ? CustomIconButton(
                          color: AppColors.greyBlue,
                          icon: Icons.gpp_bad,
                          size: 36,
                          onPressed: () =>
                              _setRole(context, ref, profile.id, .inhabitant),
                          iconColor: Colors.white,
                        )
                      : CustomIconButton(
                          color: AppColors.blue,
                          icon: Icons.star,
                          size: 36,
                          onPressed: () =>
                              _setRole(context, ref, profile.id, .admin),
                          iconColor: Colors.white,
                        ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
