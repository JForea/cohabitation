import 'package:frontend/shared/types/role.dart';

Role getRoleFromName(String name) {
  name = name.toLowerCase();
  switch (name) {
    case "inhabitant":
      return Role.inhabitant;
    case "admin":
      return Role.admin;
    case "creator":
      return Role.creator;
    default:
      throw Error();
  }
}
