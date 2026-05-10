import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_back_button.dart';

class PageWrapper extends StatelessWidget {
  const PageWrapper({
    super.key,
    required this.backButton,
    required this.pageName,
    required this.children,
    required this.bottomFloatingButtonExists,
    required this.pathIfCantPop,
  });

  final List<Widget> children;
  final String pageName;
  final bool backButton;
  final bool bottomFloatingButtonExists;
  final String pathIfCantPop;

  @override
  Widget build(BuildContext context) {
    return CustomScrollView(
      slivers: [
        SliverFillRemaining(
          hasScrollBody: false,
          child: Container(
            padding: .only(
              top: 60,
              left: 20,
              right: 20,
              bottom: bottomFloatingButtonExists ? 90 : 20,
            ),
            width: .infinity,
            child: Column(
              crossAxisAlignment: .start,
              spacing: 20,
              children: [
                Row(
                  spacing: 15,
                  children: [
                    CustomBackButton(
                      mainColor: false,
                      pathIfCantPop: pathIfCantPop,
                    ),
                    Text(
                      pageName,
                      style: TextStyle(
                        fontSize: 20,
                        fontWeight: .w500,
                        color: Theme.of(context).colorScheme.onSurface,
                      ),
                    ),
                  ],
                ),
                ...children,
              ],
            ),
          ),
        ),
      ],
    );
  }
}
