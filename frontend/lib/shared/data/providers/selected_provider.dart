import 'package:flutter_riverpod/flutter_riverpod.dart';

final selectedProvider =
    NotifierProvider.family<_SelectedNotifier, Set<int>, String>(
      _SelectedNotifier.new,
    );

class _SelectedNotifier extends Notifier<Set<int>> {
  _SelectedNotifier(this.key);
  final String key;

  @override
  Set<int> build() => {};

  void select(int id) {
    state = {...state, id};
  }

  void selectCancel(int id) {
    final newState = {...state};
    newState.remove(id);
    state = newState;
  }
}
