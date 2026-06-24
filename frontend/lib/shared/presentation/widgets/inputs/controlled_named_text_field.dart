import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:frontend/core/formatters/price_input_formatter.dart';
import 'package:frontend/core/formatters/time_input_formatter.dart';
import 'package:frontend/app/theme/app_colors.dart';
import 'package:frontend/app/theme/app_shadows.dart';
import 'package:frontend/shared/presentation/widgets/texts/field_name.dart';

enum InputType { text, password, price, time }

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
    this.highlightError,
    this.errorMessage,
  });

  final String text;
  final String title;
  final String hintText;
  final void Function(String) onChange;
  final bool secondaryColor;
  final InputType type;
  final bool require;
  final int? maxLines;
  final bool? highlightError;
  final String? errorMessage;

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
    return Column(
      crossAxisAlignment: .start,
      spacing: 8,
      children: [
        FieldName(title: widget.title, require: widget.require),
        Container(
          padding: .symmetric(vertical: 4, horizontal: 16),
          decoration: BoxDecoration(
            color: widget.secondaryColor
                ? Theme.of(context).colorScheme.surface
                : Colors.white,
            borderRadius: .all(.circular(16)),
            border: .all(
              color: widget.highlightError != null && widget.highlightError!
                  ? AppColors.red
                  : Colors.transparent,
            ),
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
                    if (widget.type == .time) TimeInputFormatter(),
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
        if (widget.errorMessage != null)
          SizedBox(
            height: 14,
            child: Text(
              widget.errorMessage!,
              maxLines: 1,
              overflow: .ellipsis,
              style: TextStyle(
                color: AppColors.red,
                fontSize: 11,
                fontWeight: .w500,
              ),
            ),
          ),
      ],
    );
  }
}
