import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';

final pageProvider = NotifierProvider<_PageNotifier, int>(
  () => _PageNotifier(),
);

class _PageNotifier extends Notifier<int> {
  @override
  int build() {
    ref.listen(apartmentProvider, (_, _) {
      Future(() => state = 0);
    });

    return 0;
  }

  void setIndex(int index) {
    state = index;
  }
}
