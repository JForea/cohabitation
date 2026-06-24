import 'package:flutter/material.dart';

class CustomSideNavBar extends StatelessWidget {
  const CustomSideNavBar({
    super.key,
    required this.currentlyActive,
    required this.setIndex,
  });

  final int currentlyActive;
  final void Function(int index) setIndex;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 260,
      color: Colors.white,
      padding: EdgeInsets.symmetric(horizontal: 16, vertical: 18),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(height: 8),
          _SidebarItem(
            index: 0,
            selectedIndex: currentlyActive,
            icon: Icons.home_outlined,
            selectedIcon: Icons.home,
            text: 'Главная',
            onTap: setIndex,
          ),
          _SidebarItem(
            index: 1,
            selectedIndex: currentlyActive,
            icon: Icons.check_box_outlined,
            selectedIcon: Icons.check_box,
            text: 'Задачи',
            onTap: setIndex,
          ),
          _SidebarItem(
            index: 2,
            selectedIndex: currentlyActive,
            icon: Icons.shopping_cart_outlined,
            selectedIcon: Icons.shopping_cart,
            text: 'Покупки',
            onTap: setIndex,
          ),
          _SidebarItem(
            index: 3,
            selectedIndex: currentlyActive,
            icon: Icons.account_balance_wallet_outlined,
            selectedIcon: Icons.account_balance_wallet,
            text: 'Расходы',
            onTap: setIndex,
          ),
          _SidebarItem(
            index: 4,
            selectedIndex: currentlyActive,
            icon: Icons.person_outline,
            selectedIcon: Icons.person,
            text: 'Профиль',
            onTap: setIndex,
          ),
        ],
      ),
    );
  }
}

class _SidebarItem extends StatelessWidget {
  const _SidebarItem({
    required this.index,
    required this.selectedIndex,
    required this.icon,
    required this.selectedIcon,
    required this.text,
    required this.onTap,
  });

  final int index;
  final int selectedIndex;
  final IconData icon;
  final IconData selectedIcon;
  final String text;
  final void Function(int index) onTap;

  @override
  Widget build(BuildContext context) {
    final selected = index == selectedIndex;
    final primary = Theme.of(context).colorScheme.primary;

    return Padding(
      padding: EdgeInsets.only(bottom: 6),
      child: Material(
        color: selected ? primary.withAlpha(37) : Colors.transparent,
        borderRadius: BorderRadius.circular(14),
        child: InkWell(
          borderRadius: BorderRadius.circular(14),
          onTap: () => onTap(index),
          child: Container(
            height: 48,
            padding: EdgeInsets.symmetric(horizontal: 14),
            child: Row(
              children: [
                Icon(
                  selected ? selectedIcon : icon,
                  size: 22,
                  color: selected ? primary : Color(0xFF7E7A9A),
                ),
                SizedBox(width: 14),
                Expanded(
                  child: Text(
                    text,
                    style: TextStyle(
                      fontSize: 16,
                      fontWeight: selected ? FontWeight.w600 : FontWeight.w500,
                      color: selected ? primary : Color(0xFF7E7A9A),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
