import 'package:frontend/shared/data/types/buying_category.dart';

class BuyingRedacted {
  BuyingRedacted({
    this.name = "",
    this.quantity = '1 шт',
    this.category = .bakery,
  });

  String name;
  String quantity;
  BuyingCategory category;
}
