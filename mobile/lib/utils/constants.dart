class AppConstants {
  static const String _apiMode =
      String.fromEnvironment('API_MODE', defaultValue: 'ngrok');
  static const String _configuredBaseUrl =
      String.fromEnvironment('API_BASE_URL');
  static const String _localBaseUrl = String.fromEnvironment(
    'LOCAL_BASE_URL',
    defaultValue: 'http://localhost:8080',
  );
  static const String _ngrokBaseUrl = String.fromEnvironment(
    'NGROK_BASE_URL',
    defaultValue: 'https://1a90-14-224-164-69.ngrok-free.app',
  );

  static String get baseUrl {
    if (_configuredBaseUrl.isNotEmpty) {
      return _withoutTrailingSlash(_configuredBaseUrl);
    }

    return _withoutTrailingSlash(
      _apiMode.toLowerCase() == 'local' ? _localBaseUrl : _ngrokBaseUrl,
    );
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
    return Uri.parse(baseUrl.endsWith('/') ? baseUrl : '$baseUrl/')
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
