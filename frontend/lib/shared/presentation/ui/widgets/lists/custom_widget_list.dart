import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/theme/app_shadows.dart';

class CustomWidgetList extends StatelessWidget {
  const CustomWidgetList({super.key, required this.children});

  final List<Widget> children;

  @override
  Widget build(BuildContext context) {
    return Container(
      clipBehavior: .antiAlias,
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surfaceContainer,
        borderRadius: .all(.circular(20)),
        boxShadow: [AppShadows.standard()],
      ),
      child: Column(
        children: children
            .map(
              (child) => Column(
                children: [
                  child,
                  Divider(height: 1, color: Color(0xFFE9E9E9)),
                ],
              ),
            )
            .toList(),
      ),
    );
  }
}
