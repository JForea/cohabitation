import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class CustomBackButton extends StatelessWidget {
  const CustomBackButton({super.key});

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
            color: Theme.of(context).colorScheme.surface,
            borderRadius: .all(.circular(10)),
          ),
          child: Icon(Icons.arrow_back, size: 24),
        ),
      ),
    );
  }
}
