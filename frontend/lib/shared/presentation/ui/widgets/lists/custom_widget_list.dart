import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/theme/app_shadows.dart';

class CustomWidgetList extends StatelessWidget {
  const CustomWidgetList({
    super.key,
    required this.children,
    required this.danger,
    this.title,
  });

  final List<Widget> children;
  final bool danger;
  final String? title;

  @override
  Widget build(BuildContext context) {
    final color = danger ? AppColors.red : Color(0xFFE9E9E9);

    return Container(
      width: .infinity,
      clipBehavior: .antiAlias,
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surfaceContainer,
        borderRadius: .all(.circular(20)),
        border: danger ? .all(color: color) : null,
        boxShadow: [AppShadows.standard()],
      ),
      child: Column(
        crossAxisAlignment: .start,
        children: [
          if (title != null) ...[
            Container(
              margin: .symmetric(horizontal: 20, vertical: 10),
              child: Text(
                title!.toUpperCase(),
                style: TextStyle(
                  fontSize: 14,
                  fontWeight: .w700,
                  color: danger
                      ? color
                      : Theme.of(context).colorScheme.secondary,
                ),
              ),
            ),
            Divider(height: 1, color: color),
          ],
          for (int i = 0; i < children.length; i++)
            Column(
              children: [
                children[i],
                if (i != children.length - 1) Divider(height: 1, color: color),
              ],
            ),
        ],
      ),
    );
  }
}
