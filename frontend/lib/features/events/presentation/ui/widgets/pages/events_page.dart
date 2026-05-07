import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:frontend/features/events/data/providers/events_provider.dart';
import 'package:frontend/features/events/presentation/ui/widgets/cards/event_card.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_app_floating_action_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/page_wrapper.dart';
import 'package:intl/intl.dart';

class EventsPage extends ConsumerWidget {
  const EventsPage({super.key, required this.date});

  final DateTime date;

  Future<void> _refresh(WidgetRef ref) async {
    ref.read(eventsProvider(date).notifier).refresh();
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final eventsState = ref.watch(eventsProvider(date));

    return Scaffold(
      floatingActionButton: CustomAppFloatingActionButton(onPressed: () {}),
      body: RefreshIndicator(
        onRefresh: () => _refresh(ref),
        child: PageWrapper(
          backButton: true,
          pageName: DateFormat("d MMMM", "ru_RU").format(date),
          children: [
            eventsState.when(
              data: (events) => events.isEmpty
                  ? Center(
                      child: Column(
                        children: [
                          SvgPicture.asset(
                            "assets/icons/calendar.svg",
                            colorFilter: ColorFilter.mode(
                              AppColors.greyBlue,
                              .srcIn,
                            ),
                            width: 90,
                            height: 90,
                          ),
                          SizedBox(height: 20),
                          Text(
                            "Пока нет",
                            style: TextStyle(
                              color: AppColors.greyBlue,
                              fontSize: 18,
                              fontWeight: .w700,
                            ),
                          ),
                          Text(
                            "запланированных событий",
                            style: TextStyle(
                              color: AppColors.greyBlue,
                              fontSize: 18,
                              fontWeight: .w700,
                            ),
                          ),
                        ],
                      ),
                    )
                  : Column(
                      crossAxisAlignment: .start,
                      spacing: 12,
                      children: [
                        Text(
                          "ЗАПЛАНИРОВАННЫЕ СОБЫТИЯ",
                          style: TextStyle(
                            color: AppColors.greyBlue,
                            fontSize: 14,
                            fontWeight: .w700,
                          ),
                        ),
                        ...events.map((e) => EventCard(event: e)),
                      ],
                    ),
              error: (e, _) => Text("Произошла ошибка при загрузке."),
              loading: () => Center(child: CircularProgressIndicator()),
            ),
          ],
        ),
      ),
    );
  }
}
