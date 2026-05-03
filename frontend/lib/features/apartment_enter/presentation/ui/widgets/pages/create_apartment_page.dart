import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_back_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/inputs/controlled_named_text_field.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/auth_page_wrapper.dart';

class CreateApartmentPage extends ConsumerStatefulWidget {
  const CreateApartmentPage({super.key});

  @override
  ConsumerState<CreateApartmentPage> createState() =>
      _CreateApartmentPageState();
}

class _CreateApartmentPageState extends ConsumerState<CreateApartmentPage> {
  late String _name;
  late String _address;

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
  }

  void create() async {
    await ref
        .read(apartmentProvider.notifier)
        .create(name: _name, address: _address);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: AuthPageWrapper(
        children: [
          CustomBackButton(mainColor: true),
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
            password: false,
            require: true,
          ),
          ControlledNamedTextField(
            text: _address,
            title: "Адрес",
            hintText: "ул. Ленина, 42, кв. 18",
            onChange: setAddress,
            secondaryColor: true,
            password: false,
            require: false,
          ),
          Spacer(),
          CustomTextButton(onPressed: create, text: "Создать"),
        ],
      ),
    );
  }
}
