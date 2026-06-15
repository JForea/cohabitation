import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/failures/failures.dart';
import 'package:frontend/shared/data/filters/expenses_filter.dart';
import 'package:frontend/shared/data/models/expenses_info.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/repositories/expense_repository.dart';
import 'package:frontend/shared/data/types/expense_category.dart';
import 'package:image_picker/image_picker.dart';

final expensesProvider = AsyncNotifierProvider(ExpensesNotifier.new);

class ExpensesNotifier extends AsyncNotifier<ExpensesInfo> {
  late ExpensesFilter _filter = ExpensesFilter(
    month: DateTime(DateTime.now().year, DateTime.now().month),
  );

  late ExpenseRepository _expenseRepository;

  late int? _apartmentId;

  static const _pageSize = 20;

  int _page = 0;
  bool _hasMore = true;
  bool _isLoading = false;

  @override
  Future<ExpensesInfo> build() async {
    _expenseRepository = ref.read(expenseRepositoryProvider);
    _apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));

    if (_apartmentId == null) throw NotInApartmentFailure();

    _page = 0;
    _hasMore = true;
    _isLoading = false;

    return ExpensesInfo(
      currentExpenseAmount: await _expenseRepository.getExpenseAmount(
        _apartmentId!,
      ),
      expenses: await _expenseRepository.getPage(
        apartmentId: _apartmentId!,
        page: _page,
        pageSize: _pageSize,
        filter: _filter,
      ),
    );
  }

  Future<void> loadMore() async {
    if (_isLoading || !_hasMore) return;

    if (_apartmentId == null) throw NotInApartmentFailure();

    _isLoading = true;

    final previous = state.value;
    if (previous == null) return;

    try {
      final expenses = await _expenseRepository.getPage(
        apartmentId: _apartmentId!,
        page: _page + 1,
        pageSize: _pageSize,
        filter: _filter,
      );

      _page++;

      if (expenses.length < _pageSize) {
        _hasMore = false;
      }

      state = AsyncData(
        previous.copyWith(expenses: [...previous.expenses, ...expenses]),
      );
    } finally {
      _isLoading = false;
    }
  }

  Future<void> create({
    required String name,
    required int amount,
    required ExpenseCategory category,
    required Profile createdBy,
    XFile? image,
  }) async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    try {
      _isLoading = true;

      final expense = await _expenseRepository.create(
        apartmentId: _apartmentId!,
        name: name,
        amount: amount,
        category: category,
        createdBy: createdBy,
        image: image,
      );

      final previousValue =
          state.value ?? ExpensesInfo(currentExpenseAmount: 0, expenses: []);

      state = AsyncData(
        previousValue.copyWith(expenses: [expense, ...previousValue.expenses]),
      );
    } finally {
      _isLoading = false;
    }
  }

  void addExpenseAmount(int amount) {
    final current = state.value;

    if (current == null) {
      return;
    }

    state = AsyncData(
      current.copyWith(
        currentExpenseAmount: current.currentExpenseAmount + amount,
      ),
    );
  }

  Future<void> deleteMany(List<int> ids) async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    final previous = state.value;

    final previousExpenses = previous?.expenses;

    if (previous == null) return;

    final current = previous.expenses
        .where((e) => !ids.contains(e.id))
        .toList();

    state = AsyncData(previous.copyWith(expenses: current));

    try {
      await _expenseRepository.deleteMany(_apartmentId!, ids);
    } catch (e) {
      state = AsyncData(previous.copyWith(expenses: previousExpenses));

      rethrow;
    }
  }

  Future<void> refresh({bool fullRefresh = false}) async {
    if (_apartmentId == null) {
      throw NotInApartmentFailure();
    }

    if (_isLoading) return;

    final previousState = state;
    final previousPage = _page;
    final previousHasMore = _hasMore;

    try {
      _isLoading = true;

      if (fullRefresh) {
        state = const AsyncLoading();
      }

      final expenses = await _expenseRepository.getPage(
        apartmentId: _apartmentId!,
        page: 0,
        pageSize: _pageSize,
        filter: _filter,
      );

      final amount = await _expenseRepository.getExpenseAmount(_apartmentId!);

      _page = 0;
      _hasMore = expenses.length >= _pageSize;

      state = AsyncData(
        ExpensesInfo(currentExpenseAmount: amount, expenses: expenses),
      );
    } catch (e) {
      _page = previousPage;
      _hasMore = previousHasMore;
      state = previousState;
      rethrow;
    } finally {
      _isLoading = false;
    }
  }

  Future<void> setFilter(ExpensesFilter filter) async {
    _filter = filter;
    await refresh();
  }
}
