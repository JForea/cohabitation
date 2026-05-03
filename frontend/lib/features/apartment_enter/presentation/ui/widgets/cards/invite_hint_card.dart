import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';

class InviteHintCard extends StatelessWidget {
  const InviteHintCard({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: .symmetric(vertical: 15, horizontal: 20),
      decoration: BoxDecoration(
        color: AppColors.blue.withAlpha(37),
        borderRadius: .all(.circular(16)),
      ),
      child: Row(
        spacing: 12,
        children: [
          SvgPicture.asset("icons/hint.svg", width: 28, height: 28),
          Expanded(
            child: Text(
              "Код можно найти в настройках квартиры у администратора",
              style: TextStyle(
                color: AppColors.blue,
                fontSize: 14,
                fontWeight: .w500,
              ),
            ),
          ),
        ],
      ),
    );
  }
}
