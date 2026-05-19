import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/expenses/presentation/ui/widgets/cards/inhabitant_expenses_amount_summary_card.dart';
import 'package:frontend/features/expenses/presentation/ui/widgets/lists/expense_list.dart';
import 'package:frontend/shared/data/filters/expenses_filter.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/data/providers/expenses_provider.dart';
import 'package:frontend/shared/data/providers/neighbours_provider.dart';
import 'package:frontend/shared/data/providers/user_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/lists/profile_list.dart';
import 'package:frontend/shared/presentation/ui/widgets/other/empty_message_widget.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/page_wrapper.dart';

class ExpensesDetailsByProfilePage extends ConsumerStatefulWidget {
  const ExpensesDetailsByProfilePage({super.key});

  @override
  ConsumerState<ConsumerStatefulWidget> createState() =>
      _ExpensesDetailsByProfilePageState();
}

class _ExpensesDetailsByProfilePageState
    extends ConsumerState<ExpensesDetailsByProfilePage> {
  late final ScrollController _scrollController;

  late Profile? _profile;

  late final DateTime _month;

  void selectProfile(Profile profile) {
    setState(() {
      _profile = profile;
    });
  }

  void onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      ref
          .read(
            expensesProvider(
              ExpensesFilter(month: _month, profileId: _profile?.id),
            ).notifier,
          )
          .loadMore();
    }
  }

  @override
  void initState() {
    _profile = ref.read(userProvider.select((u) => u?.profile));
    final now = DateTime.now();
    _month = DateTime(now.year, now.month);

    _scrollController = ScrollController();
    _scrollController.addListener(onScroll);
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final userProfile = ref.watch(userProvider.select((u) => u?.profile));
    final neighboursState = ref.watch(neighboursProvider);
    final expensesState = ref.watch(
      expensesProvider(ExpensesFilter(month: _month, profileId: _profile?.id)),
    );

    return Scaffold(
      body: PageWrapper(
        pageName: "Подробности",
        backButton: true,
        pathIfCantPop: "/",
        bottomFloatingButtonExists: false,
        children: [
          neighboursState.when(
            data: (neighnours) => ProfileList(
              activeId: _profile?.id ?? -1,
              onSelect: selectProfile,
              profiles: [?userProfile, ...neighnours],
            ),
            error: (_, _) => Text("Произошла ошибка при загрузке."),
            loading: () => Center(child: CircularProgressIndicator()),
          ),
          expensesState.when(
            data: (expensesInfo) => Column(
              crossAxisAlignment: .start,
              spacing: 10,
              children: [
                Center(
                  child: InhabitantExpensesAmountSummaryCard(
                    profile: _profile!,
                    apartmentExpenseAmount: expensesInfo.currentExpenseAmount,
                  ),
                ),
                Text(
                  "ИСТОРИЯ",
                  style: TextStyle(
                    color: Theme.of(context).colorScheme.onSurfaceVariant,
                    fontSize: 14,
                    fontWeight: .w700,
                  ),
                ),
                expensesInfo.expenses.isEmpty
                    ? Center(
                        child: EmptyMessageWidget(
                          assetPath: "assets/icons/wallet.svg",
                          message: "Нет записей о расходах",
                          iconSize: 60,
                          fontSize: 14,
                        ),
                      )
                    : ExpenseList(expenses: expensesInfo.expenses),
              ],
            ),
            error: (_, _) => Text("Произошла ошибка при загрузке"),
            loading: () => Center(child: CircularProgressIndicator()),
          ),
        ],
      ),
    );
  }
}
