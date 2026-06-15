import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_back_button.dart';

class PageWrapper extends StatelessWidget {
  const PageWrapper({
    super.key,
    required this.backButton,
    required this.pageName,
    required this.bottomFloatingButtonExists,
    required this.pathIfCantPop,
    required this.children,
    this.controller,
  });

  final List<Widget> children;
  final String pageName;
  final bool backButton;
  final bool bottomFloatingButtonExists;
  final String pathIfCantPop;
  final ScrollController? controller;

  @override
  Widget build(BuildContext context) {
    return CustomScrollView(
      controller: controller,
      slivers: [
        SliverPadding(
          padding: EdgeInsets.only(
            top: 60,
            left: 20,
            right: 20,
            bottom: bottomFloatingButtonExists ? 90 : 20,
          ),
          sliver: SliverList(
            delegate: SliverChildListDelegate([
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
                      fontWeight: FontWeight.w500,
                      color: Theme.of(context).colorScheme.onSurface,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 20),
              for (Widget child in children) ...[
                child,
                const SizedBox(height: 20),
              ],
            ]),
          ),
        ),
      ],
    );
  }
}
