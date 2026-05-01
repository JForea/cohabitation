import 'package:flutter_riverpod/flutter_riverpod.dart';

final pageProvider = NotifierProvider<_PageNotifier, int>(
  () => _PageNotifier(),
);

class _PageNotifier extends Notifier<int> {
  @override
  int build() => 0;

  void setIndex(int index) {
    state = index;
  }
}
