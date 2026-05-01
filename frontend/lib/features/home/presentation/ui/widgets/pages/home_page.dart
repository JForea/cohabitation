import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/home/presentation/ui/widgets/navigation/custom_bottom_nav_bar.dart';
import 'package:frontend/shared/data/providers/page_provider.dart';

class HomePage extends ConsumerWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final currentlyActive = ref.watch(pageProvider);

    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.surface,
      body: IndexedStack(
        index: currentlyActive,
        children: const [
          Placeholder(),
          Placeholder(),
          Placeholder(),
          Placeholder(),
          Placeholder(),
        ],
      ),
      bottomNavigationBar: CustomBottomNavBar(
        currentlyActive: currentlyActive,
        setIndex: (i) => ref.read(pageProvider.notifier).setIndex(i),
      ),
    );
  }
}
