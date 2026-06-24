import 'package:frontend/shared/domain/types/expense_category.dart';

class ExpensesFilter {
  const ExpensesFilter({this.category, required this.month, this.profileId});

  final ExpenseCategory? category;
  final DateTime month;
  final int? profileId;

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        other is ExpensesFilter &&
            other.month == month &&
            other.category == category &&
            other.profileId == profileId;
  }

  @override
  int get hashCode => month.hashCode;
}
