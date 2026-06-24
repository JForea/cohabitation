class StatusResponse {
  const StatusResponse({required this.status});

  final bool status;

  factory StatusResponse.fromJson(Map<String, dynamic> json) {
    return StatusResponse(status: json["status"] as bool);
  }
}
