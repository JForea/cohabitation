import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/models/rule.dart';
import 'package:frontend/shared/data/network/dio_client.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';

final rulesProvider = AsyncNotifierProvider<_RulesNotifier, List<Rule>>(
  _RulesNotifier.new,
);

class _RulesNotifier extends AsyncNotifier<List<Rule>> {
  late String baseUrl;

  @override
  Future<List<Rule>> build() async {
    final apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));

    if (apartmentId == null) {
      throw Exception("Not in apartment.");
    }

    baseUrl = "/apartments/$apartmentId/rules";

    final response = await AppDio.dio.get(baseUrl);
    List<Rule> rules = (response.data as List)
        .map((json) => Rule.fromJson(json))
        .toList();

    return rules;
  }

  Future<void> refresh() async {
    state = await AsyncValue.guard(() async {
      final response = await AppDio.dio.get(baseUrl);

      return (response.data as List)
          .map((json) => Rule.fromJson(json))
          .toList();
    });
  }

  Future<bool> create(String text) async {
    try {
      final previousValue = state.value;

      final response = await AppDio.dio.post(baseUrl, data: {"text": text});

      final rule = Rule(id: response.data["id"] as int, text: text);

      state = AsyncValue.data([rule, ...?previousValue]);

      return true;
    } catch (e) {
      print(e);
      return false;
    }
  }

  Future<bool> delete(int id) async {
    final previous = state.value ?? [];

    try {
      final newList = previous.where((rule) => rule.id != id).toList();

      state = AsyncData(newList);

      await AppDio.dio.delete("$baseUrl/$id");

      return true;
    } catch (e) {
      print(e);
      state = AsyncData(previous);
      return false;
    }
  }
}
