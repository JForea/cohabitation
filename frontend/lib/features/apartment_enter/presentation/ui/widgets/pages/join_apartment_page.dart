import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/apartment_enter/presentation/ui/widgets/cards/invite_hint_card.dart';
import 'package:frontend/features/apartment_enter/presentation/ui/widgets/inputs/invite_code_input.dart';
import 'package:frontend/shared/data/providers/async_apartment_provider.dart';
import 'package:frontend/shared/data/providers/async_user_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_back_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/snack_bars/message_snack_bar.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/auth_page_wrapper.dart';

class JoinApartmentPage extends ConsumerStatefulWidget {
  const JoinApartmentPage({super.key});

  @override
  ConsumerState<JoinApartmentPage> createState() => _JoinApartmentPageState();
}

class _JoinApartmentPageState extends ConsumerState<JoinApartmentPage> {
  late String inviteCode;

  void setInviteCode(String s) {
    inviteCode = s;
  }

  @override
  void initState() {
    inviteCode = "";
    super.initState();
  }

  Future<void> onJoin(BuildContext context) async {
    final profile = await ref
        .read(asyncApartmentProvider.notifier)
        .join(inviteCode);

    if (profile == null) {
      return;
    }

    try {
      ref.read(asyncUserProvider.notifier).setProfile(profile);
    } catch (_) {
      if (context.mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(MessageSnackBar(message: "Неверный код", error: true));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Scaffold(
      backgroundColor: Colors.white,
      body: AuthPageWrapper(
        children: [
          CustomBackButton(mainColor: true, pathIfCantPop: "/enter"),
          Text(
            "Войти в квартиру",
            style: TextStyle(
              fontSize: 20,
              fontWeight: .w500,
              color: colorScheme.onSurface,
            ),
          ),
          Text(
            "Узнайте код у администратора",
            style: TextStyle(
              color: Color(0xFF707070),
              fontSize: 14,
              fontWeight: .w500,
            ),
          ),
          InviteCodeInput(
            text: inviteCode,
            title: "Пригласительный код",
            hintText: "HOME0001",
            onChange: setInviteCode,
          ),
          InviteHintCard(),
          Spacer(),
          CustomTextButton(onPressed: () => onJoin(context), text: "Войти"),
        ],
      ),
    );
  }
}
