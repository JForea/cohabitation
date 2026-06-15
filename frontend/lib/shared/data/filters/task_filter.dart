class TaskFilter {
  const TaskFilter({this.assignedTo, this.done});

  final int? assignedTo;
  final bool? done;

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        other is TaskFilter &&
            other.assignedTo == assignedTo &&
            other.done == done;
  }
}
