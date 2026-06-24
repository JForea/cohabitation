import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:frontend/features/buyings/presentation/widgets/list_tiles/buying_list_tile.dart';
import 'package:frontend/shared/data/models/buying.dart';
import 'package:frontend/shared/data/types/buying_category.dart';
import 'package:frontend/shared/presentation/ui/widgets/lists/custom_widget_list.dart';
import 'package:frontend/core/utils/util_functions.dart';

class CategoryBuyingList extends StatelessWidget {
  const CategoryBuyingList({
    super.key,
    required this.category,
    required this.buyings,
    required this.onBuyingComplete,
    this.onSelect,
    this.onSelectCancel,
    this.selected,
  });

  final BuyingCategory category;
  final List<Buying> buyings;
  final void Function(int) onBuyingComplete;
  final Set<int>? selected;
  final void Function(int)? onSelect;
  final void Function(int)? onSelectCancel;

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
              "assets/icons/buying_categories/${category.name}.svg",
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
        CustomWidgetList(
          danger: false,
          children: buyings
              .map(
                (b) => BuyingListTile(
                  buying: b,
                  onComplete: onBuyingComplete,
                  selectionMode: selected?.isNotEmpty,
                  onSelect: onSelect,
                  onSelectCancel: onSelectCancel,
                ),
              )
              .toList(),
        ),
      ],
    );
  }
}
