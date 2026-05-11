import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/settings/presentation/ui/widgets/list_tiles/danger_list_tile.dart';
import 'package:frontend/features/settings/presentation/ui/widgets/list_tiles/generate_invite_code_tile.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/providers/async_apartment_provider.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/data/providers/rules_provider.dart';
import 'package:frontend/shared/data/providers/user_provider.dart';
import 'package:frontend/shared/data/types/role.dart';
import 'package:frontend/shared/presentation/ui/widgets/lists/custom_widget_list.dart';
import 'package:frontend/shared/presentation/ui/widgets/lists/rule_list.dart';
import 'package:frontend/shared/presentation/ui/widgets/snack_bars/message_snack_bar.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/page_wrapper.dart';
import 'package:go_router/go_router.dart';

class SettingsPage extends ConsumerStatefulWidget {
  const SettingsPage({super.key});

  @override
  ConsumerState<ConsumerStatefulWidget> createState() => _SettingsPageState();
}

class _SettingsPageState extends ConsumerState<SettingsPage> {
  late bool _isLoading;

  void _logout(WidgetRef ref) async {
    await ref.read(authProvider.notifier).logout();
  }

  void _leave(BuildContext context, WidgetRef ref) async {
    setState(() {
      _isLoading = true;
    });

    bool left = await ref.read(asyncApartmentProvider.notifier).leave();
    if (left && context.mounted) {
      ref.read(authProvider.notifier).setProfile(null);
      context.go("/enter");
    } else {
      setState(() {
        _isLoading = false;
      });
    }
  }

  Future<bool> _createRule(WidgetRef ref, String text) async {
    bool created = await ref.read(rulesProvider.notifier).create(text);

    return created;
  }

  Future<void> _deleteRule(WidgetRef ref, int ruleId) async {
    await ref.read(rulesProvider.notifier).delete(ruleId);
  }

  Future<void> _copyInviteCode(BuildContext context, String? inviteCode) async {
    if (inviteCode != null) {
      await Clipboard.setData(ClipboardData(text: inviteCode));

      if (context.mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(MessageSnackBar(message: "Скопировано"));
      }
    }
  }

  @override
  void initState() {
    _isLoading = false;
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final apartment = ref.watch(apartmentProvider);
    final role = ref.watch(userProvider.select((u) => u?.profile?.role));
    final rulesState = ref.watch(rulesProvider);

    return PopScope(
      canPop: !_isLoading,
      child: Stack(
        children: [
          Scaffold(
            body: PageWrapper(
              backButton: true,
              pathIfCantPop: "/",
              pageName: "Настройки",
              bottomFloatingButtonExists: false,
              children: [
                if (role != null &&
                    role != Role.inhabitant &&
                    apartment != null)
                  CustomWidgetList(
                    danger: false,
                    title: "Квартира",
                    children: [
                      GenerateInviteCodeTile(
                        inviteCode: apartment.inviteCode,
                        onCopy: () =>
                            _copyInviteCode(context, apartment.inviteCode),
                        onGenerate: () => ref
                            .read(asyncApartmentProvider.notifier)
                            .generateCode(),
                      ),
                    ],
                  ),
                if (role != null && role != .inhabitant)
                  rulesState.when(
                    data: (rules) => RuleList(
                      titleNeeded: true,
                      rules: rules,
                      onRuleAdd: (text) async => await _createRule(ref, text),
                      onRuleRemove: (id) async => await _deleteRule(ref, id),
                    ),
                    error: (e, _) => Text("Произошла ошибка при загрузке."),
                    loading: () => Center(child: CircularProgressIndicator()),
                  ),
                CustomWidgetList(
                  danger: true,
                  title: "Опасная зона",
                  children: [
                    DangerListTile(
                      iconData: Icons.home_outlined,
                      onTap: () => _leave(context, ref),
                      text: "Покинуть квартиру",
                    ),
                    DangerListTile(
                      iconData: Icons.logout,
                      onTap: () => _logout(ref),
                      text: "Выйти из аккаунта",
                    ),
                    if (role == Role.creator)
                      DangerListTile(
                        iconData: Icons.delete_outline,
                        onTap: () {},
                        text: "Удалить квартиру",
                      ),
                  ],
                ),
              ],
            ),
          ),

          if (_isLoading)
            Positioned.fill(
              child: Container(
                color: Colors.black54,
                child: const Center(child: CircularProgressIndicator()),
              ),
            ),
        ],
      ),
    );
  }
}
