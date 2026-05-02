import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:frontend/features/buyings/presentation/ui/widgets/list_tiles/buying_list_tile.dart';
import 'package:frontend/shared/data/models/buying.dart';
import 'package:frontend/shared/data/types/buying_category.dart';
import 'package:frontend/shared/presentation/theme/app_shadows.dart';
import 'package:frontend/shared/utils/util_functions.dart';

class CategoryBuyingList extends StatelessWidget {
  const CategoryBuyingList({
    super.key,
    required this.category,
    required this.buyings,
    required this.onBuyingComplete,
  });

  final BuyingCategory category;
  final List<Buying> buyings;
  final void Function(int) onBuyingComplete;

  @override
  Widget build(BuildContext context) {
    return Column(
      spacing: 12,
      crossAxisAlignment: .start,
      children: [
        Row(
          spacing: 8,
          children: [
            SvgPicture.asset(
              "icons/buying_categories/${category.name}.svg",
              width: 28,
              height: 28,
            ),
            Text(
              UtilFunctions.getDisplayNameFromT(category).toUpperCase(),
              style: TextStyle(
                fontSize: 14,
                fontWeight: .w700,
                color: Theme.of(context).colorScheme.onSurfaceVariant,
              ),
            ),
          ],
        ),
        Container(
          clipBehavior: .antiAlias,
          decoration: BoxDecoration(
            color: Theme.of(context).colorScheme.surfaceContainer,
            borderRadius: .all(.circular(20)),
            boxShadow: [AppShadows.standard()],
          ),
          child: Column(
            children: buyings
                .map(
                  (b) => Column(
                    children: [
                      BuyingListTile(buying: b, onComplete: onBuyingComplete),
                      Divider(height: 1, color: Color(0xFFE9E9E9)),
                    ],
                  ),
                )
                .toList(),
          ),
        ),
      ],
    );
  }
}
