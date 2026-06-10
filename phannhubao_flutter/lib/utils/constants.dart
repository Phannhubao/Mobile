class AppConstants {
  static const String _localBaseUrl = 'https://3424-2405-4802-93ea-5b00-61ec-d2c4-f7e6-9370.ngrok-free.app';
  static const String _configuredBaseUrl =
      String.fromEnvironment('API_BASE_URL');

  static String get baseUrl {
    if (_configuredBaseUrl.isNotEmpty) {
      return _withoutTrailingSlash(_configuredBaseUrl);
    }

    return _localBaseUrl;
  }

  static Map<String, String> get getHeaders {
    return {
      'Accept': 'application/json',
      ..._ngrokHeaders,
    };
  }

  static Map<String, String> get jsonHeaders {
    return {
      'Content-Type': 'application/json',
      ..._ngrokHeaders,
    };
  }

  static String resolveUrl(String? value) {
    if (value == null || value.trim().isEmpty) {
      return '';
    }

    final trimmed = value.trim();
    final uri = Uri.tryParse(trimmed);
    if (uri != null && uri.hasScheme) {
      return uri.toString();
    }

    final relativePath =
        trimmed.startsWith('/') ? trimmed.substring(1) : trimmed;
    return Uri.parse('${baseUrl.endsWith('/') ? baseUrl : '$baseUrl/'}')
        .resolve(relativePath)
        .toString();
  }

  static Map<String, String>? imageHeaders(String url) {
    final uri = Uri.tryParse(url);
    final apiUri = Uri.tryParse(baseUrl);
    if (uri == null ||
        apiUri == null ||
        uri.host != apiUri.host ||
        _ngrokHeaders.isEmpty) {
      return null;
    }
    return _ngrokHeaders;
  }

  static Map<String, String> get _ngrokHeaders {
    if (!baseUrl.contains('ngrok-free.app')) {
      return const {};
    }
    return const {'ngrok-skip-browser-warning': 'true'};
  }

  static String _withoutTrailingSlash(String value) {
    return value.endsWith('/') ? value.substring(0, value.length - 1) : value;
  }

  // API Endpoints
  static const String loginEndpoint = '/api/auth/login';
  static const String registerEndpoint = '/api/auth/register';
  static const String refreshEndpoint = '/api/auth/refresh';
  static const String logoutEndpoint = '/api/auth/logout';
  static const String meEndpoint = '/api/users/me';

  // SharedPreferences Keys
  static const String accessTokenKey = 'access_token';
  static const String refreshTokenKey = 'refresh_token';
  static const String userNameKey = 'user_name';
  static const String userEmailKey = 'user_email';
}
