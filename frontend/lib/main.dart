import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/firebase_options.dart';
import 'package:intl/date_symbol_data_local.dart';
import 'package:riverpod_devtools/riverpod_devtools.dart';
import 'package:frontend/shared/data/providers/auth_provider.dart';
import 'package:frontend/shared/presentation/theme/custom_theme.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:frontend/shared/router/router_provider.dart';

Future<void> main() async {
  await dotenv.load(fileName: ".env");

  WidgetsFlutterBinding.ensureInitialized();

  await SystemChrome.setPreferredOrientations([
    DeviceOrientation.portraitUp, // только вертикально
  ]);

  await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform);

  await initializeDateFormatting("ru_RU");

  await FirebaseMessaging.instance.requestPermission(provisional: true);

  String? fcmToken;
  try {
    if (kIsWeb) {
      fcmToken = await FirebaseMessaging.instance.getToken(
        vapidKey: dotenv.get("FIREBASE_WEB_PUBLIC_KEY"),
      );
    } else {
      fcmToken = await FirebaseMessaging.instance.getToken();
    }
  } catch (e) {
    print(e);
  }

  if (fcmToken != null) {
    print("FCM TOKEN: $fcmToken");
  } else {
    print("NO TOKEN :(");
  }

  FirebaseMessaging.instance.onTokenRefresh
      .listen((fcmToken) {
        print(fcmToken);
      })
      .onError((err) {
        print(err);
      });

  FirebaseMessaging.onMessage.listen((message) {
    debugPrint("NEW MESSAGE");
    debugPrint("TITLE: ${message.notification?.title}");
    debugPrint("BODY: ${message.notification?.body}");
  });

  runApp(
    ProviderScope(observers: [RiverpodDevToolsObserver()], child: MyApp()),
  );
}

class MyApp extends ConsumerWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authProvider);
    final router = ref.watch(routerProvider);

    if (authState.isLoading) {
      return MaterialApp(
        theme: customTheme,
        builder: (context, child) =>
            Scaffold(body: Center(child: CircularProgressIndicator())),
      );
    }

    return MaterialApp.router(
      debugShowCheckedModeBanner: false,
      title: 'Flutter Demo',
      theme: customTheme,
      routerConfig: router,
    );
  }
}
