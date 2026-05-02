import 'package:flutter/material.dart';
import 'package:frontend/shared/data/models/buying.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/ui/widgets/avatars/avatar.dart';
import 'package:frontend/shared/presentation/ui/widgets/checkboxes/status_checkbox.dart';

class BuyingListTile extends StatelessWidget {
  const BuyingListTile({
    super.key,
    required this.buying,
    required this.onComplete,
  });

  final Buying buying;
  final void Function(int) onComplete;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: .symmetric(horizontal: 16, vertical: 8),
      width: .infinity,
      child: Row(
        children: [
          StatusCheckbox(
            checked: buying.completedBy != null,
            uncheckedColor: AppColors.greyBlue,
            onCheck: () => onComplete(buying.id),
          ),
          SizedBox(width: 18),
          Column(
            crossAxisAlignment: .start,
            children: [
              Text(
                buying.name,
                style: TextStyle(
                  color: Theme.of(context).colorScheme.onSurface,
                  fontSize: 14,
                  fontWeight: .w500,
                ),
              ),
              SizedBox(height: 4),
              Row(
                spacing: 8,
                children: [
                  Text(
                    buying.quantity,
                    style: TextStyle(
                      color: Color(0xFFA3A3A3),
                      fontSize: 12,
                      fontWeight: .w500,
                    ),
                  ),
                  if (buying.assignedTo != null) ...[
                    Avatar(
                      name: buying.assignedTo!.name,
                      size: 18,
                      color: buying.assignedTo!.color,
                    ),
                    Text(
                      buying.assignedTo!.name,
                      style: TextStyle(
                        color: AppColors.greyBlue,
                        fontSize: 12,
                        fontWeight: .w500,
                      ),
                    ),
                  ],
                ],
              ),
            ],
          ),
        ],
      ),
    );
  }
}
