import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/tasks/presentation/ui/widgets/chips/task_priority_choice_chip.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/data/types/room.dart';
import 'package:frontend/shared/data/types/task_priority.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/ui/widgets/avatars/avatar.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_back_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/chips/custom_choice_chip.dart';
import 'package:frontend/shared/presentation/ui/widgets/inputs/controlled_named_text_field.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/choice_wrapper.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/tab_wrapper.dart';
import 'package:frontend/shared/utils/get_date_display_from_date_time.dart';
import 'package:frontend/shared/utils/get_display_name_from_t.dart';

class CreateTaskPage extends ConsumerStatefulWidget {
  const CreateTaskPage({super.key});

  @override
  ConsumerState<ConsumerStatefulWidget> createState() => _CreateTaskPageState();
}

class _CreateTaskPageState extends ConsumerState<CreateTaskPage> {
  late String name;
  late String description;
  int? assignedTo;
  late Room room;
  late TaskPriority priority;
  int? dueTimeOffset;
  late int points;

  @override
  void initState() {
    name = "";
    description = "";
    room = Room.common;
    priority = TaskPriority.medium;
    dueTimeOffset = 0;
    points = 5;
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

  void changeAssigned(int? id) {
    setState(() {
      assignedTo = id;
    });
  }

  void changePriority(TaskPriority p) {
    setState(() {
      priority = p;
    });
  }

  void changeDueTimeOffset(int? offset) {
    setState(() {
      dueTimeOffset = offset;
    });
  }

  void changePoints(int p) {
    setState(() {
      points = p;
    });
  }

  @override
  Widget build(BuildContext context) {
    final profiles = [ref.read(authProvider).user!.profile!];

    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.surface,
      body: PageWrapper(
        floatingButtonExists: false,
        children: [
          Row(
            spacing: 15,
            children: [
              CustomBackButton(mainColor: false),
              Text(
                "Новая задача",
                style: TextStyle(
                  fontSize: 20,
                  fontWeight: .w500,
                  color: Theme.of(context).colorScheme.onSurface,
                ),
              ),
            ],
          ),
          ControlledNamedTextField(
            text: name,
            title: "Название",
            hintText: "Что нужно сделать?",
            onChange: setName,
            secondaryColor: false,
            password: false,
            require: true,
          ),
          ControlledNamedTextField(
            text: description,
            title: "Описание",
            hintText: "Дополнительные детали...",
            onChange: setDescription,
            secondaryColor: false,
            password: false,
            require: false,
            maxLines: 5,
          ),
          ChoiceWrapper(
            name: "Назначить",
            children: [
              CustomChoiceChip(
                name: "Общий",
                icon: Avatar(
                  name: "Общий",
                  size: 24,
                  color: AppColors.greyBlue,
                ),
                selected: assignedTo == null,
                checkMark: true,
                onSelect: () => changeAssigned(null),
              ),
              ...profiles.map(
                (p) => CustomChoiceChip(
                  name: p.name,
                  icon: Avatar(name: p.name, size: 24, color: p.color),
                  selected: p.id == assignedTo,
                  checkMark: true,
                  onSelect: () => changeAssigned(p.id),
                ),
              ),
            ],
          ),
          ChoiceWrapper(
            name: "Комната",
            children: Room.values
                .map(
                  (r) => CustomChoiceChip(
                    name: getDisplayNameFromT(r),
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
          ChoiceWrapper(
            name: "Срок",
            children: [
              ...List.generate(7, (i) {
                final date = DateTime.now().add(Duration(days: i));

                return CustomChoiceChip(
                  name: getDateDisplayFromDateTime(date),
                  selected: i == dueTimeOffset,
                  checkMark: true,
                  onSelect: () => changeDueTimeOffset(i),
                );
              }),
              CustomChoiceChip(
                name: "Без срока",
                selected: dueTimeOffset == null,
                checkMark: true,
                onSelect: () => changeDueTimeOffset(null),
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
          CustomTextButton(onPressed: () {}, text: "Создать"),
        ],
      ),
    );
  }
}
