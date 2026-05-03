import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/settings/presentation/ui/widgets/list_tiles/danger_list_tile.dart';
import 'package:frontend/features/settings/presentation/ui/widgets/list_tiles/generate_invite_code_tile.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/data/types/role.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_back_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/lists/custom_widget_list.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/page_wrapper.dart';

class SettingsPage extends ConsumerWidget {
  const SettingsPage({super.key});

  void logout(WidgetRef ref) {
    ref.read(authProvider.notifier).logout();
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final profile = ref.read(authProvider).value!.user!.profile!;
    final apartment = ref.watch(apartmentProvider).value!;

    return Scaffold(
      body: PageWrapper(
        children: [
          Row(
            spacing: 15,
            children: [
              CustomBackButton(mainColor: false),
              Text(
                "Настройки",
                style: TextStyle(
                  color: Theme.of(context).colorScheme.onSurface,
                  fontSize: 20,
                  fontWeight: .w500,
                ),
              ),
            ],
          ),
          if (profile.role != Role.inhabitant)
            CustomWidgetList(
              danger: false,
              title: "Квартира",
              children: [
                GenerateInviteCodeTile(
                  inviteCode: apartment.inviteCode,
                  onGenerate: () =>
                      ref.read(apartmentProvider.notifier).generateCode(),
                ),
              ],
            ),
          Spacer(),
          CustomWidgetList(
            danger: true,
            title: "Опасная зона",
            children: [
              DangerListTile(
                iconData: Icons.home_outlined,
                onTap: () {},
                text: "Покинуть квартиру",
              ),
              DangerListTile(
                iconData: Icons.logout,
                onTap: () => logout(ref),
                text: "Выйти из аккаунта",
              ),
              if (profile.role == Role.creator)
                DangerListTile(
                  iconData: Icons.delete_outline,
                  onTap: () {},
                  text: "Удалить квартиру",
                ),
            ],
          ),
        ],
      ),
    );
  }
}
