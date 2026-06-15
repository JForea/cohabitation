import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/failures/failures.dart';
import 'package:frontend/shared/data/failures/map_dio_exception.dart';
import 'package:frontend/shared/data/models/event.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/data/models/profile/profile_brief.dart';
import 'package:frontend/shared/data/network/api_client.dart';
import 'package:frontend/shared/data/network/api_client_provider.dart';
import 'package:frontend/shared/utils/util_functions.dart';
import 'package:intl/intl.dart';

final eventRepositoryProvider = Provider<EventRepository>((ref) {
  final apiClient = ref.read(apiClientProvider);

  return EventRepository(apiClient);
});

class EventRepository {
  EventRepository(this._apiClient);

  final ApiClient _apiClient;

  String _baseUrl(int apartmentId) {
    return "/apartments/$apartmentId/events";
  }

  Future<Set<DateTime>> loadMonth(int apartmentId, DateTime month) async {
    try {
      final query = {
        "year": month.year,
        "month": DateFormat.MMMM("en_US").format(month).toUpperCase(),
      };

      final response = await _apiClient.get(
        "${_baseUrl(apartmentId)}/calendar",
        queryParameters: query,
      );

      try {
        return (response as List).map((date) => DateTime.parse(date)).toSet();
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<List<Event>> getEventsByDay(int apartmentId, DateTime day) async {
    try {
      final response = await _apiClient.get(
        "${_baseUrl(apartmentId)}/day/${UtilFunctions.dateToStringRequest(day)}",
      );

      try {
        return (response as List).map((e) => Event.fromJson(e)).toList();
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<Event> create({
    required int apartmentId,
    required String name,
    required Profile createdBy,
    String? description,
    required DateTime day,
    TimeOfDay? time,
  }) async {
    try {
      String twoDigits(int n) => n.toString().padLeft(2, '0');

      final response = await _apiClient.post(
        _baseUrl(apartmentId),
        data: {
          "name": name,
          "description": description,
          "time": time != null
              ? "${twoDigits(time.hour)}:${twoDigits(time.minute)}"
              : null,
          "date": UtilFunctions.dateToStringRequest(day),
        },
      );

      try {
        return Event(
          id: response["id"],
          createdBy: ProfileBrief.fromFullProfile(createdBy),
          name: name,
          description: description,
          time: time,
        );
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }
}
