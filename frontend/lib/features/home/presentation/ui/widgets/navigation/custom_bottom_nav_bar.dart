import 'package:flutter/material.dart';
import 'package:frontend/features/home/presentation/ui/widgets/navigation/navigation_element.dart';

class CustomBottomNavBar extends StatelessWidget {
  const CustomBottomNavBar({
    super.key,
    required this.currentlyActive,
    required this.setIndex,
  });

  final int currentlyActive;
  final void Function(int) setIndex;

  @override
  Widget build(BuildContext context) {
    final List<String> assetPaths = [
      "assets/icons/home.svg",
      "assets/icons/task_list.svg",
      "assets/icons/shopping_cart.svg",
      "assets/icons/wallet.svg",
      "assets/icons/user.svg",
    ];

    final List<String> names = [
      "Главная",
      "Задачи",
      "Покупки",
      "Расходы",
      "Профиль",
    ];

    return Container(
      padding: .symmetric(vertical: 15, horizontal: 20),
      width: .infinity,
      decoration: BoxDecoration(color: Colors.white),
      child: Row(
        mainAxisAlignment: .spaceBetween,
        children: [
          for (int i = 0; i < 5; i++)
            NavigationElement(
              onTap: () => setIndex(i),
              assetPath: assetPaths[i],
              isActive: i == currentlyActive,
              text: names[i],
            ),
        ],
      ),
    );
  }
}
