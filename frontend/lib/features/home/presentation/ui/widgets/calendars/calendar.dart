import 'package:flutter/material.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/theme/app_shadows.dart';
import 'package:intl/intl.dart';
import 'package:table_calendar/table_calendar.dart';

class Calendar extends StatelessWidget {
  const Calendar({
    super.key,
    required this.dates,
    required this.focusedDay,
    required this.firstDay,
    required this.lastDay,
    required this.onDaySelected,
    required this.onPageChanged,
  });

  final Set<DateTime>? dates;
  final DateTime focusedDay;
  final DateTime firstDay;
  final DateTime lastDay;
  final VoidCallback onDaySelected;
  final void Function(DateTime) onPageChanged;

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: .all(.circular(20)),
        boxShadow: [AppShadows.standard()],
      ),
      child: TableCalendar(
        locale: "ru_RU",
        startingDayOfWeek: .monday,
        availableCalendarFormats: {CalendarFormat.month: "Месяц"},
        rowHeight: 36,
        daysOfWeekHeight: 36,
        focusedDay: focusedDay,
        firstDay: firstDay,
        lastDay: lastDay,
        headerStyle: HeaderStyle(
          decoration: BoxDecoration(
            color: AppColors.blue,
            borderRadius: .vertical(top: .circular(20)),
          ),
          titleTextStyle: TextStyle(
            color: Colors.white,
            fontSize: 16,
            fontWeight: .w700,
          ),
          titleCentered: true,
          leftChevronIcon: Icon(
            Icons.chevron_left,
            size: 24,
            color: Colors.white,
          ),
          rightChevronIcon: Icon(
            Icons.chevron_right,
            size: 24,
            color: Colors.white,
          ),
          titleTextFormatter: (date, locale) {
            String dateFormatted = DateFormat(
              'LLLL, yyyy',
              locale,
            ).format(date);

            return dateFormatted[0].toUpperCase() + dateFormatted.substring(1);
          },
        ),
        daysOfWeekStyle: DaysOfWeekStyle(
          decoration: BoxDecoration(color: AppColors.blue),
          weekdayStyle: TextStyle(
            color: Colors.white,
            fontSize: 14,
            fontWeight: .w700,
          ),
          weekendStyle: TextStyle(
            color: Colors.white,
            fontSize: 14,
            fontWeight: .w700,
          ),
          dowTextFormatter: (date, locale) {
            String dateFormatted = DateFormat("E", locale).format(date);

            return dateFormatted[0].toUpperCase() + dateFormatted[1];
          },
        ),
        calendarStyle: CalendarStyle(
          defaultTextStyle: TextStyle(fontSize: 14, fontWeight: .w500),
          weekendTextStyle: TextStyle(fontSize: 14, fontWeight: .w500),
          todayTextStyle: TextStyle(
            color: Colors.white,
            fontSize: 14,
            fontWeight: .w500,
          ),
          todayDecoration: BoxDecoration(color: AppColors.blue, shape: .circle),
          outsideTextStyle: TextStyle(
            color: AppColors.greyBlue,
            fontSize: 14,
            fontWeight: .w500,
          ),
        ),
        onDaySelected: (selectedDay, focusedDay) => onDaySelected(),
        onPageChanged: (focusedDay) => onPageChanged(focusedDay),
        calendarBuilders: CalendarBuilders(
          defaultBuilder: (context, day, focusedDay) {
            if (dates == null) {
              return null;
            }

            for (final date in dates!) {
              if (date.year == day.year &&
                  date.month == day.month &&
                  date.day == day.day) {
                return Container(
                  margin: const EdgeInsets.all(4),
                  alignment: Alignment.center,
                  decoration: BoxDecoration(
                    color: AppColors.blue.withAlpha(37),
                    shape: .circle,
                  ),
                  child: Text(
                    '${date.day}',
                    style: TextStyle(
                      color: AppColors.blue,
                      fontSize: 14,
                      fontWeight: .w600,
                    ),
                  ),
                );
              }
            }

            return null;
          },
        ),
      ),
    );
  }
}
