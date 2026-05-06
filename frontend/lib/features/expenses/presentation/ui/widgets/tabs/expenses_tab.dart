import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/expenses/presentation/ui/widgets/cards/monthly_expenses_card.dart';
import 'package:frontend/features/expenses/presentation/ui/widgets/lists/expense_list.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/providers/expenses_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/tab_wrapper.dart';
import 'package:frontend/shared/utils/util_functions.dart';

class ExpensesTab extends ConsumerWidget {
  const ExpensesTab({super.key});

  Future<void> _refresh(WidgetRef ref) async {
    ref.read(expensesProvider.notifier).refresh();
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final expenseState = ref.watch(expensesProvider);
    final budget = ref.watch(apartmentProvider.select((a) => a?.budget));
    final currentExpenses = ref.watch(
      apartmentProvider.select((a) => a?.currentExpenseSum),
    );

    return RefreshIndicator(
      onRefresh: () => _refresh(ref),
      child: TabWrapper(
        floatingButtonExists: true,
        appBarExists: false,
        children: [
          Column(
            crossAxisAlignment: .start,
            spacing: 8,
            children: [
              Text(
                "Расходы",
                style: TextStyle(
                  color: Theme.of(context).colorScheme.onSurface,
                  fontSize: 20,
                  fontWeight: .w500,
                ),
              ),
              Text(
                UtilFunctions.toExpensesPageDateString(DateTime.now()),
                style: TextStyle(
                  color: Theme.of(context).colorScheme.onSurfaceVariant,
                  fontSize: 14,
                  fontWeight: .w500,
                ),
              ),
            ],
          ),
          if (budget != null && currentExpenses != null)
            MonthlyExpensesCard(
              budget: budget,
              currentExpenses: currentExpenses,
            ),
          expenseState.when(
            data: (expenses) => ExpenseList(expenses: expenses),
            error: (e, _) => Text("Произошла ошибка при загрузке."),
            loading: () => Center(child: CircularProgressIndicator()),
          ),
        ],
      ),
    );
  }
}
