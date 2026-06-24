import 'package:flutter/material.dart';
import 'package:frontend/core/failures/failures.dart';
import 'package:frontend/shared/domain/models/rule.dart';
import 'package:frontend/shared/presentation/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/widgets/dialogs/error_dialog.dart';
import 'package:frontend/shared/presentation/widgets/inputs/controlled_text_field.dart';
import 'package:frontend/shared/presentation/widgets/list_tiles/add_list_tile.dart';
import 'package:frontend/shared/presentation/widgets/list_tiles/rule_list_tile.dart';
import 'package:frontend/shared/presentation/widgets/lists/custom_widget_list.dart';

class RuleList extends StatefulWidget {
  const RuleList({
    super.key,
    required this.rules,
    this.onRuleAdd,
    this.onRuleRemove,
    required this.titleNeeded,
  });

  final List<Rule> rules;
  final Future<void> Function(String)? onRuleAdd;
  final Future<void> Function(int)? onRuleRemove;
  final bool titleNeeded;

  @override
  State<RuleList> createState() => _RuleListState();
}

class _RuleListState extends State<RuleList> {
  late String text;
  late bool addingNow;

  void switchAddingNow() {
    setState(() {
      addingNow = !addingNow;
    });
  }

  void setText(String s) {
    text = s;
  }

  @override
  void initState() {
    text = "";
    addingNow = false;
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return CustomWidgetList(
      title: widget.titleNeeded ? "Правила квартиры" : null,
      danger: false,
      children: [
        ...widget.rules.map(
          (r) => RuleListTile(rule: r, onRemove: widget.onRuleRemove),
        ),
        if (addingNow && widget.onRuleAdd != null)
          Container(
            margin: .symmetric(vertical: 8, horizontal: 16),
            child: Row(
              spacing: 8,
              children: [
                Flexible(
                  child: ControlledTextField(
                    text: text,
                    hintText: "Новое правило...",
                    onChange: setText,
                    secondaryColor: true,
                    padding: .symmetric(vertical: 8, horizontal: 12),
                    borderRadius: 10,
                    fontSize: 12,
                  ),
                ),
                CustomTextButton(
                  onPressed: () async {
                    setState(() {
                      addingNow = false;
                    });
                    try {
                      await widget.onRuleAdd!(text);

                      setState(() {
                        text = "";
                      });
                    } on Failure catch (e) {
                      if (context.mounted) {
                        showErrorDialog(context, e.message);
                      }
                    }
                  },
                  text: "Добавить",
                  paddinH: 10,
                  paddingW: 8,
                  borderRadius: 10,
                  fontSize: 12,
                ),
              ],
            ),
          ),
        if (widget.onRuleAdd != null)
          AddListTile(onAdd: switchAddingNow, text: "Добавить правило"),
      ],
    );
  }
}
