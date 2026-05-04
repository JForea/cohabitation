import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/home/presentation/ui/widgets/previews/neighbours_preview.dart';
import 'package:frontend/shared/data/providers/neighbours_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/tab_wrapper.dart';

class HomeTab extends ConsumerWidget {
  const HomeTab({super.key});

  Future<void> _refresh(WidgetRef ref) async {
    ref.read(neighboursProvider.notifier).refresh();
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final neighbours = ref.watch(neighboursProvider).value;

    return RefreshIndicator(
      onRefresh: () async => _refresh(ref),
      child: TabWrapper(
        floatingButtonExists: false,
        children: [
          NeighboursPreview(neighbours: neighbours?.take(3).toList() ?? []),
        ],
      ),
    );
  }
}
