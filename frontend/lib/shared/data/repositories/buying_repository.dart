import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/buyings/data/models/buying_redacted.dart';
import 'package:frontend/shared/data/failures/failures.dart';
import 'package:frontend/shared/data/failures/map_dio_exceptiond.dart';
import 'package:frontend/shared/data/models/buying.dart';
import 'package:frontend/shared/data/models/profile/profile.dart';
import 'package:frontend/shared/data/models/profile/profile_brief.dart';
import 'package:frontend/shared/data/network/dio_provider.dart';

final buyingRepositoryProvider = Provider<BuyingRepository>((ref) {
  final dio = ref.read(dioProvider);

  return BuyingRepository(dio);
});

class BuyingRepository {
  BuyingRepository(this._dio);

  final Dio _dio;

  String _baseUrl(int apartmentId) {
    return "/apartments/$apartmentId/buyings";
  }

  Future<List<Buying>> getPage({
    required int apartmentId,
    required int page,
    required int pageSize,
    required bool isPublic,
  }) async {
    try {
      final query = {
        'page': '$page',
        'size': '$pageSize',
        'isPublic': '$isPublic',
      };

      final response = await _dio.get(
        _baseUrl(apartmentId),
        queryParameters: query,
      );

      final data = response.data as List;

      try {
        return data.map((buyingJson) => Buying.fromJson(buyingJson)).toList();
      } catch (e) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<Buying> create({
    required int apartmentId,
    required Profile createdBy,
    required BuyingRedacted buyingRedacted,
    Profile? assignedTo,
    required bool isPublic,
  }) async {
    try {
      final response = await _dio.post(
        _baseUrl(apartmentId),
        data: {
          "name": buyingRedacted.name,
          "quantity": buyingRedacted.quantity,
          "assignedTo": assignedTo?.id,
          "category": buyingRedacted.category.name.toUpperCase(),
          "isPublic": isPublic,
        },
      );

      try {
        return Buying(
          id: response.data["id"] as int,
          createdBy: ProfileBrief.fromFullProfile(createdBy),
          name: buyingRedacted.name,
          quantity: buyingRedacted.quantity,
          assignedTo: assignedTo != null
              ? ProfileBrief.fromFullProfile(assignedTo)
              : null,
          category: buyingRedacted.category,
        );
      } catch (_) {
        throw ResponseParsingFailure();
      }
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<List<Buying>> createMany({
    required int apartmentId,
    required Profile createdBy,
    required List<BuyingRedacted> buyingsRedacted,
    Profile? assignedTo,
    required bool isPublic,
  }) async {
    try {
      final response = await _dio.post(
        "${_baseUrl(apartmentId)}/bulk",
        data: {
          "buyings": buyingsRedacted
              .map(
                (b) => {
                  "name": b.name,
                  "quantity": b.quantity,
                  "category": b.category.name.toUpperCase(),
                },
              )
              .toList(),
          "assignedTo": assignedTo?.id,
          "isPublic": isPublic,
        },
      );

      final List<Buying> buyings = [];

      try {
        for (int i = 0; i < buyingsRedacted.length; i++) {
          buyings.add(
            Buying(
              id: response.data[i]["id"] as int,
              createdBy: ProfileBrief.fromFullProfile(createdBy),
              name: buyingsRedacted[i].name,
              quantity: buyingsRedacted[i].quantity,
              category: buyingsRedacted[i].category,
            ),
          );
        }
      } catch (e) {
        throw ResponseParsingFailure();
      }

      return buyings;
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<void> switchBuyingStatus({
    required int apartmentId,
    required int buyingId,
    required Profile userProfile,
  }) async {
    try {
      await _dio.patch("${_baseUrl(apartmentId)}/$buyingId");
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }

  Future<void> deleteMany(int apartmentId, List<int> ids) async {
    try {
      await _dio.delete(_baseUrl(apartmentId), data: ids);
    } on DioException catch (e) {
      throw mapDioException(e);
    }
  }
}
