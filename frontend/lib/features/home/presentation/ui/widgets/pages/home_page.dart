import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/buyings/presentation/ui/widgets/tabs/buyings_tab.dart';
import 'package:frontend/features/expenses/presentation/ui/widgets/tabs/expenses_tab.dart';
import 'package:frontend/features/home/presentation/ui/widgets/navigation/custom_bottom_nav_bar.dart';
import 'package:frontend/features/home/data/providers/page_provider.dart';
import 'package:frontend/features/home/presentation/ui/widgets/tabs/home_tab.dart';
import 'package:frontend/features/profile/presentation/ui/widgets/tabs/profile_tab.dart';
import 'package:frontend/features/tasks/presentation/ui/widgets/tabs/tasks_tab.dart';
import 'package:frontend/shared/data/providers/user_provider.dart';
import 'package:frontend/shared/data/types/role.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_app_floating_action_button.dart';
import 'package:go_router/go_router.dart';

class HomePage extends ConsumerStatefulWidget {
  const HomePage({super.key});

  @override
  ConsumerState<ConsumerStatefulWidget> createState() => _HomePageState();
}

class _HomePageState extends ConsumerState<HomePage> {
  late final PageController controller;

  Widget? _buildFloatingActionButton(
    int currentIndex,
    Role? role,
    BuildContext context,
  ) {
    return switch (currentIndex) {
      1 =>
        role != Role.inhabitant
            ? CustomAppFloatingActionButton(
                onPressed: () => context.push("/tasks/create"),
              )
            : null,
      2 => CustomAppFloatingActionButton(
        onPressed: () => context.push("/buyings/create"),
      ),
      3 => CustomAppFloatingActionButton(
        onPressed: () => context.push("/expenses/create"),
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
    final role = ref.watch(userProvider.select((u) => u!.profile!.role));

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
          HomeTab(),
          TasksTab(),
          BuyingsTab(),
          ExpensesTab(),
          ProfileTab(),
        ],
      ),
      floatingActionButton: _buildFloatingActionButton(
        currentlyActive,
        role,
        context,
      ),
      bottomNavigationBar: CustomBottomNavBar(
        currentlyActive: currentlyActive,
        setIndex: (i) => ref.read(pageProvider.notifier).setIndex(i),
      ),
    );
  }
}
