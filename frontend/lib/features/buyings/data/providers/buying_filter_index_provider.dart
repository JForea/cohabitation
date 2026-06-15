import 'package:flutter_riverpod/flutter_riverpod.dart';

final buyingFilterIndexProvider = NotifierProvider(
  BuyingFilterIndexNotifier.new,
);

class BuyingFilterIndexNotifier extends Notifier<int> {
  @override
  int build() => 0;

  void setIndex(int index) => state = index;
}
