import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/theme/app_shadows.dart';
import 'package:go_router/go_router.dart';

class CustomBackButton extends StatelessWidget {
  const CustomBackButton({super.key, required this.mainColor});

  final bool mainColor;

  @override
  Widget build(BuildContext context) {
    return Ink(
      child: InkWell(
        onTap: () => context.pop(),
        borderRadius: .all(.circular(10)),
        child: Container(
          width: 42,
          height: 42,
          alignment: .center,
          decoration: BoxDecoration(
            color: mainColor
                ? Theme.of(context).colorScheme.surface
                : Theme.of(context).colorScheme.surfaceContainer,
            borderRadius: .all(.circular(10)),
            boxShadow: [AppShadows.standard()],
          ),
          child: Icon(Icons.arrow_back, size: 24),
        ),
      ),
    );
  }
}
