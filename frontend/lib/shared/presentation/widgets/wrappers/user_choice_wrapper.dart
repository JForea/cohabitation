import 'package:flutter/material.dart';
import 'package:frontend/shared/domain/models/profile/profile.dart';
import 'package:frontend/app/theme/app_colors.dart';
import 'package:frontend/shared/presentation/widgets/avatars/avatar.dart';
import 'package:frontend/shared/presentation/widgets/chips/custom_choice_chip.dart';
import 'package:frontend/shared/presentation/widgets/wrappers/choice_wrapper.dart';

class UserChoiceWrapper extends StatelessWidget {
  const UserChoiceWrapper({
    super.key,
    required this.name,
    required this.profiles,
    required this.selected,
    this.autoAssign,
    required this.multipleSelect,
    required this.select,
  });

  final String name;
  final List<Profile> profiles;
  final List<int> selected;
  final bool? autoAssign;
  final bool multipleSelect;
  final void Function(Profile?, {bool autoAssign}) select;

  @override
  Widget build(BuildContext context) {
    return ChoiceWrapper(
      name: name,
      children: [
        CustomChoiceChip(
          name: "Общий",
          icon: Avatar(name: "Общий", size: 24, color: AppColors.greyBlue),
          selected: selected.isEmpty && !(autoAssign ?? false),
          checkMark: true,
          onSelect: () => select(null, autoAssign: false),
        ),
        if (autoAssign != null && !multipleSelect)
          CustomChoiceChip(
            name: "Авто",
            icon: Icon(Icons.settings, size: 24, color: AppColors.greyBlue),
            selected: autoAssign != null && autoAssign!,
            checkMark: true,
            onSelect: () => select(null, autoAssign: true),
          ),
        ...profiles.map(
          (p) => CustomChoiceChip(
            name: p.name,
            icon: Avatar(name: p.name, size: 24, color: p.color),
            selected: selected.contains(p.id) && !(autoAssign ?? false),
            checkMark: true,
            onSelect: () => select(p, autoAssign: false),
          ),
        ),
      ],
    );
  }
}
