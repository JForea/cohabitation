import 'package:flutter/material.dart';

class EnterButton extends StatelessWidget {
  const EnterButton({
    super.key,
    required this.filled,
    required this.onTap,
    required this.mainText,
    required this.subText,
    required this.icon,
  });

  final bool filled;
  final VoidCallback onTap;
  final String mainText;
  final String subText;
  final IconData icon;

  @override
  Widget build(BuildContext context) {
    return Material(
      borderRadius: .all(.circular(15)),
      clipBehavior: .antiAlias,
      color: Colors.transparent,
      child: Ink(
        padding: .symmetric(vertical: 20, horizontal: 15),
        width: .infinity,
        decoration: BoxDecoration(
          gradient: filled
              ? LinearGradient(
                  colors: [
                    Theme.of(context).colorScheme.primary,
                    Theme.of(context).colorScheme.secondary,
                  ],
                  begin: .topLeft,
                  end: .bottomRight,
                )
              : null,
          borderRadius: .all(.circular(15)),
          border: filled ? null : .all(width: 1, color: Color(0xFFE2E0FF)),
        ),
        child: InkWell(
          onTap: onTap,
          borderRadius: .all(.circular(15)),
          child: Row(
            crossAxisAlignment: .start,
            spacing: 10,
            children: [
              Container(
                height: 24,
                width: 24,
                decoration: BoxDecoration(
                  borderRadius: .all(.circular(20)),
                  color: Color(filled ? 0xFF8D87FF : 0xFFEDE9FF),
                ),
                child: Center(
                  child: Icon(
                    icon,
                    color: filled
                        ? Colors.white
                        : Theme.of(context).colorScheme.primary,
                    size: 18,
                  ),
                ),
              ),
              Column(
                crossAxisAlignment: .start,
                spacing: 10,
                children: [
                  Text(
                    mainText,
                    style: TextStyle(
                      color: filled ? Colors.white : Colors.black,
                      fontWeight: .w700,
                      fontSize: 14,
                    ),
                  ),
                  Text(
                    subText,
                    style: TextStyle(
                      color: filled
                          ? Color(0xFFE5E5E5)
                          : Theme.of(context).colorScheme.onSurfaceVariant,
                      fontWeight: .w500,
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
