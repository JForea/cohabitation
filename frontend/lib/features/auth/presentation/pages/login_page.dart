import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/auth/data/auth_data_holder.dart';
import 'package:frontend/core/failures/failures.dart';
import 'package:frontend/shared/state/providers/async_user_provider.dart';
import 'package:frontend/shared/presentation/widgets/buttons/custom_back_button.dart';
import 'package:frontend/shared/presentation/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/widgets/inputs/controlled_named_text_field.dart';
import 'package:frontend/shared/presentation/widgets/snack_bars/message_snack_bar.dart';
import 'package:frontend/shared/presentation/widgets/wrappers/auth_page_wrapper.dart';
import 'package:frontend/core/utils/fcm_helper.dart';
import 'package:go_router/go_router.dart';

class LoginPage extends ConsumerWidget {
  const LoginPage({super.key, required this.register});

  final bool register;

  void setEmail(WidgetRef ref, String s) {
    ref.read(authDataHolderProvider.notifier).setEmail(s);
  }

  void setPassword(WidgetRef ref, String s) {
    ref.read(authDataHolderProvider.notifier).setPassword(s);
  }

  bool validateEmail(String email) {
    final emailRegex = RegExp(
      r'^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$',
    );

    return emailRegex.hasMatch(email);
  }

  bool validatePassword(String password) {
    return password.length >= 8 && password.length <= 32;
  }

  Future<void> login(BuildContext context, WidgetRef ref) async {
    final authHolderNotifier = ref.read(authDataHolderProvider.notifier);

    final asyncUserNotifier = ref.read(asyncUserProvider.notifier);

    bool permissionGranted = await FcmHelper.requestPermission();

    String email = ref.read(authDataHolderProvider).email;
    String password = ref.read(authDataHolderProvider).password;

    bool ok = true;

    if (!validateEmail(email)) {
      ok = false;
      authHolderNotifier.setEmailError("Неверный формат");
    }

    if (!validatePassword(password)) {
      ok = false;
      authHolderNotifier.setPasswordError("Длина пароля - от 8 до 32 символов");
    }

    if (!ok) return;

    try {
      if (permissionGranted) {
        await asyncUserNotifier.authorize(
          false,
          email,
          password,
          deviceId: await FcmHelper.getDeviceId(),
          fcmToken: await FcmHelper.getToken(),
          platform: FcmHelper.getPlatform(),
        );
      } else {
        await asyncUserNotifier.authorize(false, email, password);
      }

      authHolderNotifier.clear();
    } on Failure catch (e) {
      if (e is NotFoundFailure) {
        authHolderNotifier.setEmailError("Неверный email или пароль");
      } else if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          MessageSnackBar(message: "Произошла ошибка", error: true),
        );
      }
    }
  }

  void goNextRegistrationPage(BuildContext context, WidgetRef ref) {
    bool ok = true;
    if (!validateEmail(ref.read(authDataHolderProvider).email)) {
      ok = false;
      ref
          .read(authDataHolderProvider.notifier)
          .setEmailError("Неверный формат");
    }
    if (!validatePassword(ref.read(authDataHolderProvider).password)) {
      ok = false;
      ref
          .read(authDataHolderProvider.notifier)
          .setPasswordError("Длина пароля - от 8 до 32 символов");
    }

    if (ok) {
      context.push("/auth/register/2");
    }
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final email = ref.read(authDataHolderProvider.select((dh) => dh.email));
    final password = ref.read(
      authDataHolderProvider.select((dh) => dh.password),
    );
    final emailError = ref.watch(
      authDataHolderProvider.select((dh) => dh.emailError),
    );
    final passwordError = ref.watch(
      authDataHolderProvider.select((dh) => dh.passwordError),
    );

    return Scaffold(
      backgroundColor: Colors.white,
      body: AuthPageWrapper(
        children: [
          CustomBackButton(mainColor: true, pathIfCantPop: "/auth"),
          Text(
            "Введите данные",
            style: TextStyle(fontSize: 20, fontWeight: .w500),
          ),
          ControlledNamedTextField(
            text: email,
            title: "Email",
            hintText: "example@mail.ru",
            onChange: (s) => setEmail(ref, s),
            secondaryColor: true,
            type: .text,
            require: true,
            errorMessage: emailError,
          ),
          ControlledNamedTextField(
            text: password,
            title: "Пароль",
            hintText: "********",
            onChange: (s) => setPassword(ref, s),
            secondaryColor: true,
            type: .password,
            require: true,
            errorMessage: passwordError,
          ),
          Spacer(),
          CustomTextButton(
            onPressed: register
                ? () => goNextRegistrationPage(context, ref)
                : () => login(context, ref),
            text: register ? "Продолжить" : "Войти",
          ),
        ],
      ),
    );
  }
}
