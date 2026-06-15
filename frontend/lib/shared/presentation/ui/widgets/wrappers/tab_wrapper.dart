import 'package:flutter/material.dart';

class TabWrapper extends StatelessWidget {
  const TabWrapper({
    super.key,
    required this.children,
    required this.floatingButtonExists,
    required this.appBarExists,
    this.scrollController,
  });

  final List<Widget> children;
  final bool floatingButtonExists;
  final bool appBarExists;
  final ScrollController? scrollController;

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      controller: scrollController,
      physics: AlwaysScrollableScrollPhysics(),
      child: Container(
        padding: .only(
          top: appBarExists ? 20 : 60,
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
