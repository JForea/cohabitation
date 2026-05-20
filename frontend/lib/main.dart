import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:frontend/app.dart';
import 'package:frontend/firebase_options.dart';
import 'package:frontend/shared/data/network/auth_session_provider.dart';
import 'package:intl/date_symbol_data_local.dart';
import 'package:riverpod_devtools/riverpod_devtools.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

Future<void> main() async {
  await dotenv.load(fileName: ".env");

  final authSession = AuthSession(FlutterSecureStorage());
  await authSession.init();

  await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform);

  WidgetsFlutterBinding.ensureInitialized();

  await SystemChrome.setPreferredOrientations([DeviceOrientation.portraitUp]);

  await initializeDateFormatting("ru_RU");

  runApp(
    ProviderScope(
      overrides: [authSessionProvider.overrideWithValue(authSession)],
      observers: [RiverpodDevToolsObserver()],
      child: App(),
    ),
  );
}
