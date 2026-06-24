import 'package:frontend/core/utils/util_functions.dart';

class ExpenseValidators {
  ExpenseValidators._internal();

  static String? validateName(String name) {
    if (name.length < 2 && name.length > 64) return "От 2 до 64 символов";
    return null;
  }

  static int? parsePrice(String price) {
    try {
      int parsed = UtilFunctions.parsePrice(price);
      if (parsed <= 0 || parsed > 1000000) return null;
      return parsed;
    } catch (_) {
      return null;
    }
  }
}
