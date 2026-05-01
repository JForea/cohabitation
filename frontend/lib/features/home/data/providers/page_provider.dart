import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/notifiers/page_notifier.dart';

final pageProvider = NotifierProvider<PageNotifier, int>(() => PageNotifier());
