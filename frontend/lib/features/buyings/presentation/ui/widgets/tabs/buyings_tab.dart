import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/providers/buyings_provider.dart';
import 'package:frontend/features/buyings/presentation/ui/widgets/lists/category_buying_list.dart';
import 'package:frontend/shared/data/providers/selected_provider.dart';
import 'package:frontend/shared/data/providers/user_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/other/empty_message_widget.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/tab_wrapper.dart';

class BuyingsTab extends ConsumerWidget {
  const BuyingsTab({super.key});

  final String selectedKey = "buyings";

  Future<void> _refresh(WidgetRef ref, int apartmentId) async {
    ref.invalidate(selectedProvider(selectedKey));
    await ref.read(buyingsProvider.notifier).refresh();
  }

  void onSelect(WidgetRef ref, int id) {
    ref.read(selectedProvider(selectedKey).notifier).select(id);
  }

  void onSelectCancel(WidgetRef ref, int id) {
    ref.read(selectedProvider(selectedKey).notifier).selectCancel(id);
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final profile = ref.watch(userProvider.select((u) => u!.profile!));
    int apartmentId = profile.apartmentId;
    final buyingsState = ref.watch(buyingsProvider);
    final buyingsSelected = ref.watch(selectedProvider(selectedKey));

    return RefreshIndicator(
      onRefresh: () => _refresh(ref, apartmentId),
      child: TabWrapper(
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
