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
    final color = danger ? AppColors.red : const Color(0xFFE9E9E9);

    return Container(
      width: double.infinity,
      clipBehavior: Clip.antiAlias,
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surfaceContainer,
        borderRadius: const BorderRadius.all(Radius.circular(20)),
        border: danger ? Border.all(color: color) : null,
        boxShadow: [AppShadows.standard()],
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          if (title != null) ...[
            Container(
              width: double.infinity,
              margin: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
              child: Text(
                title!.toUpperCase(),
                style: TextStyle(
                  color: danger
                      ? AppColors.red
                      : Theme.of(context).colorScheme.onSurfaceVariant,
                  fontSize: 14,
                  fontWeight: .w700,
                ),
              ),
            ),
            Divider(height: 1, color: color),
          ],

          for (int i = 0; i < children.length; i++) ...[
            children[i],
            if (i != children.length - 1) Divider(height: 1, color: color),
          ],
        ],
      ),
    );
  }
}
