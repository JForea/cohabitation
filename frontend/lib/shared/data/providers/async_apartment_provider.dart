import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/shared/data/failures/failures.dart';
import 'package:frontend/shared/data/models/apartment.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/data/providers/user_provider.dart';
import 'package:frontend/shared/data/repositories/apartment_repository.dart';

final asyncApartmentProvider =
    AsyncNotifierProvider<ApartmentNotifier, Apartment?>(ApartmentNotifier.new);

class ApartmentNotifier extends AsyncNotifier<Apartment?> {
  late ApartmentRepository _apartmentRepository;

  late int? _apartmentId;

  @override
  Future<Apartment?> build() async {
    _apartmentRepository = ref.read(apartmentRepositoryProvider);
    _apartmentId = ref.watch(
      userProvider.select((u) => u?.profile?.apartmentId),
    );

    if (_apartmentId == null) {
      return null;
    }

    return _apartmentRepository.get(_apartmentId!);
  }

  Future<Profile?> create({required String name, String? address}) async {
    try {
      state = const AsyncLoading();

      final createApartmentResponse = await _apartmentRepository.create(
        name: name,
        address: address,
      );

      state = AsyncData(
        Apartment(
          address: address,
          budget: createApartmentResponse.budget,
          id: createApartmentResponse.id,
          name: name,
        ),
      );

      return createApartmentResponse.profile;
    } catch (e, st) {
      state = AsyncError(e, st);
      rethrow;
    }
  }

  Future<Profile?> join(String inviteCode) async {
    try {
      state = const AsyncLoading();

      final joinApartmentResponse = await _apartmentRepository.join(inviteCode);

      state = AsyncData(joinApartmentResponse.apartment);

      return joinApartmentResponse.profile;
    } catch (e, st) {
      state = AsyncError(e, st);
      rethrow;
    }
  }

  Future<void> leave() async {
    final previous = state;

    state = AsyncLoading();

    try {
      await _apartmentRepository.leave();

      state = AsyncData(null);
    } catch (e) {
      state = previous;
      rethrow;
    }
  }

  Future<void> generateCode() async {
    final previousValue = state.value;

    if (previousValue == null || _apartmentId == null) {
      throw NotInApartmentFailure();
    }

    final inviteCode = await _apartmentRepository.generateCode(_apartmentId!);

    state = AsyncData(previousValue.copyWith(inviteCode: inviteCode));
  }

  Future<void> setBudget(int budget) async {
    final previousValue = state.value;

    if (previousValue == null || _apartmentId == null) {
      throw NotInApartmentFailure();
    }

    await _apartmentRepository.setBudget(_apartmentId!, budget);

    state = AsyncData(previousValue.copyWith(budget: budget));
  }

  Future<void> delete() async {
    final previous = state;

    if (previous.value == null || _apartmentId == null) {
      throw NotInApartmentFailure();
    }

    state = AsyncLoading();

    try {
      await _apartmentRepository.delete(_apartmentId!);

      state = AsyncData(null);
    } catch (e) {
      state = previous;
      rethrow;
    }
  }
}
