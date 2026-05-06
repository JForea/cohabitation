import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:frontend/shared/formatters/price_input_formatter.dart';
import 'package:frontend/shared/presentation/theme/app_shadows.dart';

enum InputType { text, password, price }

class ControlledNamedTextField extends StatefulWidget {
  const ControlledNamedTextField({
    super.key,
    this.text = "",
    required this.title,
    required this.hintText,
    required this.onChange,
    required this.secondaryColor,
    required this.type,
    required this.require,
    this.maxLines,
  });

  final String text;
  final String title;
  final String hintText;
  final void Function(String) onChange;
  final bool secondaryColor;
  final InputType type;
  final bool require;
  final int? maxLines;

  @override
  State<StatefulWidget> createState() => _ControlledNamedTextFieldState();
}

class _ControlledNamedTextFieldState extends State<ControlledNamedTextField> {
  final _textEditingController = TextEditingController();
  bool _showPassword = false;

  void switchShow() {
    setState(() {
      _showPassword = !_showPassword;
    });
  }

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
  void didUpdateWidget(ControlledNamedTextField oldWidget) {
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
      spacing: 8,
      children: [
        Row(
          children: [
            Text(
              "${widget.title.toUpperCase()} ${widget.require ? "*" : ""}",
              style: TextStyle(
                fontSize: 12,
                fontWeight: .w700,
                color: textColor,
              ),
            ),
            if (!widget.require)
              Text(
                "(необязательно)",
                style: TextStyle(
                  fontSize: 12,
                  fontWeight: .w500,
                  color: textColor,
                ),
              ),
          ],
        ),
        Container(
          padding: .symmetric(vertical: 4, horizontal: 16),
          decoration: BoxDecoration(
            color: widget.secondaryColor
                ? Theme.of(context).colorScheme.surface
                : Colors.white,
            borderRadius: .all(.circular(16)),
            boxShadow: [AppShadows.standard()],
          ),
          child: Row(
            spacing: 8,
            children: [
              Expanded(
                child: TextField(
                  controller: _textEditingController,
                  onChanged: widget.onChange,
                  minLines: widget.maxLines ?? 1,
                  maxLines: widget.maxLines ?? 1,
                  keyboardType: widget.type == .price
                      ? .number
                      : (widget.maxLines != null && widget.maxLines! > 1
                            ? .multiline
                            : .text),
                  decoration: InputDecoration(
                    border: .none,
                    hintText: widget.hintText,
                    hintStyle: TextStyle(
                      fontSize: 14,
                      color: Color(0xFF9A9A9A),
                    ),
                  ),
                  style: TextStyle(fontSize: 14),
                  obscureText: widget.type == .password && !_showPassword,
                  inputFormatters: [
                    if (widget.type == .price) PriceInputFormatter(),
                  ],
                ),
              ),
              if (widget.type == .password)
                IconButton(
                  onPressed: switchShow,
                  icon: SvgPicture.asset(
                    _showPassword
                        ? "assets/icons/opened_eye.svg"
                        : "assets/icons/closed_eye.svg",
                  ),
                ),
            ],
          ),
        ),
      ],
    );
  }
}
