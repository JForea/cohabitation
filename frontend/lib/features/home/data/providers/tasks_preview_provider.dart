import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/core/failures/failures.dart';
import 'package:frontend/shared/data/models/task.dart';
import 'package:frontend/shared/data/providers/apartment_provider.dart';
import 'package:frontend/shared/data/repositories/task_repository.dart';

final tasksPreviewProvider = FutureProvider<List<Task>>((ref) async {
  final apartmentId = ref.watch(apartmentProvider.select((a) => a?.id));

  if (apartmentId == null) throw NotInApartmentFailure();

  final taskRepository = ref.read(taskRepositoryProvider);

  final dueTime = DateTime.now().add(Duration(days: 7));

  return taskRepository.getPage(
    apartmentId: apartmentId,
    page: 0,
    pageSize: 3,
    done: false,
    dueTime: dueTime,
  );
});
