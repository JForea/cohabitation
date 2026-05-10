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
      child: ListView.separated(
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        itemCount: children.length + (title != null ? 1 : 0),
        separatorBuilder: (_, _) => Divider(height: 1, color: color),
        itemBuilder: (context, index) {
          if (title != null && index == 0) {
            return Container(
              margin: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
              child: Text(
                title!.toUpperCase(),
                style: TextStyle(
                  fontSize: 14,
                  fontWeight: FontWeight.w700,
                  color: danger
                      ? color
                      : Theme.of(context).colorScheme.onSurfaceVariant,
                ),
              ),
            );
          }

          final childIndex = title != null ? index - 1 : index;

          return children[childIndex];
        },
      ),
    );
  }
}
