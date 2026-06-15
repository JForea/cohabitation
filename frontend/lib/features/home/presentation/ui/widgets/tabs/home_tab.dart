import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/home/data/providers/unread_notifications_count_provider.dart';
import 'package:frontend/features/home/presentation/ui/widgets/app_bars/home_app_bar.dart';
import 'package:frontend/features/home/presentation/ui/widgets/calendars/calendar.dart';
import 'package:frontend/features/home/presentation/ui/widgets/previews/neighbours_preview.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/providers/calendar_provider.dart';
import 'package:frontend/shared/data/providers/neighbours_provider.dart';
import 'package:frontend/shared/data/providers/user_provider.dart';
import 'package:frontend/shared/presentation/ui/widgets/other/empty_message_widget.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/tab_wrapper.dart';
import 'package:go_router/go_router.dart';

class HomeTab extends ConsumerStatefulWidget {
  const HomeTab({super.key});

  @override
  ConsumerState<HomeTab> createState() => _HomeTabState();
}

class _HomeTabState extends ConsumerState<HomeTab> {
  late DateTime focusedDay;
  late DateTime firstDay;
  late DateTime lastDay;

  Future<void> refresh(WidgetRef ref) async {
    ref.read(neighboursProvider.notifier).refresh();
    ref.read(calendarProvider.notifier).refresh();
    ref.read(unreadNotificationsCountProvider.notifier).refresh();
  }

  Future<void> updateMonth(WidgetRef ref, DateTime month) async {
    ref.read(calendarProvider.notifier).changeMonth(month);

    setState(() {
      focusedDay = month;
    });
  }

  @override
  void initState() {
    focusedDay = DateTime.now();
    firstDay = focusedDay.subtract(Duration(days: 90));
    lastDay = focusedDay.add(Duration(days: 365));
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final neighboursState = ref.watch(neighboursProvider);
    final name = ref.watch(userProvider.select((u) => u?.profile?.name));
    final address = ref.watch(apartmentProvider.select((a) => a?.address));
    final calendarDates = ref.watch(calendarProvider.select((s) => s.value));
    final unreadNotificationsCount = ref.watch(
      unreadNotificationsCountProvider.select((s) => s.value),
    );

    return RefreshIndicator(
      onRefresh: () async => refresh(ref),
      child: Column(
        children: [
          HomeAppBar(
            userName: name ?? "",
            address: address,
            unreadNotificationsCount: unreadNotificationsCount ?? 0,
          ),
          Expanded(
            child: LayoutBuilder(
              builder: (context, constraints) {
                final isWide = constraints.maxWidth >= 900;

                return TabWrapper(
                  floatingButtonExists: false,
                  appBarExists: true,
                  children: [
                    if (isWide)
                      Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          SizedBox(
                            width: 520,
                            child: Calendar(
                              dates: calendarDates,
                              focusedDay: focusedDay,
                              firstDay: firstDay,
                              lastDay: lastDay,
                              onDaySelected: (day) => context.push(
                                "/events/day/${day.toIso8601String()}",
                              ),
                              onPageChanged: (month) => updateMonth(ref, month),
                            ),
                          ),
                          SizedBox(width: 32),
                          Expanded(
                            child: Align(
                              alignment: Alignment.topLeft,
                              child: ConstrainedBox(
                                constraints: BoxConstraints(maxWidth: 620),
                                child: _NeighboursBlock(
                                  neighboursState: neighboursState,
                                ),
                              ),
                            ),
                          ),
                        ],
                      )
                    else ...[
                      Calendar(
                        dates: calendarDates,
                        focusedDay: focusedDay,
                        firstDay: firstDay,
                        lastDay: lastDay,
                        onDaySelected: (day) => context.push(
                          "/events/day/${day.toIso8601String()}",
                        ),
                        onPageChanged: (month) => updateMonth(ref, month),
                      ),
                      _NeighboursBlock(neighboursState: neighboursState),
                    ],
                  ],
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

class _NeighboursBlock extends StatelessWidget {
  const _NeighboursBlock({required this.neighboursState});

  final AsyncValue<List<Profile>> neighboursState;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          "Соседи",
          style: TextStyle(
            color: Theme.of(context).colorScheme.onSurface,
            fontSize: 18,
            fontWeight: FontWeight.w500,
          ),
        ),
        SizedBox(height: 12),
        neighboursState.when(
          data: (neighbours) => neighbours.isEmpty
              ? Center(
                  child: EmptyMessageWidget(
                    iconSize: 70,
                    fontSize: 16,
                    assetPath: "assets/icons/user.svg",
                    message: "У вас пока нет соседей",
                  ),
                )
              : NeighboursPreview(neighbours: neighbours.take(3).toList()),
          error: (e, _) => Text("Произошла ошибка при загрузке."),
          loading: () => Center(child: CircularProgressIndicator()),
        ),
      ],
    );
  }
}
