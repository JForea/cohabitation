import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/core/failures/failures.dart';
import 'package:frontend/shared/domain/models/profile/profile.dart';
import 'package:frontend/shared/state/providers/apartment_provider.dart';
import 'package:frontend/shared/data/repositories/profile_repository.dart';
import 'package:frontend/shared/domain/types/role.dart';

final neighboursProvider =
    AsyncNotifierProvider<NeighboursNotifier, List<Profile>>(
      NeighboursNotifier.new,
    );

class NeighboursNotifier extends AsyncNotifier<List<Profile>> {
  late ProfileRepository _profileRepository;

  late int? _apartmentId;

  @override
  Future<List<Profile>> build() async {
    _profileRepository = ref.read(profileRepositoryProvider);
    _apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));

    if (_apartmentId == null) throw NotInApartmentFailure();

    return _profileRepository.getAll(
      apartmentId: _apartmentId!,
      excludeMe: true,
    );
  }

  Future<void> refresh() async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    final neigbours = await _profileRepository.getAll(
      apartmentId: _apartmentId!,
      excludeMe: true,
    );

    state = AsyncData(neigbours);
  }

  Future<void> kick(int profileId) async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    final previousValue = state.value ?? [];

    try {
      final newValue = [...previousValue];
      newValue.removeWhere((p) => p.id == profileId);
      state = AsyncData(newValue);
      await _profileRepository.kick(_apartmentId!, profileId);
    } catch (e) {
      state = AsyncData(previousValue);

      rethrow;
    }
  }

  Future<void> setRole(int profileId, Role role) async {
    if (_apartmentId == null) throw NotInApartmentFailure();

    final previousValue = state.value ?? [];

    try {
      final newValue = [...previousValue];
      for (int i = 0; i < newValue.length; i++) {
        if (newValue[i].id == profileId) {
          newValue[i] = newValue[i].copyWith(role: role);
        }
      }
      state = AsyncData(newValue);
      await _profileRepository.setRole(_apartmentId!, profileId, role);
    } catch (e) {
      state = AsyncData(previousValue);
    }
  }
}
