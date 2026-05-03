import 'package:flutter/material.dart';

class AuthPageWrapper extends StatelessWidget {
  const AuthPageWrapper({super.key, required this.children});

  final List<Widget> children;

  @override
  Widget build(BuildContext context) {
    final mediaQuery = MediaQuery.of(context);

    return CustomScrollView(
      slivers: [
        SliverFillRemaining(
          hasScrollBody: false,
          child: Container(
            padding: .symmetric(
              horizontal: mediaQuery.size.width * 0.1,
              vertical: mediaQuery.size.height * 0.1,
            ),
            child: Column(
              crossAxisAlignment: .start,
              spacing: 20,
              children: children,
            ),
          ),
        ),
      ],
    );
  }
}
