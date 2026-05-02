import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/auth/data/auth_data_holder.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_back_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/inputs/controlled_named_text_field.dart';

class LoginPage extends ConsumerWidget {
  LoginPage({super.key});

  final dataHolder = AuthDataHolder.instance;

  void setEmail(String s) {
    dataHolder.email = s;
  }

  void setPassword(String s) {
    dataHolder.password = s;
  }

  Future<void> login(WidgetRef ref) async {
    await ref
        .read(authProvider.notifier)
        .login(dataHolder.email, dataHolder.password);
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final mediaQuery = MediaQuery.of(context);

    return Scaffold(
      backgroundColor: Colors.white,
      body: Container(
        padding: .symmetric(
          horizontal: mediaQuery.size.width * 0.1,
          vertical: mediaQuery.size.height * 0.1,
        ),
        child: Column(
          crossAxisAlignment: .start,
          spacing: 20,
          children: [
            CustomBackButton(mainColor: true),
            Text(
              "Введите данные",
              style: TextStyle(fontSize: 20, fontWeight: .w500),
            ),
            ControlledNamedTextField(
              text: dataHolder.email,
              title: "Email",
              hintText: "example@mail.ru",
              onChange: setEmail,
              secondaryColor: true,
              password: false,
              require: true,
            ),
            ControlledNamedTextField(
              text: dataHolder.password,
              title: "Пароль",
              hintText: "********",
              onChange: setPassword,
              secondaryColor: true,
              password: true,
              require: true,
            ),
            Spacer(),
            CustomTextButton(onPressed: () => login(ref), text: "Войти"),
          ],
        ),
      ),
    );
  }
}
