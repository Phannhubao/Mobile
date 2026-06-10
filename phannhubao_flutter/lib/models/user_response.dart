class UserResponse {
  final int id;
  final String name;
  final String email;
  final String provider;
  final String? avatarUrl;
  final List<String> roles;
  final String? dateOfBirth;
  final bool salesNotification;
  final bool newArrivalsNotification;
  final bool deliveryStatusNotification;

  UserResponse({
    required this.id,
    required this.name,
    required this.email,
    required this.provider,
    this.avatarUrl,
    required this.roles,
    this.dateOfBirth,
    this.salesNotification = true,
    this.newArrivalsNotification = true,
    this.deliveryStatusNotification = true,
  });

  factory UserResponse.fromJson(Map<String, dynamic> json) {
    return UserResponse(
      id: json['id'] ?? 0,
      name: json['name'] ?? '',
      email: json['email'] ?? '',
      provider: json['provider'] ?? 'local',
      avatarUrl: json['avatarUrl'],
      roles: json['roles'] != null ? List<String>.from(json['roles']) : [],
      dateOfBirth: json['dateOfBirth'],
      salesNotification: json['salesNotification'] ?? true,
      newArrivalsNotification: json['newArrivalsNotification'] ?? true,
      deliveryStatusNotification: json['deliveryStatusNotification'] ?? true,
    );
  }
}
