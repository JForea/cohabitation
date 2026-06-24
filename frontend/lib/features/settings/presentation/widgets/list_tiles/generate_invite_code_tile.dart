import 'package:flutter/material.dart';
import 'package:frontend/app/theme/app_colors.dart';
import 'package:frontend/shared/presentation/widgets/buttons/custom_icon_button.dart';

class GenerateInviteCodeTile extends StatelessWidget {
  const GenerateInviteCodeTile({
    super.key,
    required this.inviteCode,
    required this.onCopy,
    required this.onGenerate,
  });

  final String? inviteCode;
  final VoidCallback onCopy;
  final VoidCallback onGenerate;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: .symmetric(vertical: 15, horizontal: 20),
      child: Column(
        crossAxisAlignment: .start,
        spacing: 10,
        children: [
          Text(
            "Пригласительный код",
            style: TextStyle(
              color: Theme.of(context).colorScheme.onSurfaceVariant,
              fontSize: 13,
              fontWeight: .w500,
            ),
          ),
          Row(
            spacing: 8,
            children: [
              Expanded(
                child: Container(
                  padding: .symmetric(horizontal: 16, vertical: 10),
                  decoration: BoxDecoration(
                    color: AppColors.blue.withAlpha(37),
                    borderRadius: .all(.circular(10)),
                  ),
                  child: Text(
                    inviteCode ?? "00000000",
                    style: TextStyle(
                      color: AppColors.blue.withAlpha(
                        inviteCode != null ? 255 : 90,
                      ),
                      letterSpacing: 1,
                      fontSize: 17,
                      fontWeight: .w800,
                    ),
                  ),
                ),
              ),
              CustomIconButton(
                color: AppColors.blue.withAlpha(37),
                icon: Icons.copy,
                size: 42,
                onPressed: onCopy,
                iconColor: AppColors.blue,
              ),
              CustomIconButton(
                color: AppColors.blue,
                icon: Icons.casino,
                size: 42,
                onPressed: onGenerate,
                iconColor: Colors.white,
              ),
            ],
          ),
        ],
      ),
    );
  }
}
