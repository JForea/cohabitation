import 'package:frontend/shared/data/types/role.dart';
import 'package:frontend/shared/data/types/room.dart';

String getDisplayNameFromT<T>(T value) {
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
        Room.corridor => "Корридор",
        Room.livingRoom => "Гостиная",
        Room.kitchen => "Кухня",
      };
      break;

    default:
      throw UnsupportedError("Provided type $T is not supported.");
  }

  return result;
}
