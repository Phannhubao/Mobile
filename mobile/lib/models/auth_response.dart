class AuthResponse {
  final String accessToken;
  final String refreshToken;
  final String tokenType;
  final String name;
  final String email;

  AuthResponse({
    required this.accessToken,
    required this.refreshToken,
    required this.tokenType,
    required this.name,
    required this.email,
  });

  factory AuthResponse.fromJson(Map<String, dynamic> json) {
    return AuthResponse(
      accessToken: json['accessToken'] ?? '',
      refreshToken: json['refreshToken'] ?? '',
      tokenType: json['tokenType'] ?? 'Bearer',
      name: json['name'] ?? '',
      email: json['email'] ?? '',
    );
  }
}
