import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_svg/svg.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/data/providers/neighbours_provider.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/ui/widgets/avatars/avatar.dart';
import 'package:frontend/shared/presentation/ui/widgets/badges/role_badge.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_icon_button.dart';

class ProfileDialog extends ConsumerWidget {
  const ProfileDialog({super.key, required this.profile});

  final Profile profile;

  Future<bool> _kick(BuildContext context, WidgetRef ref, int profileId) async {
    if (context.mounted) {
      Navigator.pop(context);
    }

    return await ref.read(neighboursProvider.notifier).kick(profileId);
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final userRole = ref.read(
      authProvider.select((s) => s.value?.user?.profile?.role),
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
            RoleBadge(role: profile.role),
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
                    userRole == .admin && profile.role == .inhabitant)
                  CustomIconButton(
                    color: AppColors.red,
                    icon: Icons.person_remove,
                    size: 36,
                    onPressed: () => _kick(context, ref, profile.id),
                    iconColor: Colors.white,
                  ),
                if (userRole == .creator)
                  profile.role == .admin
                      ? CustomIconButton(
                          color: AppColors.greyBlue,
                          icon: Icons.gpp_bad,
                          size: 36,
                          onPressed: () {},
                          iconColor: Colors.white,
                        )
                      : CustomIconButton(
                          color: AppColors.blue,
                          icon: Icons.star,
                          size: 36,
                          onPressed: () {},
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
