class Tag {
  final String id;
  final String tagName;
  final String? icon;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  Tag({
    required this.id,
    required this.tagName,
    this.icon,
    this.createdAt,
    this.updatedAt,
  });

  factory Tag.fromJson(Map<String, dynamic> json) {
    return Tag(
      id: json['id'] ?? '',
      tagName: json['tagName'] ?? '',
      icon: json['icon'],
      createdAt: DateTime.tryParse(json['createdAt'] ?? ''),
      updatedAt: DateTime.tryParse(json['updatedAt'] ?? ''),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'tagName': tagName,
      'icon': icon,
      'createdAt': createdAt?.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }
}
