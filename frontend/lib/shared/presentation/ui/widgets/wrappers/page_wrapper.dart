import 'package:flutter/material.dart';

class PageWrapper extends StatelessWidget {
  const PageWrapper({super.key, required this.children});

  final List<Widget> children;

  @override
  Widget build(BuildContext context) {
    return CustomScrollView(
      slivers: [
        SliverFillRemaining(
          hasScrollBody: false,
          child: Container(
            padding: .only(top: 60, left: 20, right: 20, bottom: 20),
            width: .infinity,
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
