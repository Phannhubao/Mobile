class CategoryModel {
  final String id;
  final String? parentId;
  final String categoryName;
  final String? categoryDescription;
  final String? icon;
  final String? image;
  final String? placeholder;
  final bool active;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  CategoryModel({
    required this.id,
    this.parentId,
    required this.categoryName,
    this.categoryDescription,
    this.icon,
    this.image,
    this.placeholder,
    required this.active,
    this.createdAt,
    this.updatedAt,
  });

  factory CategoryModel.fromJson(Map<String, dynamic> json) {
    final parent = json['parent'];
    return CategoryModel(
      id: json['id'] ?? '',
      parentId: json['parentId'] ??
          (parent is Map<String, dynamic> ? parent['id'] : null),
      categoryName: json['categoryName'] ?? '',
      categoryDescription: json['categoryDescription'],
      icon: json['icon'],
      image: json['image'],
      placeholder: json['placeholder'],
      active: json['active'] ?? true,
      createdAt: DateTime.tryParse(json['createdAt'] ?? ''),
      updatedAt: DateTime.tryParse(json['updatedAt'] ?? ''),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'parentId': parentId,
      'categoryName': categoryName,
      'categoryDescription': categoryDescription,
      'icon': icon,
      'image': image,
      'placeholder': placeholder,
      'active': active,
      'createdAt': createdAt?.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }
}
