import 'package:flutter/material.dart';

enum RepeatEndOption { week, month, threeMonths, sixMonths, never }

enum RepeatIntervalOption {
  daily,
  everyTwoDays,
  everyThreeDays,
  weekly,
  biweekly,
  monthly,
}

class RepeatRuleDto {
  late int _intervalDays;
  DateTime? _endDate;
  late RepeatEndOption _endOption;
  late RepeatIntervalOption _intervalOption;

  RepeatRuleDto({DateTime? beginDate}) {
    _intervalDays = 1;
    _endOption = RepeatEndOption.week;

    final start = DateUtils.dateOnly(beginDate ?? DateTime.now());
    _endDate = start.add(Duration(days: 7));

    _intervalOption = RepeatIntervalOption.daily;
    _intervalDays = 1;
  }

  int get intervalDays => _intervalDays;
  DateTime? get endDate => _endDate;
  RepeatEndOption get endOption => _endOption;
  RepeatIntervalOption get intervalOption => _intervalOption;

  set intervalDays(int value) {
    _intervalDays = value;
  }

  void setEndOption(RepeatEndOption option, {DateTime? beginDate}) {
    _endOption = option;

    final start = DateUtils.dateOnly(beginDate ?? DateTime.now());

    switch (option) {
      case RepeatEndOption.week:
        _endDate = start.add(Duration(days: 7));
        break;
      case RepeatEndOption.month:
        _endDate = DateTime(start.year, start.month + 1, start.day);
        break;
      case RepeatEndOption.threeMonths:
        _endDate = DateTime(start.year, start.month + 3, start.day);
        break;
      case RepeatEndOption.sixMonths:
        _endDate = DateTime(start.year, start.month + 6, start.day);
        break;
      case RepeatEndOption.never:
        _endDate = null;
        break;
    }
  }

  void setIntervalOption(RepeatIntervalOption option) {
    _intervalOption = option;

    switch (option) {
      case RepeatIntervalOption.daily:
        _intervalDays = 1;
        break;
      case RepeatIntervalOption.everyTwoDays:
        _intervalDays = 2;
        break;
      case RepeatIntervalOption.everyThreeDays:
        _intervalDays = 3;
        break;
      case RepeatIntervalOption.weekly:
        _intervalDays = 7;
        break;
      case RepeatIntervalOption.biweekly:
        _intervalDays = 14;
        break;
      case RepeatIntervalOption.monthly:
        _intervalDays = 30;
        break;
    }
  }
}
