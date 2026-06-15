class RepeatRuleDto {
  late int _intervalDays;
  late DateTime _endDate;

  RepeatRuleDto({DateTime? beginDate}) {
    _intervalDays = 1;
    _endDate = beginDate ?? DateTime.now().add(const Duration(days: 7));
  }

  int get intervalDays => _intervalDays;
  DateTime get endDate => _endDate;

  set intervalDays(int value) {
    _intervalDays = value;
  }

  set endDate(DateTime value) {
    _endDate = value;
  }
}
