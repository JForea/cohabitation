import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:frontend/shared/data/types/buying_category.dart';
import 'package:frontend/shared/presentation/theme/app_shadows.dart';

class CategorySelector extends StatelessWidget {
  const CategorySelector({
    super.key,
    required this.category,
    required this.change,
  });

  final BuyingCategory category;
  final void Function(BuyingCategory) change;

  void _showCategoryMenu(BuildContext context, Offset position) {
    const double iconSize = 28;
    const double gapSize = 14;
    const double paddingSize = 10;
    const int elementsPerLine = 3;

    final double totalContentWidth =
        iconSize * elementsPerLine +
        gapSize * (elementsPerLine - 1) +
        paddingSize * 2;

    showMenu(
      context: context,
      position: RelativeRect.fromLTRB(
        position.dx,
        position.dy,
        position.dx,
        position.dy,
      ),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      elevation: 8,
      constraints: BoxConstraints(
        maxWidth: totalContentWidth,
        minWidth: totalContentWidth,
      ),
      items: [
        PopupMenuItem(
          enabled: false,
          padding: EdgeInsets.zero,
          child: Container(
            padding: const EdgeInsets.all(paddingSize),
            child: Wrap(
              spacing: gapSize,
              runSpacing: gapSize,
              alignment: WrapAlignment.center,
              children: BuyingCategory.values
                  .map(
                    (c) => GestureDetector(
                      onTap: () {
                        Navigator.pop(context);
                        change(c);
                      },
                      child: SvgPicture.asset(
                        "assets/icons/buying_categories/${c.name}.svg",
                        width: iconSize,
                        height: iconSize,
                      ),
                    ),
                  )
                  .toList(),
            ),
          ),
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTapDown: (details) =>
          _showCategoryMenu(context, details.globalPosition),
      child: Container(
        padding: .symmetric(horizontal: 10, vertical: 5),
        decoration: BoxDecoration(
          borderRadius: .all(.circular(20)),
          color: Theme.of(context).colorScheme.surface,
          boxShadow: [AppShadows.standard()],
        ),
        child: Row(
          spacing: 4,
          children: [
            SvgPicture.asset(
              "assets/icons/buying_categories/${category.name}.svg",
              width: 24,
              height: 24,
            ),
            Icon(Icons.arrow_drop_down, color: Color(0xFF8080A4), size: 24),
          ],
        ),
      ),
    );
  }
}
