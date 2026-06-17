import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/events/utils/event_validators.dart';
import 'package:frontend/shared/data/failures/failures.dart';
import 'package:frontend/shared/data/providers/events_provider.dart';
import 'package:frontend/features/events/presentation/ui/widgets/cards/event_card.dart';
import 'package:frontend/shared/data/providers/user_provider.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_app_floating_action_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/dialogs/error_dialog.dart';
import 'package:frontend/shared/presentation/ui/widgets/inputs/controlled_named_text_field.dart';
import 'package:frontend/shared/presentation/ui/widgets/modals/app_modal.dart';
import 'package:frontend/shared/presentation/ui/widgets/other/empty_message_widget.dart';
import 'package:frontend/shared/presentation/ui/widgets/texts/default_load_error_text.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/page_wrapper.dart';
import 'package:intl/intl.dart';

class EventsPage extends ConsumerStatefulWidget {
  const EventsPage({super.key, required this.date});

  final DateTime date;

  @override
  ConsumerState<ConsumerStatefulWidget> createState() => _EventsPageState();
}

class _EventsPageState extends ConsumerState<EventsPage> {
  late String name;
  late String time;
  late String description;

  late String nameErrorMessage;
  late String timeErrorMessage;
  late String descriptionErrorMessage;

  Future<void> _refresh(WidgetRef ref) async {
    ref.read(eventsProvider(widget.date).notifier).refresh();
  }

  void setName(String s) {
    name = s;
  }

  void setTime(String s) {
    time = s;
  }

  void setDescription(String s) {
    description = s;
  }

  TimeOfDay parseTime(String time) {
    final timeSplitted = time.split(":");
    return TimeOfDay(
      hour: int.parse(timeSplitted[0]),
      minute: int.parse(timeSplitted[1]),
    );
  }

  Future<void> create(BuildContext context) async {
    bool ok = true;
    setState(() {
      final nameError = EventValidators.validateName(name);
      if (nameError != null) {
        nameErrorMessage = nameError;
        ok = false;
      }
      final timeError = EventValidators.validateTime(time);
      if (timeError != null) {
        timeErrorMessage = timeError;
        ok = false;
      }
      final descriptionError = EventValidators.validateDescription(description);
      if (descriptionError != null) {
        descriptionErrorMessage = descriptionError;
        ok = false;
      }
    });

    if (!ok) return;

    TimeOfDay? timeOfDay;
    timeOfDay = parseTime(time);

    final createdBy = ref.read(userProvider)?.profile;

    if (createdBy == null) {
      return;
    }

    try {
      await ref
          .read(eventsProvider(widget.date).notifier)
          .create(
            name: name,
            createdBy: createdBy,
            description: description,
            time: timeOfDay,
          );

      setState(() {
        name = "";
        time = "";
        description = "";

        nameErrorMessage = "";
        timeErrorMessage = "";
        descriptionErrorMessage = "";
      });

      if (context.mounted) {
        Navigator.pop(context);
      }
    } on Failure catch (e) {
      if (context.mounted) {
        showErrorDialog(context, e.message);
      }
    }
  }

  void _onAdd(BuildContext context) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (context) => AppModal(
        children: [
          Text(
            "Новое событие",
            style: TextStyle(
              color: Theme.of(context).colorScheme.onSurface,
              fontSize: 18,
              fontWeight: .w500,
            ),
          ),
          ControlledNamedTextField(
            text: name,
            title: "Название",
            hintText: "Собрание",
            onChange: setName,
            secondaryColor: true,
            type: .text,
            require: true,
            errorMessage: nameErrorMessage,
          ),
          ControlledNamedTextField(
            text: time,
            title: "Время начала",
            hintText: "14:30",
            onChange: setTime,
            secondaryColor: true,
            type: .time,
            require: false,
            errorMessage: timeErrorMessage,
          ),
          ControlledNamedTextField(
            text: description,
            title: "Описание",
            hintText: "Обсудим планы",
            onChange: setDescription,
            secondaryColor: true,
            type: .text,
            maxLines: 3,
            require: false,
            errorMessage: descriptionErrorMessage,
          ),
          CustomTextButton(
            onPressed: () async => await create(context),
            text: "Создать",
          ),
        ],
      ),
    );
  }

  @override
  void initState() {
    name = "";
    time = "";
    description = "";

    nameErrorMessage = "";
    timeErrorMessage = "";
    descriptionErrorMessage = "";
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final eventsState = ref.watch(eventsProvider(widget.date));

    return Scaffold(
      floatingActionButton: CustomAppFloatingActionButton(
        onPressed: () => _onAdd(context),
      ),
      body: RefreshIndicator(
        onRefresh: () => _refresh(ref),
        child: PageWrapper(
          backButton: true,
          pathIfCantPop: "/",
          pageName: DateFormat("d MMMM", "ru_RU").format(widget.date),
          bottomFloatingButtonExists: true,
          children: [
            eventsState.when(
              data: (events) => events.isEmpty
                  ? Center(
                      child: EmptyMessageWidget(
                        assetPath: "assets/icons/calendar.svg",
                        message: "Пока нет запланированных событий",
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
              error: (e, _) => defaultLoadErrorText,
              loading: () => Center(child: CircularProgressIndicator()),
            ),
          ],
        ),
      ),
    );
  }
}
