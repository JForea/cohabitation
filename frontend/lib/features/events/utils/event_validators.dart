class EventValidators {
  static String? validateName(String name) {
    if (name.isEmpty) {
      return "Поле не должно быть пустым";
    }
    if (name.length > 64) {
      return "До 64 символов";
    }
    return null;
  }

  static String? validateDescription(String description) {
    if (description.length > 256) {
      return "До 256 символов";
    }
    return null;
  }

  static String? validateTime(String time) {
    if (time.isNotEmpty &&
        !RegExp(r'^([01]\d|2[0-3]):([0-5]\d)$').hasMatch(time)) {
      return "Неверный формат";
    }
    return null;
  }
}
