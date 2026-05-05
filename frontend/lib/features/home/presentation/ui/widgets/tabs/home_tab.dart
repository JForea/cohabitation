import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/home/presentation/ui/widgets/app_bars/home_app_bar.dart';
import 'package:frontend/features/home/presentation/ui/widgets/previews/neighbours_preview.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/providers/neighbours_provider.dart';
import 'package:frontend/shared/data/providers/user_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/tab_wrapper.dart';

class HomeTab extends ConsumerWidget {
  const HomeTab({super.key});

  Future<void> _refresh(WidgetRef ref) async {
    ref.read(neighboursProvider.notifier).refresh();
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final neighboursState = ref.watch(neighboursProvider);
    final name = ref.watch(userProvider.select((u) => u?.profile?.name));
    final address = ref.watch(apartmentProvider.select((a) => a?.address));

    return RefreshIndicator(
      onRefresh: () async => _refresh(ref),
      child: Column(
        children: [
          HomeAppBar(userName: name ?? "", address: address),
          TabWrapper(
            floatingButtonExists: false,
            appBarExists: true,
            children: [
              neighboursState.when(
                data: (neighbours) =>
                    NeighboursPreview(neighbours: neighbours.take(3).toList()),
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
