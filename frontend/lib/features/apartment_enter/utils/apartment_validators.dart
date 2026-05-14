class ApartmentValidators {
  ApartmentValidators._internal();

  static String? validateName(String name) {
    if (name.length < 2 || name.length > 32) {
      return "От 2 до 32 символов";
    }
    return null;
  }

  static String? validateAddress(String address) {
    if (address.isNotEmpty && address.length > 64) {
      return "До 64 символов";
    }
    return null;
  }
}
