String getDateDisplayFromDateTime(DateTime date) {
  final now = DateTime.now();

  final today = DateTime(now.year, now.month, now.day);
  final target = DateTime(date.year, date.month, date.day);

  final diff = target.difference(today).inDays;

  if (diff == 0) return 'Сегодня';
  if (diff == 1) return 'Завтра';
  if (diff == 2) return 'Послезавтра';

  const months = [
    '',
    'января',
    'февраля',
    'марта',
    'апреля',
    'мая',
    'июня',
    'июля',
    'августа',
    'сентября',
    'октября',
    'ноября',
    'декабря',
  ];

  return '${target.day} ${months[target.month]}';
}
