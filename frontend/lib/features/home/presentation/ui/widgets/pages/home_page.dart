import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/buyings/presentation/ui/widgets/tabs/buyings_tab.dart';
import 'package:frontend/features/home/presentation/ui/widgets/navigation/custom_bottom_nav_bar.dart';
import 'package:frontend/features/home/data/providers/page_provider.dart';
import 'package:frontend/features/profile/presentation/ui/widgets/tabs/profile_tab.dart';
import 'package:frontend/features/tasks/presentation/ui/widgets/tabs/tasks_tab.dart';
import 'package:go_router/go_router.dart';

class HomePage extends ConsumerStatefulWidget {
  const HomePage({super.key});

  @override
  ConsumerState<ConsumerStatefulWidget> createState() => _HomePageState();
}

class _HomePageState extends ConsumerState<HomePage> {
  late final PageController controller;

  Widget? _buildFloatingActionButton(int currentIndex, BuildContext context) {
    return switch (currentIndex) {
      1 => FloatingActionButton(
        onPressed: () => context.push("/tasks/create"),
        shape: CircleBorder(),
        child: Icon(Icons.add, size: 28),
      ),
      2 => FloatingActionButton(
        onPressed: () => context.push("/buyings/create"),
        shape: CircleBorder(),
        child: Icon(Icons.add, size: 28),
      ),
      3 => FloatingActionButton(
        onPressed: () {},
        shape: CircleBorder(),
        child: Icon(Icons.add, size: 28),
      ),
      _ => null,
    };
  }

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
          TasksTab(),
          BuyingsTab(),
          Placeholder(),
          ProfileTab(),
        ],
      ),
      floatingActionButton: _buildFloatingActionButton(
        currentlyActive,
        context,
      ),
      bottomNavigationBar: CustomBottomNavBar(
        currentlyActive: currentlyActive,
        setIndex: (i) => ref.read(pageProvider.notifier).setIndex(i),
      ),
    );
  }
}
