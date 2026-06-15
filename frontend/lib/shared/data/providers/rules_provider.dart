import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/failures/failures.dart';
import 'package:frontend/shared/data/models/rule.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/repositories/rule_repository.dart';

final rulesProvider = AsyncNotifierProvider<_RulesNotifier, List<Rule>>(
  _RulesNotifier.new,
);

class _RulesNotifier extends AsyncNotifier<List<Rule>> {
  late RuleRepository _ruleRepository;

  late int? _apartmentId;

  @override
  Future<List<Rule>> build() async {
    _ruleRepository = ref.read(ruleRepositoryProvider);
    _apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));

    if (_apartmentId == null) throw NotInApartmentFailure();

    return _ruleRepository.getAll(_apartmentId!);
  }

  Future<void> refresh() async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    state = AsyncData(await _ruleRepository.getAll(_apartmentId!));
  }

  Future<void> create(String text) async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    final previousValue = state.value ?? [];

    try {
      final rule = await _ruleRepository.create(_apartmentId!, text);

      state = AsyncValue.data([rule, ...previousValue]);
    } catch (e) {
      state = AsyncData(previousValue);
      rethrow;
    }
  }

  Future<void> delete(int id) async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    final previous = state.value ?? [];

    try {
      final current = previous.where((rule) => rule.id != id).toList();

      state = AsyncData(current);

      await _ruleRepository.delete(_apartmentId!, id);
    } catch (e) {
      state = AsyncData(previous);
      rethrow;
    }
  }
}
