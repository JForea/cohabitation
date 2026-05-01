import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/home/presentation/ui/widgets/navigation/custom_bottom_nav_bar.dart';
import 'package:frontend/shared/data/providers/page_provider.dart';

class HomePage extends ConsumerStatefulWidget {
  const HomePage({super.key});

  @override
  ConsumerState<ConsumerStatefulWidget> createState() => _HomePageState();
}

class _HomePageState extends ConsumerState<HomePage> {
  late final PageController controller;

  @override
  void initState() {
    super.initState();
    controller = PageController();
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final currentlyActive = ref.watch(pageProvider);

    ref.listen(pageProvider, (prev, next) {
      if (!controller.position.isScrollingNotifier.value &&
          controller.page?.round() != next) {
        controller.animateToPage(
          next,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      }
    });

    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.surface,
      body: PageView(
        controller: controller,
        onPageChanged: (i) => ref.read(pageProvider.notifier).setIndex(i),
        physics: const BouncingScrollPhysics(),
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
