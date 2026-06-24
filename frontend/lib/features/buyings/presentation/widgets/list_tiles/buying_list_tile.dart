import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/models/buying.dart';
import 'package:frontend/shared/data/providers/selected_provider.dart';
import 'package:frontend/app/theme/app_colors.dart';
import 'package:frontend/app/theme/app_decorations.dart';
import 'package:frontend/shared/presentation/ui/widgets/avatars/avatar.dart';
import 'package:frontend/shared/presentation/ui/widgets/checkboxes/status_checkbox.dart';

class BuyingListTile extends ConsumerWidget {
  const BuyingListTile({
    super.key,
    required this.buying,
    required this.onComplete,
    this.onSelect,
    this.onSelectCancel,
    this.selectionMode,
  });

  final Buying buying;
  final void Function(int) onComplete;
  final void Function(int)? onSelect;
  final void Function(int)? onSelectCancel;
  final bool? selectionMode;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final isSelected = ref.watch(
      selectedProvider("buyings").select((s) => s.contains(buying.id)),
    );

    void handleTap() {
      if (isSelected) {
        onSelectCancel?.call(buying.id);
        return;
      }

      if (selectionMode != null && selectionMode!) {
        onSelect?.call(buying.id);
        return;
      }
    }

    void handleLongPress() {
      if (!isSelected) {
        onSelect?.call(buying.id);
      }
    }

    return GestureDetector(
      onTap: handleTap,
      onLongPress: handleLongPress,
      child: Container(
        padding: .symmetric(horizontal: 16, vertical: 8),
        decoration: AppDecorations.listTileDecoration(
          context: context,
          selected: isSelected,
        ),
        width: .infinity,
        child: Row(
          children: [
            StatusCheckbox(
              checked: buying.completedBy != null,
              uncheckedColor: AppColors.greyBlue,
              onCheck: () => onComplete(buying.id),
            ),
            SizedBox(width: 18),
            Expanded(
              child: Column(
                crossAxisAlignment: .start,
                children: [
                  Text(
                    buying.name,
                    maxLines: 1,
                    overflow: .ellipsis,
                    style: TextStyle(
                      color: isSelected
                          ? AppColors.blue
                          : Theme.of(context).colorScheme.onSurface,
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
                          maxLines: 1,
                          overflow: .ellipsis,
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
            ),
          ],
        ),
      ),
    );
  }
}
