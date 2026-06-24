import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/domain/models/apartment.dart';
import 'package:frontend/shared/state/providers/async_apartment_provider.dart';

final apartmentProvider = Provider<Apartment?>((ref) {
  final apartmentState = ref.watch(asyncApartmentProvider);
  return apartmentState.value;
});
