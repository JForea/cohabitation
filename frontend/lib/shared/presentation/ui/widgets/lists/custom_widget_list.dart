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

    final itemsCount = children.length * 2 - 1 + (title != null ? 2 : 0);

    return Container(
      width: double.infinity,
      clipBehavior: Clip.antiAlias,
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surfaceContainer,
        borderRadius: const BorderRadius.all(Radius.circular(20)),
        border: danger ? Border.all(color: color) : null,
        boxShadow: [AppShadows.standard()],
      ),
      child: ListView.builder(
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        itemCount: itemsCount,
        itemBuilder: (context, index) {
          if (title != null) {
            if (index == 0) {
              return Container(
                width: double.infinity,
                margin: const EdgeInsets.symmetric(
                  horizontal: 20,
                  vertical: 10,
                ),
                child: Text(
                  title!.toUpperCase(),
                  style: TextStyle(
                    color: danger
                        ? AppColors.red
                        : Theme.of(context).colorScheme.onSurfaceVariant,
                    fontSize: 14,
                    fontWeight: FontWeight.w700,
                  ),
                ),
              );
            }

            if (index == 1) {
              return Divider(height: 1, color: color);
            }

            index -= 2;
          }

          if (index.isOdd) {
            return Divider(height: 1, color: color);
          }

          return children[index ~/ 2];
        },
      ),
    );
  }
}
