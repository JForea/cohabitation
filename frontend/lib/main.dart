import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/app.dart';
import 'package:frontend/firebase_options.dart';
import 'package:intl/date_symbol_data_local.dart';
import 'package:riverpod_devtools/riverpod_devtools.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

Future<void> main() async {
  debugRepaintRainbowEnabled = true;
  await dotenv.load(fileName: ".env");

  WidgetsFlutterBinding.ensureInitialized();

  await SystemChrome.setPreferredOrientations([DeviceOrientation.portraitUp]);

  await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform);

  await initializeDateFormatting("ru_RU");

  FirebaseMessaging.instance.onTokenRefresh
      .listen((fcmToken) {
        print(fcmToken);
      })
      .onError((err) {
        print(err);
      });

  runApp(ProviderScope(observers: [RiverpodDevToolsObserver()], child: App()));
}
