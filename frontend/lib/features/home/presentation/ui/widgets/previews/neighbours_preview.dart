import 'package:flutter/material.dart';
import 'package:frontend/features/home/presentation/ui/widgets/cards/neighbour_preview_card.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';

class NeighboursPreview extends StatelessWidget {
  const NeighboursPreview({super.key, required this.neighbours});

  final List<Profile> neighbours;

  @override
  Widget build(BuildContext context) {
    final mediaQuery = MediaQuery.of(context);

    return Column(
      crossAxisAlignment: .start,
      spacing: 15,
      children: [
        Text(
          "Соседи",
          style: TextStyle(
            color: Theme.of(context).colorScheme.onSurface,
            fontSize: 18,
            fontWeight: .w500,
          ),
        ),
        Row(
          spacing: 10,
          children: [
            ...neighbours.map(
              (p) => NeighbourPreviewCard(
                profile: p,
                width: (mediaQuery.size.width - 20 * 2 - 10 * 2) / 3,
              ),
            ),
          ],
        ),
      ],
    );
  }
}
