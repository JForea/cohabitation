import 'package:flutter/services.dart';
import 'package:intl/intl.dart';

class PriceInputFormatter extends TextInputFormatter {
  final _formatter = NumberFormat('#,###', 'ru_RU');

  @override
  TextEditingValue formatEditUpdate(
    TextEditingValue oldValue,
    TextEditingValue newValue,
  ) {
    final digits = newValue.text.replaceAll(RegExp(r'\D'), '');

    if (digits.isEmpty) {
      return const TextEditingValue(text: '');
    }

    final number = int.parse(digits);
    String formatted = _formatter.format(number);

    formatted = formatted.replaceAll('\u00A0', ' ');

    formatted = '$formatted ₽';

    return TextEditingValue(
      text: formatted,
      selection: TextSelection.collapsed(
        offset: formatted.length - 2, // курсор перед ₽
      ),
    );
  }
}
