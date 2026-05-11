import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/theme/app_shadows.dart';

class ControlledTextField extends StatefulWidget {
  const ControlledTextField({
    super.key,
    this.text = "",
    required this.hintText,
    required this.onChange,
    required this.secondaryColor,
    this.padding = const .symmetric(horizontal: 10, vertical: 8),
    this.borderRadius = 30,
    this.fontSize = 14,
  });

  final String text;
  final String hintText;
  final void Function(String) onChange;
  final bool secondaryColor;
  final EdgeInsets padding;
  final double borderRadius;
  final double fontSize;

  @override
  State<StatefulWidget> createState() => _ControlledTextFieldState();
}

class _ControlledTextFieldState extends State<ControlledTextField> {
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
  void didUpdateWidget(ControlledTextField oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (oldWidget.text != widget.text &&
        _textEditingController.text != widget.text) {
      _textEditingController.text = widget.text;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: widget.padding,
      decoration: BoxDecoration(
        color: widget.secondaryColor
            ? Theme.of(context).colorScheme.surface
            : Colors.white,
        borderRadius: .all(.circular(widget.borderRadius)),
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
          hintStyle: TextStyle(
            fontSize: widget.fontSize,
            color: Color(0xFF9A9A9A),
          ),
        ),
        style: TextStyle(fontSize: widget.fontSize),
      ),
    );
  }
}
