import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/theme/app_shadows.dart';

class InviteCodeInput extends StatefulWidget {
  const InviteCodeInput({
    super.key,
    this.text = "",
    required this.title,
    required this.hintText,
    required this.onChange,
  });

  final String text;
  final String title;
  final String hintText;
  final void Function(String) onChange;

  @override
  State<StatefulWidget> createState() => _InviteCodeInputState();
}

class _InviteCodeInputState extends State<InviteCodeInput> {
  final _textEditingController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _textEditingController.value = _textEditingController.value.copyWith(
      text: widget.text,
    );
  }

  @override
  void dispose() {
    _textEditingController.dispose();
    super.dispose();
  }

  @override
  void didUpdateWidget(InviteCodeInput oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (oldWidget != widget) {
      if (_textEditingController.text != widget.text) {
        _textEditingController.value = _textEditingController.value.copyWith(
          text: widget.text,
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final textColor = Theme.of(context).colorScheme.onSurfaceVariant;

    return Column(
      crossAxisAlignment: .start,
      spacing: 8,
      children: [
        Text(
          widget.title.toUpperCase(),
          style: TextStyle(fontSize: 12, fontWeight: .w700, color: textColor),
        ),
        Container(
          padding: .symmetric(vertical: 4, horizontal: 16),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: .all(.circular(16)),
            boxShadow: [AppShadows.standard()],
          ),
          child: TextField(
            controller: _textEditingController,
            onChanged: widget.onChange,
            decoration: InputDecoration(
              border: .none,
              hintText: widget.hintText,
              hintStyle: TextStyle(
                fontSize: 18,
                color: Color(0xFF828282),
                letterSpacing: 3,
                fontWeight: .w800,
              ),
            ),
            style: TextStyle(
              fontSize: 18,
              color: AppColors.blue,
              letterSpacing: 3,
              fontWeight: .w800,
            ),
            textAlign: .center,
            inputFormatters: [
              TextInputFormatter.withFunction((oldValue, newValue) {
                return newValue.copyWith(
                  text: newValue.text.toUpperCase(),
                  selection: newValue.selection,
                );
              }),
            ],
          ),
        ),
      ],
    );
  }
}
