import 'package:frontend/shared/data/types/buying_category.dart';

class BuyingRedacted {
  BuyingRedacted({
    this.name = "",
    this.quantity = '1 шт',
    this.category = .bakery,
    this.isNameError = false,
    this.isQuantityError = false,
  });

  String name;
  String quantity;
  BuyingCategory category;
  bool isNameError;
  bool isQuantityError;
}
