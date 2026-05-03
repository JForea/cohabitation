import 'package:flutter/material.dart';
import 'package:frontend/features/buyings/data/models/buying_redacted.dart';
import 'package:frontend/features/buyings/presentation/ui/widgets/selectors/category_selector.dart';
import 'package:frontend/shared/data/types/buying_category.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/item_control_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/inputs/controlled_small_text_field.dart';

class RedactBuyingListTile extends StatelessWidget {
  const RedactBuyingListTile({
    super.key,
    required this.buying,
    required this.onRemove,
    required this.changeCategory,
  });

  final BuyingRedacted buying;
  final VoidCallback onRemove;
  final void Function(BuyingCategory) changeCategory;

  void _changeName(BuyingRedacted buying, String s) {
    buying.name = s;
  }

  void _changeQuantity(BuyingRedacted buying, String s) {
    buying.quantity = s;
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: .all(10),
      child: Row(
        spacing: 8,
        children: [
          Expanded(
            child: ControlledSmallTextField(
              text: buying.name,
              hintText: "Хлеб",
              onChange: (s) => _changeName(buying, s),
              secondaryColor: true,
            ),
          ),
          Expanded(
            child: ControlledSmallTextField(
              text: buying.quantity,
              hintText: "1 шт",
              onChange: (s) => _changeQuantity(buying, s),
              secondaryColor: true,
            ),
          ),
          CategorySelector(category: buying.category, change: changeCategory),
          ItemControlButton(onTap: onRemove, size: 32, add: false),
        ],
      ),
    );
  }
}
