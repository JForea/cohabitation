import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:frontend/shared/presentation/widgets/buttons/custom_text_button.dart';
import 'package:go_router/go_router.dart';

class WelcomePage extends StatelessWidget {
  const WelcomePage({super.key});

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.sizeOf(context);
    final isDesktop = size.width >= 1024;

    return Scaffold(
      backgroundColor: Colors.white,
      body: Center(
        child: ConstrainedBox(
          constraints: BoxConstraints(
            maxWidth: isDesktop ? 500 : double.infinity,
          ),
          child: Padding(
            padding: EdgeInsets.symmetric(
              horizontal: isDesktop ? 32 : size.width * 0.1,
              vertical: isDesktop ? 48 : size.height * 0.1,
            ),
            child: isDesktop
                ? Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      ConstrainedBox(
                        constraints: BoxConstraints(
                          maxWidth: 340,
                          maxHeight: 340,
                        ),
                        child: SvgPicture.asset('assets/icons/welcome.svg'),
                      ),
                      SizedBox(height: 40),
                      Text(
                        'Добро пожаловать',
                        style: TextStyle(
                          fontSize: 32,
                          fontWeight: FontWeight.w600,
                        ),
                        textAlign: TextAlign.center,
                      ),
                      Text(
                        'в Flatly',
                        style: TextStyle(
                          fontSize: 32,
                          fontWeight: FontWeight.w600,
                        ),
                        textAlign: TextAlign.center,
                      ),
                      SizedBox(height: 40),
                      CustomTextButton(
                        onPressed: () => context.push("/auth/login"),
                        text: "Войти в аккаунт",
                      ),
                      SizedBox(height: 20),
                      _RegisterText(),
                    ],
                  )
                : Column(
                    children: [
                      Text(
                        'Добро пожаловать',
                        style: TextStyle(
                          fontSize: 24,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                      Text(
                        'в Flatly',
                        style: TextStyle(
                          fontSize: 24,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                      Spacer(),
                      SvgPicture.asset('assets/icons/welcome.svg'),
                      Spacer(),
                      CustomTextButton(
                        onPressed: () => context.push("/auth/login"),
                        text: "Войти в аккаунт",
                      ),
                      SizedBox(height: 20),
                      _RegisterText(),
                    ],
                  ),
          ),
        ),
      ),
    );
  }
}

class _RegisterText extends StatelessWidget {
  const _RegisterText();

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Text(
          "Нет аккаунта? ",
          style: TextStyle(
            fontSize: 13,
            fontWeight: FontWeight.w600,
            color: Color(0xFF707070),
          ),
        ),
        GestureDetector(
          onTap: () => context.push("/auth/register"),
          child: Text(
            "Зарегистрироваться",
            style: TextStyle(
              fontSize: 13,
              fontWeight: FontWeight.w700,
              color: Theme.of(context).colorScheme.primary,
            ),
          ),
        ),
      ],
    );
  }
}
