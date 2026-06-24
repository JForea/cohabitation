import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:frontend/features/apartment_enter/presentation/widgets/buttons/enter_button.dart';
import 'package:go_router/go_router.dart';

class OptionPage extends StatelessWidget {
  const OptionPage({super.key});

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.sizeOf(context);
    final isDesktop = size.width >= 1024;

    return Scaffold(
      backgroundColor: Colors.white,
      body: Center(
        child: ConstrainedBox(
          constraints: BoxConstraints(
            maxWidth: isDesktop ? 480 : double.infinity,
          ),
          child: Padding(
            padding: EdgeInsets.symmetric(
              horizontal: isDesktop ? 32 : size.width * 0.1,
            ),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                ConstrainedBox(
                  constraints: BoxConstraints(
                    maxWidth: isDesktop ? 280 : double.infinity,
                    maxHeight: isDesktop ? 280 : double.infinity,
                  ),
                  child: SvgPicture.asset("assets/icons/onboarding_home.svg"),
                ),
                SizedBox(height: 20),
                Text(
                  "Flatly",
                  style: TextStyle(
                    fontSize: isDesktop ? 28 : 20,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                SizedBox(height: 10),
                Text(
                  "Управление совместным проживанием",
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: isDesktop ? 16 : 14,
                    fontWeight: FontWeight.w500,
                    color: Color(0xFF707070),
                  ),
                ),
                SizedBox(height: isDesktop ? 48 : 60),
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
                  onTap: () => context.push("/enter/join"),
                  mainText: "Войти по коду",
                  subText: "Есть пригласительный код?",
                  icon: Icons.login,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
