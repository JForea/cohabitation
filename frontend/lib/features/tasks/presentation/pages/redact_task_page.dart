import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/tasks/presentation/widgets/chips/task_priority_choice_chip.dart';
import 'package:frontend/shared/data/dtos/repeat_rule_dto.dart';
import 'package:frontend/core/failures/failures.dart';
import 'package:frontend/shared/domain/models/profile/profile.dart';
import 'package:frontend/shared/domain/models/task.dart';
import 'package:frontend/shared/state/providers/async_user_provider.dart';
import 'package:frontend/shared/state/providers/neighbours_provider.dart';
import 'package:frontend/shared/state/providers/tasks_provider.dart';
import 'package:frontend/shared/domain/types/room.dart';
import 'package:frontend/shared/domain/types/task_priority.dart';
import 'package:frontend/app/theme/app_styles.dart';
import 'package:frontend/shared/presentation/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/widgets/chips/custom_choice_chip.dart';
import 'package:frontend/shared/presentation/widgets/dialogs/error_dialog.dart';
import 'package:frontend/shared/presentation/widgets/inputs/controlled_named_text_field.dart';
import 'package:frontend/shared/presentation/widgets/switches/custom_switch.dart';
import 'package:frontend/shared/presentation/widgets/wrappers/choice_wrapper.dart';
import 'package:frontend/shared/presentation/widgets/wrappers/page_wrapper.dart';
import 'package:frontend/shared/presentation/widgets/wrappers/user_choice_wrapper.dart';
import 'package:frontend/core/utils/util_functions.dart';
import 'package:go_router/go_router.dart';

class RedactTaskPage extends ConsumerStatefulWidget {
  const RedactTaskPage({super.key, this.task});

  final Task? task;

  @override
  ConsumerState<ConsumerStatefulWidget> createState() => _RedactTaskPageState();
}

class _RedactTaskPageState extends ConsumerState<RedactTaskPage> {
  late String name;
  late String description;
  late List<Profile> assignedTo;
  late bool autoAssign;
  late Room room;
  late TaskPriority priority;
  RepeatRuleDto? repeatRule;
  int? dueDateOffset;
  late int points;

  late bool isLoading;

  @override
  void initState() {
    name = "";
    description = "";
    assignedTo = [];
    autoAssign = false;
    room = Room.common;
    priority = TaskPriority.medium;
    dueDateOffset = 0;
    points = 5;

    isLoading = false;

    super.initState();
  }

  void setName(String s) {
    name = s;
  }

  void setDescription(String s) {
    description = s;
  }

  void changeRoom(Room r) {
    setState(() {
      room = r;
    });
  }

  void changeAssigned(Profile? p, {bool autoAssign = false}) {
    setState(() {
      if (autoAssign) {
        this.autoAssign = true;
        assignedTo.clear();
        return;
      }

      this.autoAssign = false;

      if (p == null) {
        assignedTo.clear();
        return;
      }

      if (repeatRule != null) {
        if (assignedTo.contains(p) && assignedTo.length > 1) {
          assignedTo.remove(p);
        } else if (!assignedTo.contains(p)) {
          assignedTo.add(p);
        }
      } else {
        assignedTo = [p];
      }
    });
  }

  void switchRepeatable() {
    setState(() {
      if (repeatRule != null) {
        repeatRule = null;
      } else {
        repeatRule = RepeatRuleDto(beginDate: repeatBeginDate);
        dueDateOffset ??= 0;
      }

      autoAssign = false;
      assignedTo = [];
    });
  }

  void changePriority(TaskPriority p) {
    setState(() {
      priority = p;
    });
  }

  void changeDueDateOffset(int? offset) {
    setState(() {
      dueDateOffset = offset;

      if (repeatRule != null) {
        dueDateOffset ??= 0;
        repeatRule!.setEndOption(
          repeatRule!.endOption,
          beginDate: repeatBeginDate,
        );
      }
    });
  }

  void changePoints(int p) {
    setState(() {
      points = p;
    });
  }

  DateTime get repeatBeginDate {
    return DateTime.now().add(Duration(days: dueDateOffset ?? 0));
  }

  void changeRepeatEndOption(RepeatEndOption option) {
    setState(() {
      repeatRule?.setEndOption(option, beginDate: repeatBeginDate);
    });
  }

  void changeRepeatInterval(RepeatIntervalOption option) {
    setState(() {
      repeatRule?.setIntervalOption(option);
    });
  }

  Future<void> create(BuildContext context) async {
    final userProfile = ref.read(asyncUserProvider).value!.profile!;

    try {
      await ref
          .read(tasksProvider.notifier)
          .create(
            userProfile: userProfile,
            name: name,
            description: description,
            assignedTo: assignedTo,
            room: room,
            priority: priority,
            dueDateOffset: dueDateOffset,
            points: points,
            autoAssign: autoAssign,
            repeatRule: repeatRule,
          );

      if (context.mounted) {
        context.go("/");
      }
    } on Failure catch (e) {
      if (context.mounted) {
        showErrorDialog(context, e.message);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final profiles = [
      ref.read(asyncUserProvider).value!.profile!,
      ...ref.read(neighboursProvider).value!,
    ];

    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.surface,
      body: PageWrapper(
        backButton: true,
        pathIfCantPop: "/",
        pageName: "Новая задача",
        bottomFloatingButtonExists: false,
        children: [
          ControlledNamedTextField(
            text: name,
            title: "Название",
            hintText: "Что нужно сделать?",
            onChange: setName,
            secondaryColor: false,
            type: .text,
            require: true,
          ),
          ControlledNamedTextField(
            text: description,
            title: "Описание",
            hintText: "Дополнительные детали...",
            onChange: setDescription,
            secondaryColor: false,
            type: .text,
            require: false,
            maxLines: 5,
          ),
          ChoiceWrapper(
            name: "Комната",
            children: Room.values
                .map(
                  (r) => CustomChoiceChip(
                    name: UtilFunctions.getDisplayNameFromT(r),
                    selected: room == r,
                    checkMark: true,
                    onSelect: () => changeRoom(r),
                  ),
                )
                .toList(),
          ),
          ChoiceWrapper(
            name: "Приоритет",
            children: TaskPriority.values
                .map(
                  (p) => TaskPriorityChoiceChip(
                    priority: p,
                    selected: p == priority,
                    onSelect: () => changePriority(p),
                  ),
                )
                .toList(),
          ),
          Row(
            spacing: 12,
            children: [
              CustomSwitch(
                turnedOn: repeatRule != null,
                onSwitch: switchRepeatable,
              ),
              Text("ПОВТОРЯЕМАЯ ЗАДАЧА", style: AppStyles.surfaceTitle()),
            ],
          ),
          UserChoiceWrapper(
            name: "Назначить",
            profiles: profiles,
            selected: assignedTo.map((p) => p.id).toList(),
            multipleSelect: repeatRule != null,
            autoAssign: autoAssign,
            select: changeAssigned,
          ),
          ChoiceWrapper(
            name: "Срок",
            children: [
              ...List.generate(7, (i) {
                final date = DateTime.now().add(Duration(days: i));

                return CustomChoiceChip(
                  name: UtilFunctions.getDateDisplayFromDateTime(date),
                  selected: i == dueDateOffset,
                  checkMark: true,
                  onSelect: () => changeDueDateOffset(i),
                );
              }),
              if (repeatRule == null)
                CustomChoiceChip(
                  name: "Без срока",
                  selected: dueDateOffset == null,
                  checkMark: true,
                  onSelect: () => changeDueDateOffset(null),
                ),
            ],
          ),
          if (repeatRule != null)
            ChoiceWrapper(
              name: "Повторять",
              children: [
                CustomChoiceChip(
                  name: "Каждый день",
                  selected:
                      repeatRule!.intervalOption == RepeatIntervalOption.daily,
                  checkMark: true,
                  onSelect: () =>
                      changeRepeatInterval(RepeatIntervalOption.daily),
                ),
                CustomChoiceChip(
                  name: "Каждые 2 дня",
                  selected:
                      repeatRule!.intervalOption ==
                      RepeatIntervalOption.everyTwoDays,
                  checkMark: true,
                  onSelect: () =>
                      changeRepeatInterval(RepeatIntervalOption.everyTwoDays),
                ),
                CustomChoiceChip(
                  name: "Каждые 3 дня",
                  selected:
                      repeatRule!.intervalOption ==
                      RepeatIntervalOption.everyThreeDays,
                  checkMark: true,
                  onSelect: () =>
                      changeRepeatInterval(RepeatIntervalOption.everyThreeDays),
                ),
                CustomChoiceChip(
                  name: "Каждую неделю",
                  selected:
                      repeatRule!.intervalOption == RepeatIntervalOption.weekly,
                  checkMark: true,
                  onSelect: () =>
                      changeRepeatInterval(RepeatIntervalOption.weekly),
                ),
                CustomChoiceChip(
                  name: "Каждые 2 недели",
                  selected:
                      repeatRule!.intervalOption ==
                      RepeatIntervalOption.biweekly,
                  checkMark: true,
                  onSelect: () =>
                      changeRepeatInterval(RepeatIntervalOption.biweekly),
                ),
                CustomChoiceChip(
                  name: "Каждый месяц",
                  selected:
                      repeatRule!.intervalOption ==
                      RepeatIntervalOption.monthly,
                  checkMark: true,
                  onSelect: () =>
                      changeRepeatInterval(RepeatIntervalOption.monthly),
                ),
              ],
            ),
          if (repeatRule != null)
            ChoiceWrapper(
              name: "Закончить повторение",
              children: [
                CustomChoiceChip(
                  name: "Через неделю",
                  selected: repeatRule!.endOption == RepeatEndOption.week,
                  checkMark: true,
                  onSelect: () => changeRepeatEndOption(RepeatEndOption.week),
                ),
                CustomChoiceChip(
                  name: "Через месяц",
                  selected: repeatRule!.endOption == RepeatEndOption.month,
                  checkMark: true,
                  onSelect: () => changeRepeatEndOption(RepeatEndOption.month),
                ),
                CustomChoiceChip(
                  name: "Через 3 месяца",
                  selected:
                      repeatRule!.endOption == RepeatEndOption.threeMonths,
                  checkMark: true,
                  onSelect: () =>
                      changeRepeatEndOption(RepeatEndOption.threeMonths),
                ),
                CustomChoiceChip(
                  name: "Через полгода",
                  selected: repeatRule!.endOption == RepeatEndOption.sixMonths,
                  checkMark: true,
                  onSelect: () =>
                      changeRepeatEndOption(RepeatEndOption.sixMonths),
                ),
                CustomChoiceChip(
                  name: "Без срока",
                  selected: repeatRule!.endOption == RepeatEndOption.never,
                  checkMark: true,
                  onSelect: () => changeRepeatEndOption(RepeatEndOption.never),
                ),
              ],
            ),
          ChoiceWrapper(
            name: "Очки за выполнение",
            children: List.generate(6, (i) {
              final p = (i + 1) * 5;

              return CustomChoiceChip(
                name: "$p",
                selected: points == p,
                checkMark: false,
                onSelect: () => changePoints(p),
                wPadding: 12,
              );
            }),
          ),
          CustomTextButton(
            onPressed: () async => await create(context),
            text: "Создать",
          ),
        ],
      ),
    );
  }
}
