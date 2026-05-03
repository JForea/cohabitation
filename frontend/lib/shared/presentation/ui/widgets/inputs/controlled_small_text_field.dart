import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/theme/app_shadows.dart';

class ControlledSmallTextField extends StatefulWidget {
  const ControlledSmallTextField({
    super.key,
    this.text = "",
    required this.hintText,
    required this.onChange,
    required this.secondaryColor,
  });

  final String text;
  final String hintText;
  final void Function(String) onChange;
  final bool secondaryColor;

  @override
  State<StatefulWidget> createState() => _ControlledSmallTextFieldState();
}

class _ControlledSmallTextFieldState extends State<ControlledSmallTextField> {
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
  void didUpdateWidget(ControlledSmallTextField oldWidget) {
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
    return Container(
      padding: .symmetric(horizontal: 10, vertical: 8),
      decoration: BoxDecoration(
        color: widget.secondaryColor
            ? Theme.of(context).colorScheme.surface
            : Colors.white,
        borderRadius: .all(.circular(30)),
        boxShadow: [AppShadows.standard()],
      ),
      child: TextField(
        controller: _textEditingController,
        onChanged: widget.onChange,
        decoration: InputDecoration(
          isDense: true,
          contentPadding: .zero,
          border: .none,
          hintText: widget.hintText,
          hintStyle: TextStyle(fontSize: 14, color: Color(0xFF9A9A9A)),
        ),
        style: TextStyle(fontSize: 14),
      ),
    );
  }
}
