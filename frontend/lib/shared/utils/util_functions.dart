import 'package:flutter/material.dart';
import 'package:frontend/shared/data/types/buying_category.dart';
import 'package:frontend/shared/data/types/role.dart';
import 'package:frontend/shared/data/types/room.dart';
import 'package:frontend/shared/data/types/task_priority.dart';
import 'package:frontend/shared/presentation/theme/app_colors.dart';

class UtilFunctions {
  static String getDateDisplayFromDateTime(DateTime date) {
    final now = DateTime.now();

    final today = DateTime(now.year, now.month, now.day);
    final target = DateTime(date.year, date.month, date.day);

    final diff = target.difference(today).inDays;

    if (diff == 0) return 'Сегодня';
    if (diff == 1) return 'Завтра';
    if (diff == 2) return 'Послезавтра';

    const months = [
      '',
      'января',
      'февраля',
      'марта',
      'апреля',
      'мая',
      'июня',
      'июля',
      'августа',
      'сентября',
      'октября',
      'ноября',
      'декабря',
    ];

    return '${target.day} ${months[target.month]}';
  }

  static String getDisplayNameFromT<T>(T value) {
    String result;
    switch (T) {
      case const (Role):
        result = switch (value as Role) {
          Role.inhabitant => "Пользователь",
          Role.admin => "Администратор",
          Role.creator => "Владелец",
        };
        break;

      case const (Room):
        result = switch (value as Room) {
          Room.bathroom => "Ванная",
          Room.bedroom => "Спальня",
          Room.common => "Общее",
          Room.corridor => "Коридор",
          Room.livingRoom => "Гостиная",
          Room.kitchen => "Кухня",
        };
        break;

      case const (TaskPriority):
        result = switch (value as TaskPriority) {
          TaskPriority.low => "Низкий",
          TaskPriority.medium => "Средний",
          TaskPriority.high => "Высокий",
        };
        break;

      case const (BuyingCategory):
        result = switch (value as BuyingCategory) {
          BuyingCategory.bakery => "Выпечка",
          BuyingCategory.cereals => "Крупы",
          BuyingCategory.dairy => "Молочное",
          BuyingCategory.drinks => "Напитки",
          BuyingCategory.fruits => "Фрукты",
          BuyingCategory.household => "Бытовое",
          BuyingCategory.meat => "Мясо",
          BuyingCategory.other => "Другое",
          BuyingCategory.vegetables => "Овощи",
        };
        break;

      default:
        throw UnsupportedError("Provided type $T is not supported.");
    }

    return result;
  }

  static T getTValueFromName<T>(String name) {
    name = name.toLowerCase();
    dynamic res;
    switch (T) {
      case const (Color):
        res = switch (name) {
          "blue" => AppColors.blue,
          "green" => AppColors.green,
          "orange" => AppColors.orange,
          "yellow" => AppColors.yellow,
          "purple" => AppColors.purple,
          "brown" => AppColors.brown,
          _ => Colors.black,
        };
        break;

      case const (Role):
        res = switch (name) {
          "inhabitant" => Role.inhabitant,
          "admin" => Role.admin,
          "creator" => Role.creator,
          _ => null,
        };
        break;

      case const (Room):
        res = switch (name) {
          "common" => Room.common,
          "kitchen" => Room.kitchen,
          "living_room" => Room.livingRoom,
          "bathroom" => Room.bathroom,
          "bedroom" => Room.bedroom,
          "corridor" => Room.corridor,
          _ => null,
        };
        break;

      case const (TaskPriority):
        res = switch (name) {
          "low" => TaskPriority.low,
          "medium" => TaskPriority.medium,
          "high" => TaskPriority.high,
          _ => null,
        };
        break;

      case const (BuyingCategory):
        res = switch (name) {
          "bakery" => BuyingCategory.bakery,
          "dairy" => BuyingCategory.dairy,
          "cereals" => BuyingCategory.cereals,
          "vegetables" => BuyingCategory.vegetables,
          "fruits" => BuyingCategory.fruits,
          "meat" => BuyingCategory.meat,
          "drinks" => BuyingCategory.drinks,
          "household" => BuyingCategory.household,
          "other" => BuyingCategory.other,
          _ => null,
        };
        break;

      default:
        throw UnsupportedError("Provided type $T is not supported.");
    }

    if (res == null) {
      throw UnsupportedError("Unknown name value '$name' for type $T.");
    }
    return res as T;
  }

  static String dateToStringRequest(DateTime date) {
    return "${date.year.toString().padLeft(4, '0')}-${date.month.toString().padLeft(2, '0')}-${date.day.toString().padLeft(2, '0')}";
  }

  static String tValueToStringRequest<T>(T value) {
    String result;
    switch (T) {
      case const (Room):
        result = switch (value as Room) {
          Room.bathroom => "BATHROOM",
          Room.bedroom => "BEDROOM",
          Room.common => "COMMON",
          Room.corridor => "CORRIDOR",
          Room.kitchen => "KITCHEN",
          Room.livingRoom => "LIVING_ROOM",
        };
        break;

      case const (TaskPriority):
        result = switch (value as TaskPriority) {
          TaskPriority.low => "LOW",
          TaskPriority.medium => "MEDIUM",
          TaskPriority.high => "HIGH",
        };
        break;

      case const (BuyingCategory):
        result = switch (value as BuyingCategory) {
          BuyingCategory.bakery => "BAKERY",
          BuyingCategory.cereals => "CEREALS",
          BuyingCategory.dairy => "DAIRY",
          BuyingCategory.drinks => "DRINKS",
          BuyingCategory.fruits => "FRUITS",
          BuyingCategory.household => "HOUSEHOLD",
          BuyingCategory.meat => "MEAT",
          BuyingCategory.other => "OTHER",
          BuyingCategory.vegetables => "VEGETABLES",
        };
        break;

      default:
        throw UnsupportedError("Provided type $T is not supported.");
    }

    return result;
  }
}
