import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:frontend/shared/domain/models/rule.dart';
import 'package:frontend/shared/presentation/widgets/buttons/item_control_button.dart';

class RuleListTile extends StatelessWidget {
  const RuleListTile({super.key, required this.rule, this.onRemove});

  final Rule rule;
  final Future<void> Function(int)? onRemove;

  @override
  Widget build(BuildContext context) {
    final redact = onRemove != null;

    return Container(
      padding: .symmetric(horizontal: 20, vertical: 15),
      child: Row(
        spacing: 16,
        crossAxisAlignment: .start,
        children: [
          SvgPicture.asset(
            "assets/icons/pin_colored.svg",
            width: 20,
            height: 20,
          ),
          Expanded(
            child: Text(
              rule.text,
              style: TextStyle(fontSize: 14, fontWeight: .w500),
            ),
          ),
          if (redact)
            ItemControlButton(
              onTap: () => onRemove!(rule.id),
              size: 24,
              add: false,
            ),
        ],
      ),
    );
  }
}
