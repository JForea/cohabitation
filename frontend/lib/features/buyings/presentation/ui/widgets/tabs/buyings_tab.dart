import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/buyings/data/providers/buyings_provider.dart';
import 'package:frontend/features/buyings/presentation/ui/widgets/lists/category_buying_list.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/tab_wrapper.dart';

class BuyingsTab extends ConsumerWidget {
  const BuyingsTab({super.key});

  Future<void> _refresh(WidgetRef ref, int apartmentId) async {
    await ref.read(buyingsProvider.notifier).refresh();
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final profile = ref.read(authProvider).value!.user!.profile!;
    int apartmentId = profile.apartmentId;
    final buyingsState = ref.watch(buyingsProvider);

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
            data: (categoryToBuyings) => Column(
              children: [
                ...categoryToBuyings.keys.map(
                  (c) => CategoryBuyingList(
                    category: c,
                    buyings: categoryToBuyings[c]!,
                    onBuyingComplete: (id) => ref
                        .read(buyingsProvider.notifier)
                        .switchBuyingStatus(id, profile),
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
