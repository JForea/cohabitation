import 'package:flutter/material.dart';
import 'package:frontend/features/events/data/models/event.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';
import 'package:frontend/shared/presentation/theme/app_shadows.dart';
import 'package:frontend/shared/presentation/ui/widgets/avatars/avatar.dart';

class EventCard extends StatelessWidget {
  const EventCard({super.key, required this.event});

  final Event event;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: .all(15),
      width: .infinity,
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surfaceContainer,
        borderRadius: .all(.circular(20)),
        boxShadow: [AppShadows.standard()],
      ),
      child: Column(
        crossAxisAlignment: .start,
        children: [
          Row(
            crossAxisAlignment: .start,
            spacing: 8,
            children: [
              Expanded(
                child: Text(
                  event.name,
                  maxLines: 1,
                  overflow: .ellipsis,
                  style: TextStyle(fontSize: 14, fontWeight: .w500),
                ),
              ),
              Avatar(
                name: event.createdBy.name,
                size: 18,
                color: event.createdBy.color,
              ),
              Text(
                event.createdBy.name,
                style: TextStyle(
                  color: AppColors.greyBlue,
                  fontSize: 12,
                  fontWeight: .w600,
                ),
              ),
            ],
          ),
          SizedBox(height: 4),
          Text(
            event.time != null
                ? MaterialLocalizations.of(
                    context,
                  ).formatTimeOfDay(event.time!, alwaysUse24HourFormat: true)
                : "",
            style: TextStyle(
              color: AppColors.greyBlue,
              fontSize: 12,
              fontWeight: .w500,
            ),
          ),
          SizedBox(height: 16),
          Text(
            event.description ?? "",
            style: TextStyle(fontSize: 12, fontWeight: .w500),
          ),
        ],
      ),
    );
  }
}
