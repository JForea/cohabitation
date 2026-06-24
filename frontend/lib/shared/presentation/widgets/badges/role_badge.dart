import 'package:flutter/material.dart';
import 'package:frontend/shared/data/types/role.dart';
import 'package:frontend/app/theme/app_colors.dart';
import 'package:frontend/core/utils/util_functions.dart';

class RoleBadge extends StatelessWidget {
  const RoleBadge({super.key, required this.role});

  final Role role;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: .symmetric(vertical: 2, horizontal: 10),
      decoration: BoxDecoration(
        color: AppColors.blue,
        borderRadius: .all(.circular(20)),
      ),
      child: Text(
        UtilFunctions.getDisplayNameFromT(role),
        style: TextStyle(color: Colors.white, fontSize: 12, fontWeight: .w500),
      ),
    );
  }
}
