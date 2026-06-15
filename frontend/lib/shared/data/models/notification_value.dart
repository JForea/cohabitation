import 'package:frontend/shared/data/types/notification_type.dart';
import 'package:frontend/shared/utils/util_functions.dart';

class NotificationValue {
  const NotificationValue({
    required this.id,
    required this.type,
    required this.text,
    required this.createdAt,
    required this.isRead,
  });

  factory NotificationValue.fromJson(Map<String, dynamic> json) {
    return NotificationValue(
      id: json["id"],
      type: UtilFunctions.getTValueFromName<NotificationType>(json["type"]),
      text: json["text"],
      createdAt: DateTime.parse(json["createdAt"]),
      isRead: json["isRead"],
    );
  }

  final int id;
  final NotificationType type;
  final String text;
  final DateTime createdAt;
  final bool isRead;

  NotificationValue copyWith({
    int? id,
    NotificationType? type,
    String? text,
    DateTime? createdAt,
    bool? isRead,
  }) {
    return NotificationValue(
      id: id ?? this.id,
      type: type ?? this.type,
      text: text ?? this.text,
      createdAt: createdAt ?? this.createdAt,
      isRead: isRead ?? this.isRead,
    );
  }
}
