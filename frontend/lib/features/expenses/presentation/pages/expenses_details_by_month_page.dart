import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/expenses/data/providers/expenses_amount_by_category_provider.dart';
import 'package:frontend/features/expenses/presentation/widgets/cards/expenses_amount_by_categories_card.dart';
import 'package:frontend/features/expenses/presentation/widgets/lists/expense_list.dart';
import 'package:frontend/shared/data/filters/expenses_filter.dart';
import 'package:frontend/shared/state/providers/apartment_provider.dart';
import 'package:frontend/shared/state/providers/expenses_provider.dart';
import 'package:frontend/shared/presentation/widgets/buttons/page_control_button.dart';
import 'package:frontend/shared/presentation/widgets/other/empty_message_widget.dart';
import 'package:frontend/shared/presentation/widgets/texts/default_load_error_text.dart';
import 'package:frontend/shared/presentation/widgets/wrappers/page_wrapper.dart';
import 'package:intl/intl.dart';

class ExpensesDetailsByMonthPage extends ConsumerStatefulWidget {
  const ExpensesDetailsByMonthPage({super.key});

  @override
  ConsumerState<ExpensesDetailsByMonthPage> createState() =>
      _ExpensesDetailsByMonthPageState();
}

class _ExpensesDetailsByMonthPageState
    extends ConsumerState<ExpensesDetailsByMonthPage> {
  late final ScrollController _scrollController;

  late final DateTime _now;
  late DateTime _month;

  void nextMonth() {
    setState(() {
      _month = DateTime(_month.year, _month.month + 1);
      ref
          .read(expensesProvider.notifier)
          .setFilter(
            ExpensesFilter(category: null, month: _month, profileId: null),
          );
    });
  }

  void prevMonth() {
    setState(() {
      _month = DateTime(_month.year, _month.month - 1);
      ref
          .read(expensesProvider.notifier)
          .setFilter(
            ExpensesFilter(category: null, month: _month, profileId: null),
          );
    });
  }

  void onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      ref.read(expensesProvider.notifier).loadMore();
    }
  }

  @override
  void initState() {
    super.initState();
    _now = DateTime.now();
    _month = DateTime(_now.year, _now.month);

    _scrollController = ScrollController();
    _scrollController.addListener(onScroll);

    ref
        .read(expensesProvider.notifier)
        .setFilter(
          ExpensesFilter(category: null, month: _month, profileId: null),
        );
  }

  @override
  Widget build(BuildContext context) {
    final expensesState = ref.watch(expensesProvider);
    final currentExpenseAmount = ref.watch(
      expensesProvider.select((s) => s.value?.currentExpenseAmount),
    );
    final expensesAmountByCategoryState = ref.watch(
      expensesAmountByCategoryProvider(_month),
    );
    final apartmentCreatedAt = ref.watch(
      apartmentProvider.select((a) => a?.createdAt),
    );

    String date = DateFormat("LLLL, y", "ru_RU").format(_month);
    date = date[0].toUpperCase() + date.substring(1);

    final controlPageButtonSize = 28.0;

    return Scaffold(
      body: PageWrapper(
        controller: _scrollController,
        backButton: true,
        pageName: "История расходов",
        bottomFloatingButtonExists: false,
        pathIfCantPop: "/",
        children: [
          Center(
            child: Row(
              mainAxisSize: .min,
              spacing: 16,
              children: [
                PageControlButton(
                  size: controlPageButtonSize,
                  active: apartmentCreatedAt != null
                      ? _month.compareTo(
                              DateTime(
                                apartmentCreatedAt.year,
                                apartmentCreatedAt.month,
                              ),
                            ) ==
                            1
                      : false,
                  onTap: prevMonth,
                  type: .previous,
                ),
                SizedBox(
                  width: 160,
                  child: Text(
                    date,
                    textAlign: .center,
                    style: TextStyle(
                      color: Theme.of(context).colorScheme.primary,
                      fontSize: 20,
                      fontWeight: .w600,
                    ),
                  ),
                ),
                PageControlButton(
                  size: controlPageButtonSize,
                  active:
                      _month.compareTo(DateTime(_now.year, _now.month)) == -1,
                  onTap: nextMonth,
                  type: .next,
                ),
              ],
            ),
          ),
          if (currentExpenseAmount != null && currentExpenseAmount != 0)
            expensesAmountByCategoryState.when(
              data: (expensesAmountByCategory) =>
                  ExpensesAmountByCategoriesCard(
                    expensesAmountByCategories: expensesAmountByCategory,
                  ),
              error: (_, _) => SizedBox(),
              loading: () => SizedBox(),
            ),
          expensesState.when(
            data: (expensesInfo) => expensesInfo.expenses.isEmpty
                ? Center(
                    child: EmptyMessageWidget(
                      assetPath: "assets/icons/wallet.svg",
                      message: "За этот месяц нет записей о расходах",
                      fontSize: 14,
                      iconSize: 60,
                    ),
                  )
                : ExpenseList(expenses: expensesInfo.expenses),
            error: (_, _) => defaultLoadErrorText,
            loading: () => Center(child: CircularProgressIndicator()),
          ),
        ],
      ),
    );
  }
}
