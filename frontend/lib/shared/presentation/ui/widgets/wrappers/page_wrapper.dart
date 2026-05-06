import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_back_button.dart';

class PageWrapper extends StatelessWidget {
  const PageWrapper({
    super.key,
    required this.backButton,
    required this.pageName,
    required this.children,
  });

  final List<Widget> children;
  final String pageName;
  final bool backButton;

  @override
  Widget build(BuildContext context) {
    return CustomScrollView(
      slivers: [
        SliverFillRemaining(
          hasScrollBody: false,
          child: Container(
            padding: .only(top: 60, left: 20, right: 20, bottom: 20),
            width: .infinity,
            child: Column(
              crossAxisAlignment: .start,
              spacing: 20,
              children: [
                Row(
                  spacing: 15,
                  children: [
                    CustomBackButton(mainColor: false),
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
