import 'package:flutter/material.dart';
import 'package:frontend/features/auth/data/auth_data_holder.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_back_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/inputs/controlled_named_text_field.dart';
import 'package:go_router/go_router.dart';

class RegistrationFirstPage extends StatelessWidget {
  RegistrationFirstPage({super.key}) {
    dataHolder = AuthDataHolder.instance;
  }

  late final AuthDataHolder dataHolder;

  void setEmail(String s) {
    dataHolder.email = s;
  }

  void setPassword(String s) {
    dataHolder.password = s;
  }

  @override
  Widget build(BuildContext context) {
    final mediaQuery = MediaQuery.of(context);

    return Scaffold(
      backgroundColor: Colors.white,
      body: Container(
        padding: EdgeInsets.symmetric(
          vertical: mediaQuery.size.height * 0.1,
          horizontal: mediaQuery.size.width * 0.1,
        ),
        child: Column(
          crossAxisAlignment: .start,
          spacing: 20,
          children: [
            CustomBackButton(),
            Text(
              "Заполните данные",
              style: TextStyle(
                fontSize: 20,
                fontWeight: .w500,
                color: Theme.of(context).colorScheme.onSurface,
              ),
            ),
            ControlledNamedTextField(
              text: dataHolder.email,
              title: "email",
              hintText: "example@mail.ru",
              onChange: setEmail,
              secondaryColor: true,
              password: false,
              require: true,
            ),
            ControlledNamedTextField(
              text: dataHolder.password,
              title: "пароль",
              hintText: "********",
              onChange: setPassword,
              secondaryColor: true,
              password: true,
              require: true,
            ),
            Spacer(),
            CustomTextButton(
              onPressed: () => context.push("/auth/register/2"),
              text: "Продолжить",
            ),
          ],
        ),
      ),
    );
  }
}
