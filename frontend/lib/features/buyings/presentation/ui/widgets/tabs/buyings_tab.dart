import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/buyings/data/providers/buyings_provider.dart';
import 'package:frontend/features/buyings/presentation/ui/widgets/lists/category_buying_list.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/tab_wrapper.dart';

class BuyingsTab extends ConsumerWidget {
  const BuyingsTab({super.key});

  Future<void> _refresh(WidgetRef ref, int apartmentId) async {
    await ref.read(buyingsProvider(apartmentId).notifier).refresh();
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final profile = ref.read(authProvider).user!.profile!;
    int apartmentId = profile.apartmentId;
    final categoryToBuyings = ref.watch(buyingsProvider(apartmentId)).value;

    return RefreshIndicator(
      onRefresh: () => _refresh(ref, apartmentId),
      child: TabWrapper(
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
          ...?categoryToBuyings?.keys.map(
            (c) => CategoryBuyingList(
              category: c,
              buyings: categoryToBuyings[c]!,
              onBuyingComplete: (id) => ref
                  .read(buyingsProvider(apartmentId).notifier)
                  .switchBuyingStatus(id, profile),
            ),
          ),
        ],
      ),
    );
  }
}
