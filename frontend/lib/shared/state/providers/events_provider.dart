import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/core/failures/failures.dart';
import 'package:frontend/shared/data/models/event.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/repositories/event_repository.dart';

final eventsProvider =
    AsyncNotifierProvider.family<EventsNotifier, List<Event>, DateTime>(
      EventsNotifier.new,
    );

class EventsNotifier extends AsyncNotifier<List<Event>> {
  EventsNotifier(DateTime day) : _day = day;
  final DateTime _day;

  late EventRepository _eventRepository;

  late int? _apartmentId;

  @override
  FutureOr<List<Event>> build() async {
    _eventRepository = ref.read(eventRepositoryProvider);
    _apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));

    if (_apartmentId == null) throw NotInApartmentFailure();

    return _eventRepository.getEventsByDay(_apartmentId!, _day);
  }

  Future<void> create({
    required String name,
    required Profile createdBy,
    String? description,
    TimeOfDay? time,
  }) async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    final event = await _eventRepository.create(
      apartmentId: _apartmentId!,
      name: name,
      description: description,
      createdBy: createdBy,
      day: _day,
      time: time,
    );

    state = AsyncData([event, ...?state.value]);
  }

  Future<void> refresh() async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    state = AsyncData(
      await _eventRepository.getEventsByDay(_apartmentId!, _day),
    );
  }
}
