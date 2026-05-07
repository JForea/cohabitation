import 'package:flutter/material.dart';

class AppModal extends StatelessWidget {
  const AppModal({super.key, required this.children});

  final List<Widget> children;

  @override
  Widget build(BuildContext context) {
    final mediaQuery = MediaQuery.of(context);

    return Container(
      padding: .all(20),
      width: .infinity,
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: .vertical(top: .circular(30)),
      ),
      child: Column(
        mainAxisSize: .min,
        crossAxisAlignment: .start,
        spacing: 20,
        children: [
          Center(
            child: Container(
              width: mediaQuery.size.width * 0.2,
              height: 5,
              decoration: BoxDecoration(
                borderRadius: .all(.circular(5)),
                color: Color(0xFFD9D9D9),
              ),
            ),
          ),
          SizedBox(),
          ...children,
        ],
      ),
    );
  }
}
