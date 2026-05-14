import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/auth/data/auth_data_holder.dart';
import 'package:frontend/shared/data/failures/failures.dart';
import 'package:frontend/shared/data/providers/async_user_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_back_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/inputs/controlled_named_text_field.dart';
import 'package:frontend/shared/presentation/ui/widgets/snack_bars/message_snack_bar.dart';
import 'package:frontend/shared/presentation/ui/widgets/switches/gender_switch.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/auth_page_wrapper.dart';
import 'package:frontend/shared/utils/fcm_helper.dart';

class RegistrationSecondPage extends ConsumerWidget {
  const RegistrationSecondPage({super.key});

  void setName(WidgetRef ref, String s) {
    ref.read(authDataHolderProvider.notifier).setName(s);
  }

  void switchGender(WidgetRef ref) {
    ref.read(authDataHolderProvider.notifier).switchGender();
  }

  Future<void> register(BuildContext context, WidgetRef ref) async {
    final dataHolder = ref.read(authDataHolderProvider);
    bool permissionGranted = await FcmHelper.requestPermission();

    try {
      if (permissionGranted) {
        await ref
            .read(asyncUserProvider.notifier)
            .authorize(
              true,
              dataHolder.email,
              dataHolder.password,
              name: dataHolder.name,
              male: dataHolder.male,
              deviceId: await FcmHelper.getDeviceId(),
              fcmToken: await FcmHelper.getToken(),
              platform: FcmHelper.getPlatform(),
            );
      } else {
        await ref
            .read(asyncUserProvider.notifier)
            .authorize(
              true,
              dataHolder.email,
              dataHolder.password,
              name: dataHolder.name,
              male: dataHolder.male,
            );
      }

      dataHolder.clear();
    } on Failure catch (e) {
      if (e is ConflictFailure) {
        ref
            .read(authDataHolderProvider.notifier)
            .setEmailError("Email уже зарегистрирован");
      } else if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          MessageSnackBar(message: "Произошла ошибка", error: true),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final name = ref.read(authDataHolderProvider.select((dh) => dh.name));
    final male = ref.watch(authDataHolderProvider.select((dh) => dh.male));
    final nameError = ref.watch(
      authDataHolderProvider.select((dh) => dh.nameError),
    );

    return Scaffold(
      backgroundColor: Colors.white,
      body: AuthPageWrapper(
        children: [
          CustomBackButton(mainColor: true, pathIfCantPop: "/auth/register/1"),
          Text(
            "Как вас зовут?",
            style: TextStyle(
              fontSize: 20,
              fontWeight: .w500,
              color: Theme.of(context).colorScheme.onSurface,
            ),
          ),
          Text(
            "Введите имя - так вас будут видеть соседи",
            style: TextStyle(
              fontSize: 14,
              fontWeight: .w500,
              color: Color(0xFF707070),
            ),
          ),
          ControlledNamedTextField(
            text: name,
            title: "ваше имя",
            hintText: "Александр",
            onChange: (s) => setName(ref, s),
            secondaryColor: true,
            type: .text,
            require: true,
            errorMessage: nameError,
          ),
          GenderSwitch(male: male, onPressed: () => switchGender(ref)),
          Spacer(),
          CustomTextButton(
            onPressed: () => register(context, ref),
            text: "Продолжить",
          ),
        ],
      ),
    );
  }
}
