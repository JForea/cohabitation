import 'package:flutter/material.dart';
import 'package:frontend/features/auth/data/auth_data_holder.dart';
import 'package:frontend/features/auth/data/auth_service.dart';
import 'package:frontend/shared/data/network/dio_client.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_back_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/inputs/controlled_named_text_field.dart';
import 'package:frontend/shared/presentation/ui/widgets/switches/gender_switch.dart';

class RegistrationSecondPage extends StatefulWidget {
  RegistrationSecondPage({super.key}) {
    dataHolder = AuthDataHolder.instance;
    authService = AuthService(AppDio.dio);
  }

  late final AuthDataHolder dataHolder;
  late final AuthService authService;

  @override
  State<RegistrationSecondPage> createState() => _RegistrationSecondPageState();
}

class _RegistrationSecondPageState extends State<RegistrationSecondPage> {
  void setName(String s) {
    widget.dataHolder.name = s;
  }

  void switchSex() {
    setState(() {
      widget.dataHolder.male = !widget.dataHolder.male;
    });
  }

  Future<void> register() async {
    AuthDataHolder dataHolder = widget.dataHolder;

    await widget.authService.register(
      dataHolder.email,
      dataHolder.password,
      dataHolder.name,
      dataHolder.male,
    );
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
              text: widget.dataHolder.name,
              title: "ваше имя",
              hintText: "Александр",
              onChange: setName,
              secondaryColor: true,
              password: false,
              require: true,
            ),
            GenderSwitch(male: widget.dataHolder.male, onPressed: switchSex),
            Spacer(),
            CustomTextButton(onPressed: register, text: "Продолжить"),
          ],
        ),
      ),
    );
  }
}
