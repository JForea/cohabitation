class Rule {
  const Rule({required this.id, required this.text});

  factory Rule.fromJson(Map<String, dynamic> json) {
    return Rule(id: json["id"], text: json["text"]);
  }

  final int id;
  final String text;

  Rule copyWith({int? id, String? text}) {
    return Rule(id: id ?? this.id, text: text ?? this.text);
  }
}
