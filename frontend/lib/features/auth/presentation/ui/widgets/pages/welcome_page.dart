import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_text_button.dart';
import 'package:go_router/go_router.dart';

class WelcomePage extends StatelessWidget {
  const WelcomePage({super.key});

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
          children: [
            const Text(
              'Добро пожаловать',
              style: TextStyle(fontSize: 24, fontWeight: .w600),
            ),
            const Text(
              'в Flatly',
              style: TextStyle(fontSize: 24, fontWeight: .w600),
            ),
            Spacer(),
            SvgPicture.asset('assets/icons/welcome.svg'),
            Spacer(),
            CustomTextButton(
              onPressed: () => context.push("/auth/login"),
              text: "Войти в аккаунт",
            ),
            SizedBox(height: 20),
            Row(
              mainAxisAlignment: .center,
              children: [
                const Text(
                  "Нет аккаунта? ",
                  style: TextStyle(
                    fontSize: 13,
                    fontWeight: .w600,
                    color: Color(0xFF707070),
                  ),
                ),
                GestureDetector(
                  onTap: () => context.push("/auth/register"),
                  child: Text(
                    "Зарегистрироваться",
                    style: TextStyle(
                      fontSize: 13,
                      fontWeight: .w700,
                      color: Theme.of(context).colorScheme.primary,
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
