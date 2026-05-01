import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/types/bubble.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_icon_button.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/text_icon_button.dart';
import 'package:go_router/go_router.dart';

class OnboardingStepWrapper extends StatelessWidget {
  const OnboardingStepWrapper({
    super.key,
    required this.gradientStartColor,
    required this.gradientEndColor,
    required this.mainText,
    required this.secondaryText,
    required this.pagesCnt,
    required this.currentPage,
    required this.onButtonClick,
    required this.icon,
    required this.bubbles,
  });

  final Color gradientStartColor;
  final Color gradientEndColor;
  final List<Bubble> bubbles;
  final SvgPicture icon;
  final String mainText;
  final String secondaryText;
  final int pagesCnt;
  final int currentPage;
  final VoidCallback onButtonClick;

  @override
  Widget build(BuildContext context) {
    final mediaQuery = MediaQuery.of(context);

    return Stack(
      children: [
        Container(
          decoration: BoxDecoration(
            gradient: LinearGradient(
              colors: [gradientStartColor, gradientEndColor],
              begin: Alignment.topLeft,
              end: Alignment.centerRight,
            ),
          ),
        ),
        for (Bubble bubble in bubbles)
          Container(
            margin: .only(
              left: mediaQuery.size.width * bubble.x,
              top: mediaQuery.size.height * bubble.y,
            ),
            width: bubble.size,
            height: bubble.size,
            decoration: BoxDecoration(
              color: Colors.white.withAlpha(28),
              borderRadius: .all(.circular(bubble.size)),
            ),
          ),
        Container(
          height: mediaQuery.size.height * 0.65,
          width: .infinity,
          alignment: .center,
          child: icon,
        ),
        Align(
          alignment: Alignment.bottomCenter,
          child: Container(
            padding: EdgeInsets.all(24),
            height: mediaQuery.size.height * 0.35,
            width: double.infinity,
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.only(
                topLeft: Radius.circular(30),
                topRight: Radius.circular(30),
              ),
            ),
            child: Column(
              spacing: 14,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  mainText,
                  style: TextStyle(fontWeight: FontWeight.w500, fontSize: 20),
                ),
                Text(
                  secondaryText,
                  style: TextStyle(
                    fontWeight: FontWeight.w500,
                    fontSize: 14,
                    color: Color(0xFF9D9D9D),
                  ),
                ),
                Spacer(),
                Row(
                  children: [
                    Row(
                      spacing: 8,
                      children: [
                        for (int i = 0; i < pagesCnt; i++)
                          (i == currentPage - 1
                              ? Container(
                                  height: 8,
                                  width: 24,
                                  decoration: BoxDecoration(
                                    color: gradientStartColor,
                                    borderRadius: .all(.circular(4)),
                                  ),
                                )
                              : Container(
                                  height: 8,
                                  width: 8,
                                  decoration: BoxDecoration(
                                    color: Color(0xFFD9D9D9),
                                    borderRadius: .all(.circular(4)),
                                  ),
                                )),
                      ],
                    ),
                    Spacer(),
                    currentPage != pagesCnt
                        ? CustomIconButton(
                            color: gradientStartColor,
                            icon: Icons.chevron_right,
                            iconColor: Colors.white,
                            size: 50,
                            onPressed: onButtonClick,
                          )
                        : TextIconButton(
                            color: gradientStartColor,
                            icon: Icons.chevron_right,
                            text: 'Начать',
                            height: 50,
                            onPressed: () => context.go('/auth'),
                            contentColor: Colors.white,
                          ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}
