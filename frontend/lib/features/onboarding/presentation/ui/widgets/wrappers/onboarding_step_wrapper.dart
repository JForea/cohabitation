import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/types/bubble.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_icon_button.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/text_icon_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/other/bubble_widget.dart';
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
    final size = MediaQuery.sizeOf(context);
    final isDesktop = size.width >= 1024;
    final mediaQuery = MediaQuery.of(context);

    if (isDesktop) {
      return Row(
        children: [
          Expanded(
            flex: 3,
            child: Stack(
              children: [
                Container(
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      colors: [gradientStartColor, gradientEndColor],
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                    ),
                  ),
                ),
                for (final bubble in bubbles) BubbleWidget(bubble: bubble),
                Center(
                  child: ConstrainedBox(
                    constraints: BoxConstraints(maxWidth: 420),
                    child: icon,
                  ),
                ),
              ],
            ),
          ),

          Expanded(
            flex: 2,
            child: Center(
              child: ConstrainedBox(
                constraints: BoxConstraints(maxWidth: 560),
                child: Padding(
                  padding: EdgeInsets.all(48),
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        mainText,
                        style: TextStyle(
                          fontWeight: FontWeight.w600,
                          fontSize: 32,
                        ),
                      ),
                      SizedBox(height: 18),
                      Text(
                        secondaryText,
                        style: TextStyle(
                          fontWeight: FontWeight.w500,
                          fontSize: 18,
                          color: Color(0xFF9D9D9D),
                        ),
                      ),
                      SizedBox(height: 48),
                      Row(
                        children: [
                          Row(
                            children: [
                              for (int i = 0; i < pagesCnt; i++) ...[
                                Container(
                                  height: 8,
                                  width: i == currentPage - 1 ? 24 : 8,
                                  decoration: BoxDecoration(
                                    color: i == currentPage - 1
                                        ? gradientStartColor
                                        : Color(0xFFD9D9D9),
                                    borderRadius: BorderRadius.circular(4),
                                  ),
                                ),
                                if (i != pagesCnt - 1) const SizedBox(width: 8),
                              ],
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
            ),
          ),
        ],
      );
    }

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
        for (Bubble bubble in bubbles) BubbleWidget(bubble: bubble),
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
