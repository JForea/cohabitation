import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/tasks/presentation/ui/widgets/chips/task_priority_choice_chip.dart';
import 'package:frontend/shared/data/models/profile.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/data/providers/neighbours_provider.dart';
import 'package:frontend/shared/data/providers/tasks_provider.dart';
import 'package:frontend/shared/data/types/room.dart';
import 'package:frontend/shared/data/types/task_priority.dart';
import 'package:frontend/shared/presentation/ui/widgets/buttons/custom_text_button.dart';
import 'package:frontend/shared/presentation/ui/widgets/chips/custom_choice_chip.dart';
import 'package:frontend/shared/presentation/ui/widgets/inputs/controlled_named_text_field.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/choice_wrapper.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/page_wrapper.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/user_choice_wrapper.dart';
import 'package:frontend/shared/utils/util_functions.dart';
import 'package:go_router/go_router.dart';

class CreateTaskPage extends ConsumerStatefulWidget {
  const CreateTaskPage({super.key});

  @override
  ConsumerState<ConsumerStatefulWidget> createState() => _CreateTaskPageState();
}

class _CreateTaskPageState extends ConsumerState<CreateTaskPage> {
  late String name;
  late String description;
  Profile? assignedTo;
  late Room room;
  late TaskPriority priority;
  int? dueDateOffset;
  late int points;

  late bool isLoading;

  @override
  void initState() {
    name = "";
    description = "";
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

  void changeAssigned(Profile? p) {
    setState(() {
      assignedTo = p;
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
    });
  }

  void changePoints(int p) {
    setState(() {
      points = p;
    });
  }

  Future<bool> create() async {
    final userProfile = ref.read(authProvider).value!.user!.profile!;

    final created = ref
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
        );

    return created;
  }

  @override
  Widget build(BuildContext context) {
    final profiles = [
      ref.read(authProvider).value!.user!.profile!,
      ...ref.read(neighboursProvider).value!,
    ];

    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.surface,
      body: PageWrapper(
        backButton: true,
        pageName: "Новая задача",
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
          UserChoiceWrapper(
            name: "Назначить",
            profiles: profiles,
            selected: assignedTo?.id,
            select: changeAssigned,
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
              CustomChoiceChip(
                name: "Без срока",
                selected: dueDateOffset == null,
                checkMark: true,
                onSelect: () => changeDueDateOffset(null),
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
          Spacer(),
          CustomTextButton(
            onPressed: () async {
              final success = await create();

              if (success && context.mounted) {
                context.go("/");
              } else {
                print("Couldn't create task.");
              }
            },
            text: "Создать",
          ),
        ],
      ),
    );
  }
}
