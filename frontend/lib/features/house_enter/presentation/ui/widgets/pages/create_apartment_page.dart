import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_back_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/inputs/controlled_named_text_field.dart';

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
    await ref.read(authProvider.notifier).createApartment(_name, _address);
  }

  @override
  Widget build(BuildContext context) {
    final mediaQuery = MediaQuery.of(context);

    return Scaffold(
      backgroundColor: Colors.white,
      body: Container(
        padding: .symmetric(
          vertical: mediaQuery.size.height * 0.1,
          horizontal: mediaQuery.size.width * 0.1,
        ),
        child: Column(
          crossAxisAlignment: .start,
          spacing: 20,
          children: [
            CustomBackButton(),
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
      ),
    );
  }
}
