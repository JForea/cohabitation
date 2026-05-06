import 'package:flutter/material.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/ui/widgets/avatars/avatar.dart';
import 'package:frontend/shared/presentation/ui/widgets/chips/custom_choice_chip.dart';
import 'package:frontend/shared/presentation/ui/widgets/wrappers/choice_wrapper.dart';

class UserChoiceWrapper extends StatelessWidget {
  const UserChoiceWrapper({
    super.key,
    required this.name,
    required this.profiles,
    required this.selected,
    required this.select,
  });

  final String name;
  final List<Profile> profiles;
  final int? selected;
  final void Function(Profile?) select;

  @override
  Widget build(BuildContext context) {
    return ChoiceWrapper(
      name: name,
      children: [
        CustomChoiceChip(
          name: "Общий",
          icon: Avatar(name: "Общий", size: 24, color: AppColors.greyBlue),
          selected: selected == null,
          checkMark: true,
          onSelect: () => select(null),
        ),
        ...profiles.map(
          (p) => CustomChoiceChip(
            name: p.name,
            icon: Avatar(name: p.name, size: 24, color: p.color),
            selected: p.id == selected,
            checkMark: true,
            onSelect: () => select(p),
          ),
        ),
      ],
    );
  }
}
