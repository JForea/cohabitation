import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';

class MessageSnackBar extends SnackBar {
  MessageSnackBar({
    super.key,
    required String message,
    super.duration = const Duration(seconds: 2),
  }) : super(
         content: Row(
           children: [
             const Icon(Icons.check_circle, color: AppColors.blue),
             const SizedBox(width: 12),
             Expanded(
               child: Text(
                 message,
                 style: TextStyle(
                   color: Colors.black,
                   fontSize: 14,
                   fontWeight: .w500,
                 ),
               ),
             ),
           ],
         ),
         behavior: .floating,
         margin: const EdgeInsets.all(16),
         shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
         backgroundColor: Colors.white,
         elevation: 8,
       );
}
