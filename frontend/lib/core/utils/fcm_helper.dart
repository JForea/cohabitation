import 'dart:io';

import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:uuid/uuid.dart';

class FcmHelper {
  FcmHelper._internal();

  static const _deviceIdKey = "deviceId";

  static final FlutterSecureStorage _storage = FlutterSecureStorage();
  static final Uuid _uuid = Uuid();
  static final String _vapidKey = dotenv.get(
    "FIREBASE_WEB_PUBLIC_KEY",
    fallback: null,
  );

  static Future<bool> requestPermission() async {
    final settings = await FirebaseMessaging.instance.requestPermission(
      provisional: true,
    );

    return settings.authorizationStatus == AuthorizationStatus.authorized ||
        settings.authorizationStatus == AuthorizationStatus.provisional;
  }

  static Future<String> getDeviceId() async {
    String? deviceId = await _storage.read(key: _deviceIdKey);

    if (deviceId == null) {
      deviceId = _uuid.v4();
      await _storage.write(key: _deviceIdKey, value: deviceId);
    }

    return deviceId;
  }

  static Future<String?> getToken() async {
    try {
      String? fcmToken;
      if (kIsWeb) {
        fcmToken = await FirebaseMessaging.instance.getToken(
          vapidKey: _vapidKey,
        );
      } else {
        fcmToken = await FirebaseMessaging.instance.getToken();
      }

      return fcmToken;
    } catch (e) {
      print(e);
      return null;
    }
  }

  static String? getPlatform() {
    if (kIsWeb) {
      return "WEB";
    }
    if (Platform.isAndroid) {
      return "ANDROID";
    }
    if (Platform.isIOS) {
      return "IOS";
    }

    return null;
  }
}
