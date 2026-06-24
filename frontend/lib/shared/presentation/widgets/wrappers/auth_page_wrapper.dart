import 'package:flutter/material.dart';

class AuthPageWrapper extends StatelessWidget {
  const AuthPageWrapper({super.key, required this.children});

  final List<Widget> children;

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.sizeOf(context);
    final isDesktop = size.width >= 1024;

    return CustomScrollView(
      slivers: [
        SliverFillRemaining(
          hasScrollBody: false,
          child: Center(
            child: ConstrainedBox(
              constraints: BoxConstraints(
                maxWidth: isDesktop ? 480 : double.infinity,
              ),
              child: Padding(
                padding: EdgeInsets.symmetric(
                  horizontal: isDesktop ? 0 : size.width * 0.1,
                  vertical: isDesktop ? 48 : size.height * 0.1,
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  spacing: 20,
                  children: children,
                ),
              ),
            ),
          ),
        ),
      ],
    );
  }
}
