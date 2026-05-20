import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/filters/buying_filter.dart';
import 'package:frontend/features/buyings/data/providers/buying_filter_index_provider.dart';
import 'package:frontend/features/buyings/presentation/ui/widgets/buttons/avatar_filter_button.dart';
import 'package:frontend/features/buyings/presentation/ui/widgets/buttons/filter_button_wide.dart';
import 'package:frontend/shared/data/providers/buyings_provider.dart';
import 'package:frontend/features/buyings/presentation/ui/widgets/lists/category_buying_list.dart';
import 'package:frontend/shared/data/providers/neighbours_provider.dart';
import 'package:frontend/shared/data/providers/selected_provider.dart';
import 'package:frontend/shared/data/providers/user_provider.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/ui/widgets/other/empty_message_widget.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/tab_wrapper.dart';

class BuyingsTab extends ConsumerStatefulWidget {
  const BuyingsTab({super.key});

  @override
  ConsumerState<ConsumerStatefulWidget> createState() => _BuyingsTabstate();
}

class _BuyingsTabstate extends ConsumerState<BuyingsTab> {
  late final ScrollController _scrollController;

  late final String _selectedKey;

  Future<void> _refresh(WidgetRef ref, int apartmentId) async {
    ref.invalidate(selectedProvider(_selectedKey));
    await ref.read(buyingsProvider.notifier).refresh();
  }

  void onSelect(WidgetRef ref, int id) {
    ref.read(selectedProvider(_selectedKey).notifier).select(id);
  }

  void onSelectCancel(WidgetRef ref, int id) {
    ref.read(selectedProvider(_selectedKey).notifier).selectCancel(id);
  }

  void onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      ref.read(buyingsProvider.notifier).loadMore();
    }
  }

  void selectFilter(BuyingFilter filter, int index) {
    if (index != ref.read(buyingFilterIndexProvider)) {
      ref.read(buyingsProvider.notifier).setFilter(filter);
      ref.read(buyingFilterIndexProvider.notifier).setIndex(index);
    }
  }

  @override
  void initState() {
    super.initState();

    _selectedKey = "buyings";
    _scrollController = ScrollController();
    _scrollController.addListener(onScroll);
  }

  @override
  Widget build(BuildContext context) {
    final profile = ref.watch(userProvider.select((u) => u!.profile!));
    int apartmentId = profile.apartmentId;
    final buyingsState = ref.watch(buyingsProvider);
    final buyingsSelected = ref.watch(selectedProvider(_selectedKey));
    final neighbours = ref.read(neighboursProvider.select((s) => s.value));
    final buyingFilterIndex = ref.read(buyingFilterIndexProvider);

    List<Widget> filterButtons = [
      FilterButtonWide(
        height: 40,
        color: AppColors.greyBlue,
        name: "Общий",
        onTap: () => selectFilter(BuyingFilter(), 0),
        isSelected: buyingFilterIndex == 0,
      ),
      FilterButtonWide(
        height: 40,
        color: AppColors.cyan,
        name: "Личный",
        onTap: () => selectFilter(BuyingFilter(isPublic: false), 1),
        isSelected: buyingFilterIndex == 1,
      ),
      AvatarFilterButton(
        size: 40,
        color: profile.color,
        name: profile.name,
        onTap: () => selectFilter(BuyingFilter(assignedTo: profile.id), 2),
        isSelected: buyingFilterIndex == 2,
      ),
      for (int i = 0; i < (neighbours?.length ?? 0); i++)
        AvatarFilterButton(
          size: 40,
          color: neighbours![i].color,
          name: neighbours[i].name,
          onTap: () =>
              selectFilter(BuyingFilter(assignedTo: neighbours[i].id), 3 + i),
          isSelected: buyingFilterIndex == 3 + i,
        ),
    ];

    return RefreshIndicator(
      onRefresh: () => _refresh(ref, apartmentId),
      child: TabWrapper(
        scrollController: _scrollController,
        appBarExists: false,
        floatingButtonExists: true,
        children: [
          Text(
            "Список покупок",
            style: TextStyle(
              fontSize: 20,
              fontWeight: .w500,
              color: Theme.of(context).colorScheme.onSurface,
            ),
          ),
          SizedBox(
            height: 40,
            child: ListView.builder(
              scrollDirection: .horizontal,
              itemCount: filterButtons.length,
              itemBuilder: (_, i) =>
                  Padding(padding: .only(right: 6), child: filterButtons[i]),
            ),
          ),
          buyingsState.when(
            data: (categoryToBuyings) => categoryToBuyings.isEmpty
                ? Center(
                    child: EmptyMessageWidget(
                      iconSize: 70,
                      fontSize: 16,
                      assetPath: "assets/icons/shopping_cart.svg",
                      message: "Список покупок пуст",
                    ),
                  )
                : Column(
                    spacing: 20,
                    children: [
                      ...categoryToBuyings.keys.map(
                        (c) => CategoryBuyingList(
                          category: c,
                          buyings: categoryToBuyings[c]!,
                          onBuyingComplete: (id) => ref
                              .read(buyingsProvider.notifier)
                              .switchBuyingStatus(id, profile),
                          selected: buyingsSelected,
                          onSelect: (id) => onSelect(ref, id),
                          onSelectCancel: (id) => onSelectCancel(ref, id),
                        ),
                      ),
                    ],
                  ),
            error: (e, _) => Text("При загрузке данных произошла ошибка."),
            loading: () => Center(child: CircularProgressIndicator()),
          ),
        ],
      ),
    );
  }
}
