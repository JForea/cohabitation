import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:frontend/features/house_enter/presentation/ui/widgets/buttons/enter_button.dart';
import 'package:go_router/go_router.dart';

class OptionPage extends StatelessWidget {
  const OptionPage({super.key});

  @override
  Widget build(BuildContext context) {
    final mediaQuery = MediaQuery.of(context);

    return Scaffold(
      backgroundColor: Colors.white,
      body: Container(
        width: .infinity,
        padding: .symmetric(horizontal: mediaQuery.size.width * 0.1),
        child: Column(
          mainAxisAlignment: .center,
          children: [
            SvgPicture.asset("assets/icons/onboarding_home.svg"),
            SizedBox(height: 20),
            Text("Flatly", style: TextStyle(fontSize: 20, fontWeight: .w500)),
            SizedBox(height: 10),
            Text(
              "Управление совместным проживанием",
              textAlign: .center,
              style: TextStyle(
                fontSize: 14,
                fontWeight: .w500,
                color: Color(0xFF707070),
              ),
            ),
            SizedBox(height: 60),
            EnterButton(
              filled: true,
              onTap: () => context.push("/enter/create"),
              mainText: "Создать квартиру",
              subText: "Станьте администратором",
              icon: Icons.add,
            ),
            SizedBox(height: 15),
            EnterButton(
              filled: false,
              onTap: () {},
              mainText: "Войти по коду",
              subText: "Есть пригласительный код?",
              icon: Icons.login,
            ),
          ],
        ),
      ),
    );
  }
}
