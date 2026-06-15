import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/buyings/presentation/ui/widgets/tabs/buyings_tab.dart';
import 'package:frontend/features/expenses/presentation/ui/widgets/tabs/expenses_tab.dart';
import 'package:frontend/features/home/data/providers/page_provider.dart';
import 'package:frontend/features/home/presentation/ui/widgets/navigation/custom_bottom_nav_bar.dart';
import 'package:frontend/features/home/presentation/ui/widgets/navigation/custom_side_nav_bar.dart';
import 'package:frontend/features/home/presentation/ui/widgets/tabs/home_tab.dart';
import 'package:frontend/features/profile/presentation/ui/widgets/tabs/profile_tab.dart';
import 'package:frontend/features/tasks/presentation/ui/widgets/tabs/tasks_tab.dart';
import 'package:frontend/shared/data/providers/buyings_provider.dart';
import 'package:frontend/shared/data/providers/expenses_provider.dart';
import 'package:frontend/shared/data/providers/selected_provider.dart';
import 'package:frontend/shared/data/providers/tasks_provider.dart';
import 'package:frontend/shared/data/providers/user_provider.dart';
import 'package:frontend/shared/data/types/role.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_app_floating_action_button.dart';
import 'package:go_router/go_router.dart';

class HomePage extends ConsumerStatefulWidget {
  const HomePage({super.key});

  @override
  ConsumerState<ConsumerStatefulWidget> createState() => _HomePageState();
}

class _HomePageState extends ConsumerState<HomePage> {
  late final PageController controller;
  late final DateTime month;

  Widget? _buildFloatingActionButton(
    int currentIndex,
    Role? role,
    BuildContext context,
  ) {
    final key = switch (currentIndex) {
      1 => "tasks",
      2 => "buyings",
      3 => "expenses",
      _ => "",
    };

    final isEmpty = ref.watch(selectedProvider(key)).isEmpty;

    return switch (currentIndex) {
      1 => CustomAppFloatingActionButton(
        iconData: isEmpty ? Icons.add : Icons.delete,
        color: isEmpty ? AppColors.blue : AppColors.red,
        onPressed: isEmpty
            ? () => context.push("/tasks/create")
            : () async {
                await ref
                    .read(tasksProvider.notifier)
                    .deleteMany(ref.read(selectedProvider(key)).toList());
                ref.invalidate(selectedProvider(key));
              },
      ),
      2 => CustomAppFloatingActionButton(
        iconData: isEmpty ? Icons.add : Icons.delete,
        color: isEmpty ? AppColors.blue : AppColors.red,
        onPressed: isEmpty
            ? () => context.push("/buyings/create")
            : () async {
                await ref
                    .read(buyingsProvider.notifier)
                    .deleteMany(ref.read(selectedProvider(key)).toList());
                ref.invalidate(selectedProvider(key));
              },
      ),
      3 => CustomAppFloatingActionButton(
        iconData: isEmpty ? Icons.add : Icons.delete,
        color: isEmpty ? AppColors.blue : AppColors.red,
        onPressed: isEmpty
            ? () => context.push("/expenses/create")
            : () async {
                await ref
                    .read(expensesProvider.notifier)
                    .deleteMany(ref.read(selectedProvider(key)).toList());
                ref.invalidate(selectedProvider(key));
              },
      ),
      _ => null,
    };
  }

  @override
  void initState() {
    super.initState();
    controller = PageController();

    final now = DateTime.now();
    month = DateTime(now.year, now.month);
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  void _setPage(int index) {
    ref.read(pageProvider.notifier).setIndex(index);
  }

  @override
  Widget build(BuildContext context) {
    final currentlyActive = ref.watch(pageProvider);
    final role = ref.watch(userProvider.select((u) => u!.profile!.role));

    final size = MediaQuery.sizeOf(context);
    final isDesktop = size.width >= 1024;

    ref.listen(pageProvider, (prev, next) {
      if (!controller.position.isScrollingNotifier.value &&
          controller.page?.round() != next) {
        controller.animateToPage(
          next,
          duration: Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      }
    });

    final pageView = PageView(
      controller: controller,
      onPageChanged: (i) => ref.read(pageProvider.notifier).setIndex(i),
      physics: isDesktop
          ? NeverScrollableScrollPhysics()
          : BouncingScrollPhysics(),
      children: [
        HomeTab(),
        TasksTab(),
        BuyingsTab(),
        ExpensesTab(),
        ProfileTab(),
      ],
    );

    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.surface,
      body: isDesktop
          ? Row(
              children: [
                CustomSideNavBar(
                  currentlyActive: currentlyActive,
                  setIndex: _setPage,
                ),
                Expanded(child: pageView),
              ],
            )
          : pageView,
      floatingActionButton: _buildFloatingActionButton(
        currentlyActive,
        role,
        context,
      ),
      bottomNavigationBar: isDesktop
          ? null
          : CustomBottomNavBar(
              currentlyActive: currentlyActive,
              setIndex: _setPage,
            ),
    );
  }
}
