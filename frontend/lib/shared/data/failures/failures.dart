sealed class Failure implements Exception {
  const Failure(this.message);

  final String message;
}

class NetworkFailure extends Failure {
  const NetworkFailure() : super('Нет подключения к серверу');
}

// STATUS CODE ERRORS

class InvalidDataFailure extends Failure {
  const InvalidDataFailure() : super('Введены неверные данные');
}

class UnauthorizedFailure extends Failure {
  const UnauthorizedFailure() : super('Необходима авторизация');
}

class ForbiddenFailure extends Failure {
  const ForbiddenFailure()
    : super('У вас отсутствуют права для совершения этого действия');
}

class NotFoundFailure extends Failure {
  const NotFoundFailure() : super('Ресурс не найден');
}

class ConflictFailure extends Failure {
  const ConflictFailure() : super('Произошёл конфликт с текущими данными');
}

class ServerFailure extends Failure {
  const ServerFailure() : super('Ошибка со стороны сервера');
}

// COMMON ERRORS

class UnknownFailure extends Failure {
  const UnknownFailure() : super('Что-то пошло не так');
}

class ResponseParsingFailure extends Failure {
  const ResponseParsingFailure() : super('Ошибка обработки ответа сервера');
}

// SPECIFIC ERRORS

class UserAlreadyExistsFailure extends Failure {
  const UserAlreadyExistsFailure()
    : super('Введённый email уже зарегистрирован');
}

class UserNotFoundFailure extends Failure {
  const UserNotFoundFailure() : super('Неверный email или пароль');
}

class AlreadyAuthorizedFailure extends Failure {
  const AlreadyAuthorizedFailure()
    : super('Вы уже авторизованы. Сначала выйдите из аккаунта.');
}

class AlreadyInApartmentFailure extends Failure {
  const AlreadyInApartmentFailure() : super('Вы уже состоите в квартире');
}

class NotInApartmentFailure extends Failure {
  const NotInApartmentFailure() : super('Вы не состоите в квартире');
}
