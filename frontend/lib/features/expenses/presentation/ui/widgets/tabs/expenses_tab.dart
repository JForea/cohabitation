import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/expenses/presentation/ui/widgets/cards/monthly_expenses_card.dart';
import 'package:frontend/features/expenses/presentation/ui/widgets/lists/expense_list.dart';
import 'package:frontend/features/expenses/presentation/ui/widgets/lists/inhabitants_expenses_amount_list.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/providers/async_apartment_provider.dart';
import 'package:frontend/shared/data/providers/expenses_provider.dart';
import 'package:frontend/shared/data/providers/neighbours_provider.dart';
import 'package:frontend/shared/data/providers/selected_provider.dart';
import 'package:frontend/shared/data/providers/user_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/inputs/controlled_named_text_field.dart';
import 'package:frontend/shared/presentation/ui/widgets/modals/app_modal.dart';
import 'package:frontend/shared/presentation/ui/widgets/other/empty_message_widget.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/tab_wrapper.dart';
import 'package:frontend/shared/utils/util_functions.dart';

class ExpensesTab extends ConsumerStatefulWidget {
  const ExpensesTab({super.key});

  @override
  ConsumerState<ConsumerStatefulWidget> createState() => _ExpensesTabState();
}

class _ExpensesTabState extends ConsumerState<ExpensesTab> {
  late String budget;

  final String key = "expenses";

  Future<void> refresh(WidgetRef ref) async {
    ref.invalidate(asyncApartmentProvider);
    ref.invalidate(expensesProvider);
    ref.invalidate(selectedProvider(key));
    ref.read(neighboursProvider.notifier).refresh();
  }

  void setBudget(String s) {
    budget = s;
  }

  void onSave() async {
    int budgetValue = UtilFunctions.parsePrice(budget);

    Navigator.pop(context);

    await ref.read(asyncApartmentProvider.notifier).setBudget(budgetValue);
  }

  void onSettingsClick(BuildContext context) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (context) => AppModal(
        children: [
          Text(
            "Настройки квартиры",
            style: TextStyle(
              color: Theme.of(context).colorScheme.onSurface,
              fontSize: 20,
              fontWeight: .w500,
            ),
          ),
          ControlledNamedTextField(
            title: "Общий бюджет",
            hintText: "50 000 ₽",
            onChange: setBudget,
            secondaryColor: true,
            type: .price,
            require: true,
          ),
          CustomTextButton(onPressed: onSave, text: "Сохранить"),
        ],
      ),
    );
  }

  void onSelect(int id) {
    ref.read(selectedProvider(key).notifier).select(id);
  }

  void onSelectCancel(int id) {
    ref.read(selectedProvider(key).notifier).selectCancel(id);
  }

  @override
  void initState() {
    budget = "";
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final expenseState = ref.watch(expensesProvider);
    final budget = ref.watch(apartmentProvider.select((a) => a?.budget));
    final currentExpenseAmount = ref.watch(
      expensesProvider.select((s) => s.value?.currentExpenseAmount),
    );
    final role = ref.watch(userProvider.select((u) => u?.profile?.role));
    final selected = ref.watch(selectedProvider(key));
    final profile = ref.watch(userProvider.select((u) => u?.profile));
    final neighboursState = ref.watch(neighboursProvider);

    return RefreshIndicator(
      onRefresh: () => refresh(ref),
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
          if (budget != null && currentExpenseAmount != null)
            MonthlyExpensesCard(
              budget: budget,
              currentExpenses: currentExpenseAmount,
              onSettingsClick: role != null && role != .inhabitant
                  ? () => onSettingsClick(context)
                  : null,
            ),

          Text("Статистика", style: TextStyle(fontWeight: .w500, fontSize: 16)),
          neighboursState.when(
            data: (neighbours) => InhabitantsExpensesAmountList(
              inhabitants: [?profile, ...neighbours],
              onTap: () {},
            ),
            error: (_, _) => Text("Не удалось загрузить статистику"),
            loading: () => Center(child: CircularProgressIndicator()),
          ),
          Column(
            crossAxisAlignment: .start,
            spacing: 8,
            children: [
              Text(
                "История",
                style: TextStyle(fontWeight: .w500, fontSize: 16),
              ),
              expenseState.when(
                data: (state) => state.expenses.isEmpty
                    ? Center(
                        child: EmptyMessageWidget(
                          iconSize: 60,
                          fontSize: 14,
                          assetPath: "assets/icons/wallet.svg",
                          message: "Пока нет записей о расходах",
                        ),
                      )
                    : ExpenseList(
                        expenses: state.expenses,
                        selected: selected,
                        onSelect: onSelect,
                        onSelectCancel: onSelectCancel,
                      ),

                error: (e, _) => Text("Произошла ошибка при загрузке."),
                loading: () => Center(child: CircularProgressIndicator()),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
