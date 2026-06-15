import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/types/bubble.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_icon_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/other/bubble_widget.dart';
import 'package:intl/intl.dart';

class MonthlyExpensesCard extends StatelessWidget {
  const MonthlyExpensesCard({
    super.key,
    required this.budget,
    required this.currentExpenses,
    required this.onSettingsClick,
  });

  final int currentExpenses;
  final int budget;
  final VoidCallback? onSettingsClick;

  @override
  Widget build(BuildContext context) {
    final formatter = NumberFormat('#,###', 'ru_RU');

    final TextStyle minorTextStyle = TextStyle(
      color: Colors.white,
      fontSize: 12,
      fontWeight: .w500,
    );

    final statsTextStyle = TextStyle(
      color: Colors.white,
      fontSize: 14,
      fontWeight: .w800,
    );

    return Container(
      width: .infinity,
      padding: .only(top: 20, left: 25, right: 15, bottom: 20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [AppColors.blue, Color(0xFF988CFF)],
          begin: .topLeft,
          end: .bottomRight,
        ),
        borderRadius: .all(.circular(20)),
      ),
      child: Stack(
        children: [
          BubbleWidget(bubble: Bubble(x: 0.55, y: 0, size: 60)),

          Column(
            crossAxisAlignment: .start,
            children: [
              Text("Потрачено в этом месяце", style: minorTextStyle),
              SizedBox(height: 8),
              Text(
                "${formatter.format(currentExpenses)} ₽ / ${formatter.format(budget)} ₽",
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 16,
                  fontWeight: .w800,
                ),
              ),
              SizedBox(height: 12),
              IntrinsicHeight(
                child: Row(
                  spacing: 15,
                  children: [
                    Column(
                      children: [
                        SizedBox(height: 8),
                        Text("баланс", style: minorTextStyle),
                        SizedBox(height: 8),
                        Text(
                          "${budget < currentExpenses ? "-" : "+"} ${formatter.format((budget - currentExpenses).abs())} ₽",
                          style: statsTextStyle,
                        ),
                      ],
                    ),
                    VerticalDivider(
                      color: Colors.white,
                      radius: .all(.circular(2)),
                    ),
                    Column(
                      children: [
                        Text("записей", style: minorTextStyle),
                        Text("о расходах", style: minorTextStyle),
                        Text("2", style: statsTextStyle),
                      ],
                    ),
                  ],
                ),
              ),
            ],
          ),
          if (onSettingsClick != null)
            Align(
              alignment: .topRight,
              child: CustomIconButton(
                color: Color(0xFFAAA3FF),
                icon: Icons.settings_outlined,
                size: 28,
                onPressed: onSettingsClick!,
                iconColor: Colors.white,
              ),
            ),
        ],
      ),
    );
  }
}
