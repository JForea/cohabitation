import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/auth/data/auth_data_holder.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_back_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/inputs/controlled_named_text_field.dart';
import 'package:frontend/shared/presentation/ui/widgets/switches/gender_switch.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/auth_page_wrapper.dart';
import 'package:frontend/shared/utils/fcm_helper.dart';

class RegistrationSecondPage extends ConsumerStatefulWidget {
  RegistrationSecondPage({super.key}) {
    dataHolder = AuthDataHolder.instance;
  }

  late final AuthDataHolder dataHolder;

  @override
  ConsumerState<RegistrationSecondPage> createState() =>
      _RegistrationSecondPageState();
}

class _RegistrationSecondPageState
    extends ConsumerState<RegistrationSecondPage> {
  void setName(String s) {
    widget.dataHolder.name = s;
  }

  void switchGender() {
    setState(() {
      widget.dataHolder.male = !widget.dataHolder.male;
    });
  }

  Future<void> register() async {
    bool permissionGranted = await FcmHelper.requestPermission();

    AuthDataHolder dataHolder = widget.dataHolder;

    if (permissionGranted) {
      await ref
          .read(authProvider.notifier)
          .register(
            dataHolder.email,
            dataHolder.password,
            dataHolder.name,
            dataHolder.male,
            deviceId: await FcmHelper.getDeviceId(),
            fcmToken: await FcmHelper.getToken(),
            platform: FcmHelper.getPlatform(),
          );
    } else {
      await ref
          .read(authProvider.notifier)
          .register(
            dataHolder.email,
            dataHolder.password,
            dataHolder.name,
            dataHolder.male,
          );
    }

    dataHolder.clear();
  }

  @override
  Widget build(BuildContext context) {
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
            text: widget.dataHolder.name,
            title: "ваше имя",
            hintText: "Александр",
            onChange: setName,
            secondaryColor: true,
            type: .text,
            require: true,
          ),
          GenderSwitch(male: widget.dataHolder.male, onPressed: switchGender),
          Spacer(),
          CustomTextButton(onPressed: register, text: "Продолжить"),
        ],
      ),
    );
  }
}
