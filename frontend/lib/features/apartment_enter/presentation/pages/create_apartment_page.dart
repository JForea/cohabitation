import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/apartment_enter/utils/apartment_validators.dart';
import 'package:frontend/shared/state/providers/async_apartment_provider.dart';
import 'package:frontend/shared/state/providers/async_user_provider.dart';
import 'package:frontend/shared/presentation/widgets/buttons/custom_back_button.dart';
import 'package:frontend/shared/presentation/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/widgets/inputs/controlled_named_text_field.dart';
import 'package:frontend/shared/presentation/widgets/snack_bars/message_snack_bar.dart';
import 'package:frontend/shared/presentation/widgets/wrappers/auth_page_wrapper.dart';
import 'package:go_router/go_router.dart';

class CreateApartmentPage extends ConsumerStatefulWidget {
  const CreateApartmentPage({super.key});

  @override
  ConsumerState<CreateApartmentPage> createState() =>
      _CreateApartmentPageState();
}

class _CreateApartmentPageState extends ConsumerState<CreateApartmentPage> {
  late String _name;
  late String _address;

  late String _nameErrorMessage;
  late String _addressErrorMessage;

  void setName(String s) {
    _name = s;
  }

  void setAddress(String s) {
    _address = s;
  }

  @override
  void initState() {
    super.initState();
    _name = "";
    _address = "";

    _nameErrorMessage = "";
    _addressErrorMessage = "";
  }

  Future<void> create(BuildContext context) async {
    bool ok = true;
    setState(() {
      final nameError = ApartmentValidators.validateName(_name);
      if (nameError != null) {
        _nameErrorMessage = nameError;
        ok = false;
      }
      final addressError = ApartmentValidators.validateAddress(_address);
      if (addressError != null) {
        _addressErrorMessage = addressError;
        ok = false;
      }
    });

    if (!ok) return;

    try {
      final profile = await ref
          .read(asyncApartmentProvider.notifier)
          .create(name: _name, address: _address);
      ref.read(asyncUserProvider.notifier).setProfile(profile);

      if (context.mounted) {
        context.go("/");
      }
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          MessageSnackBar(message: "Произошла ошибка", error: true),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: AuthPageWrapper(
        children: [
          CustomBackButton(mainColor: true, pathIfCantPop: "/enter"),
          Text(
            "Создать квартиру",
            style: TextStyle(fontSize: 20, fontWeight: .w500),
          ),
          Text(
            "Вы станете администратором",
            style: TextStyle(
              fontSize: 14,
              fontWeight: .w500,
              color: Color(0xFF707070),
            ),
          ),
          ControlledNamedTextField(
            text: _name,
            title: "Название",
            hintText: "Наша квартира",
            onChange: setName,
            secondaryColor: true,
            type: .text,
            require: true,
            errorMessage: _nameErrorMessage,
          ),
          ControlledNamedTextField(
            text: _address,
            title: "Адрес",
            hintText: "ул. Ленина, 42, кв. 18",
            onChange: setAddress,
            secondaryColor: true,
            type: .text,
            require: false,
            errorMessage: _addressErrorMessage,
          ),
          Spacer(),
          CustomTextButton(
            onPressed: () async => await create(context),
            text: "Создать",
          ),
        ],
      ),
    );
  }
}
