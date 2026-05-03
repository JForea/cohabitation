import 'package:flutter/material.dart';

class TabWrapper extends StatelessWidget {
  const TabWrapper({
    super.key,
    required this.children,
    required this.floatingButtonExists,
  });

  final List<Widget> children;
  final bool floatingButtonExists;

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      physics: AlwaysScrollableScrollPhysics(),
      child: Container(
        padding: .only(
          top: 60,
          left: 20,
          right: 20,
          bottom: floatingButtonExists ? 90 : 20,
        ),
        width: .infinity,
        child: Column(
          crossAxisAlignment: .start,
          spacing: 20,
          children: children,
        ),
      ),
    );
  }
}
