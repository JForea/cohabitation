import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/models/event.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/data/models/profile/profile_brief.dart';
import 'package:frontend/shared/data/network/dio_client.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/utils/util_functions.dart';

final eventsProvider =
    AsyncNotifierProvider.family<_EventsNotifier, List<Event>, DateTime>(
      _EventsNotifier.new,
    );

class _EventsNotifier extends AsyncNotifier<List<Event>> {
  _EventsNotifier(this.day);
  final DateTime day;
  late String baseUrl;

  @override
  FutureOr<List<Event>> build() async {
    final apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));

    if (apartmentId == null) {
      throw Exception("Not in apartment.");
    }

    baseUrl = "/apartments/$apartmentId/events";

    return _fetchData(day);
  }

  Future<List<Event>> _fetchData(DateTime date) async {
    final response = await AppDio.dio.get(
      "$baseUrl/day/${UtilFunctions.dateToStringRequest(date)}",
    );

    return (response.data as List).map((e) => Event.fromJson(e)).toList();
  }

  Future<void> refresh() async {
    state = await AsyncValue.guard(() => _fetchData(day));
  }

  Future<bool> create({
    required String name,
    required Profile createdBy,
    String? description,
    TimeOfDay? time,
  }) async {
    try {
      String twoDigits(int n) => n.toString().padLeft(2, '0');

      final response = await AppDio.dio.post(
        baseUrl,
        data: {
          "name": name,
          "description": description,
          "time": time != null
              ? "${twoDigits(time.hour)}:${twoDigits(time.minute)}"
              : null,
          "date": UtilFunctions.dateToStringRequest(day),
        },
      );

      final event = Event(
        id: response.data["id"],
        createdBy: ProfileBrief.fromFullProfile(createdBy),
        name: name,
        description: description,
        time: time,
      );

      final previousValue = state.value;

      state = AsyncValue.data([event, ...?previousValue]);

      return true;
    } catch (e) {
      return false;
    }
  }
}
