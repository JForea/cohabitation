import 'package:flutter/material.dart';
import 'package:frontend/features/home/presentation/ui/widgets/cards/neighbour_preview_card.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';

class NeighboursPreview extends StatelessWidget {
  const NeighboursPreview({super.key, required this.neighbours});

  final List<Profile> neighbours;

  static const double _spacing = 10;
  static const double _horizontalPadding = 20;
  static const double _desktopMaxCardWidth = 190;

  @override
  Widget build(BuildContext context) {
    final mediaQuery = MediaQuery.of(context);

    return LayoutBuilder(
      builder: (context, constraints) {
        final isConstrained =
            constraints.maxWidth < mediaQuery.size.width - 100;

        if (!isConstrained) {
          return Row(
            spacing: _spacing,
            children: [
              ...neighbours.map(
                (p) => NeighbourPreviewCard(
                  profile: p,
                  width:
                      (mediaQuery.size.width -
                          _horizontalPadding * 2 -
                          _spacing * 2) /
                      3,
                ),
              ),
            ],
          );
        }

        return Wrap(
          spacing: _spacing,
          runSpacing: _spacing,
          children: [
            ...neighbours.map(
              (p) =>
                  NeighbourPreviewCard(profile: p, width: _desktopMaxCardWidth),
            ),
          ],
        );
      },
    );
  }
}
