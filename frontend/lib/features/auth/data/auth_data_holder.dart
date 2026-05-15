import 'package:flutter_riverpod/flutter_riverpod.dart';

final authDataHolderProvider =
    NotifierProvider<AuthDataHolderNotifier, AuthDataHolder>(
      AuthDataHolderNotifier.new,
    );

class AuthDataHolderNotifier extends Notifier<AuthDataHolder> {
  @override
  AuthDataHolder build() {
    return AuthDataHolder();
  }

  void setEmail(String email) =>
      state = state.copyWith(email: email, emailError: "");

  void setName(String name) =>
      state = state.copyWith(name: name, nameError: "");

  void setPassword(String password) =>
      state = state.copyWith(password: password, passwordError: "");

  void switchGender() => state = state.copyWith(male: !state.male);

  void setEmailError(String emailError) =>
      state = state.copyWith(emailError: emailError);

  void setPasswordError(String passwordError) =>
      state = state.copyWith(passwordError: passwordError);

  void setNameError(String nameError) =>
      state = state.copyWith(nameError: nameError);

  void clear() => state = AuthDataHolder();
}

class AuthDataHolder {
  AuthDataHolder({
    this.email = "",
    this.password = "",
    this.male = true,
    this.name = "",
    this.emailError = "",
    this.passwordError = "",
    this.nameError = "",
  });

  String email;
  String password;
  String name;
  bool male;

  String emailError;
  String passwordError;
  String nameError;

  void clear() {
    email = "";
    password = "";
    name = "";
    male = true;

    emailError = "";
    passwordError = "";
    nameError = "";
  }

  AuthDataHolder copyWith({
    String? email,
    String? password,
    String? name,
    bool? male,
    String? emailError,
    String? passwordError,
    String? nameError,
  }) {
    return AuthDataHolder(
      email: email ?? this.email,
      password: password ?? this.password,
      name: name ?? this.name,
      male: male ?? this.male,
      emailError: emailError ?? this.emailError,
      passwordError: passwordError ?? this.passwordError,
      nameError: nameError ?? this.nameError,
    );
  }
}
