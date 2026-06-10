import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/auth_response.dart';
import '../models/user_response.dart';
import '../utils/constants.dart';

class AuthService {
  late http.Client _httpClient;

  AuthService() {
    _httpClient = _createHttpClient();
  }

  http.Client _createHttpClient() {
    // Nếu cần bypass certificate cho ngrok, sử dụng:
    HttpOverrides.global = _MyHttpOverrides();
    return http.Client();
  }

  // ───────────── Token helpers ─────────────

  Future<String?> getAccessToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(AppConstants.accessTokenKey);
  }

  Future<void> saveTokens(AuthResponse auth) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(AppConstants.accessTokenKey, auth.accessToken);
    await prefs.setString(AppConstants.refreshTokenKey, auth.refreshToken);
    await prefs.setString(AppConstants.userNameKey, auth.name);
    await prefs.setString(AppConstants.userEmailKey, auth.email);
  }

  Future<void> saveOAuth2Tokens(String accessToken, String refreshToken) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(AppConstants.accessTokenKey, accessToken);
    await prefs.setString(AppConstants.refreshTokenKey, refreshToken);
  }

  Future<void> clearTokens() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(AppConstants.accessTokenKey);
    await prefs.remove(AppConstants.refreshTokenKey);
    await prefs.remove(AppConstants.userNameKey);
    await prefs.remove(AppConstants.userEmailKey);
  }

  Future<bool> isLoggedIn() async {
    final token = await getAccessToken();
    return token != null && token.isNotEmpty;
  }

  // ───────────── API Calls ─────────────

  Future<AuthResponse> login(String email, String password) async {
    try {
      final response = await _httpClient.post(
        Uri.parse('${AppConstants.baseUrl}${AppConstants.loginEndpoint}'),
        headers: {
          'Content-Type': 'application/json',
          ...AppConstants.getHeaders,
        },
        body: jsonEncode({'email': email, 'password': password}),
      ).timeout(
        const Duration(seconds: 30),
        onTimeout: () => throw Exception('Hết thời gian chờ - Kiểm tra kết nối mạng'),
      );

      if (response.statusCode == 200) {
        try {
          final auth = AuthResponse.fromJson(jsonDecode(response.body));
          await saveTokens(auth);
          return auth;
        } catch (e) {
          throw Exception('Lỗi parse dữ liệu: ${e.toString()}');
        }
      } else {
        // Cố gắng parse lỗi từ JSON, nếu không thì hiển thị HTML/plain text
        try {
          final body = jsonDecode(response.body);
          throw Exception(body['message'] ?? 'Đăng nhập thất bại (HTTP ${response.statusCode})');
        } catch (e) {
          // Nếu không parse được JSON (HTML response), trả về error với status code
          throw Exception('Đăng nhập thất bại - ${response.statusCode}. Kiểm tra backend URL ngrok của bạn.');
        }
      }
    } catch (e) {
      if (e.toString().contains('certificate') ||
          e.toString().contains('HandshakeException')) {
        throw Exception(
            'Lỗi kết nối SSL - Hãy kiểm tra URL ngrok của bạn');
      }
      rethrow;
    }
  }

  Future<void> register(String name, String email, String password) async {
    try {
      final response = await _httpClient.post(
        Uri.parse('${AppConstants.baseUrl}${AppConstants.registerEndpoint}'),
        headers: {
          'Content-Type': 'application/json',
          ...AppConstants.getHeaders,
        },
        body: jsonEncode({
          'name': name,
          'email': email,
          'password': password,
        }),
      ).timeout(
        const Duration(seconds: 30),
        onTimeout: () => throw Exception('Hết thời gian chờ - Kiểm tra kết nối mạng'),
      );

      if (response.statusCode != 200) {
        final body = jsonDecode(response.body);
        throw Exception(body['message'] ?? 'Đăng ký thất bại');
      }
    } catch (e) {
      if (e.toString().contains('certificate') ||
          e.toString().contains('HandshakeException')) {
        throw Exception(
            'Lỗi kết nối SSL - Hãy kiểm tra URL ngrok của bạn');
      }
      rethrow;
    }
  }

  Future<UserResponse> getMe() async {
    final token = await getAccessToken();
    final response = await _httpClient.get(
      Uri.parse('${AppConstants.baseUrl}${AppConstants.meEndpoint}'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
        ...AppConstants.getHeaders,
      },
    );

    if (response.statusCode == 200) {
      return UserResponse.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Không thể lấy thông tin user');
    }
  }

  Future<Map<String, dynamic>> getUserProfileStats() async {
    final token = await getAccessToken();
    final response = await _httpClient.get(
      Uri.parse('${AppConstants.baseUrl}/api/users/profile-stats'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
        ...AppConstants.getHeaders,
      },
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body) as Map<String, dynamic>;
    } else {
      throw Exception('Không thể lấy thông tin thống kê người dùng');
    }
  }

  Future<void> createRealOrder(Map<String, dynamic> orderPayload) async {
    final token = await getAccessToken();
    final response = await _httpClient.post(
      Uri.parse('${AppConstants.baseUrl}/api/users/orders'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
        ...AppConstants.getHeaders,
      },
      body: jsonEncode(orderPayload),
    );
    if (response.statusCode != 200) {
      throw Exception('Không thể tạo đơn hàng trên server');
    }
  }

  Future<void> updateProfile({
    required String name,
    required String dateOfBirth,
    required bool sales,
    required bool newArrivals,
    required bool deliveryStatus,
  }) async {
    final token = await getAccessToken();
    final response = await _httpClient.put(
      Uri.parse('${AppConstants.baseUrl}/api/users/profile'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
        ...AppConstants.getHeaders,
      },
      body: jsonEncode({
        'name': name,
        'dateOfBirth': dateOfBirth,
        'salesNotification': sales,
        'newArrivalsNotification': newArrivals,
        'deliveryStatusNotification': deliveryStatus,
      }),
    );
    if (response.statusCode != 200) {
      throw Exception('Cập nhật thông tin cá nhân thất bại');
    }
  }

  Future<List<Map<String, dynamic>>> getUserCards() async {
    final token = await getAccessToken();
    final response = await _httpClient.get(
      Uri.parse('${AppConstants.baseUrl}/api/users/cards'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
        ...AppConstants.getHeaders,
      },
    );
    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(utf8.decode(response.bodyBytes));
      return List<Map<String, dynamic>>.from(data);
    } else {
      return [];
    }
  }

  Future<void> addUserCard(String cardType, String lastFour) async {
    final token = await getAccessToken();
    final response = await _httpClient.post(
      Uri.parse('${AppConstants.baseUrl}/api/users/cards'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
        ...AppConstants.getHeaders,
      },
      body: jsonEncode({
        'cardType': cardType,
        'lastFour': lastFour,
      }),
    );
    if (response.statusCode != 200) {
      throw Exception('Không thể thêm thẻ thanh toán');
    }
  }

  Future<void> simulateAddOrder() async {
    final token = await getAccessToken();
    await _httpClient.post(
      Uri.parse('${AppConstants.baseUrl}/api/users/simulate/order'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
        ...AppConstants.getHeaders,
      },
    );
  }

  Future<void> simulateAddAddress() async {
    final token = await getAccessToken();
    await _httpClient.post(
      Uri.parse('${AppConstants.baseUrl}/api/users/simulate/address'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
        ...AppConstants.getHeaders,
      },
    );
  }

  Future<void> simulateAddCard() async {
    final token = await getAccessToken();
    await _httpClient.post(
      Uri.parse('${AppConstants.baseUrl}/api/users/simulate/card'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
        ...AppConstants.getHeaders,
      },
    );
  }

  Future<void> simulateAddCoupon() async {
    final token = await getAccessToken();
    await _httpClient.post(
      Uri.parse('${AppConstants.baseUrl}/api/users/simulate/coupon'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
        ...AppConstants.getHeaders,
      },
    );
  }

  Future<void> simulateAddReview() async {
    final token = await getAccessToken();
    await _httpClient.post(
      Uri.parse('${AppConstants.baseUrl}/api/users/simulate/review'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
        ...AppConstants.getHeaders,
      },
    );
  }

  Future<void> logout() async {
    try {
      final token = await getAccessToken();
      await _httpClient.post(
        Uri.parse('${AppConstants.baseUrl}${AppConstants.logoutEndpoint}'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
          ...AppConstants.getHeaders,
        },
      );
    } catch (_) {
      // Ignore lỗi mạng, vẫn xóa token local
    } finally {
      await clearTokens();
    }
  }
}

/// Custom HttpOverrides để xử lý certificate ngrok
class _MyHttpOverrides extends HttpOverrides {
  @override
  HttpClient createHttpClient(SecurityContext? context) {
    return super.createHttpClient(context)
      ..badCertificateCallback = (X509Certificate cert, String host, int port) {
        // Cho phép ngrok URLs bỏ qua kiểm tra certificate
        if (host.contains('ngrok') || host.contains('ngrok-free')) {
          return true;
        }
        return false;
      };
  }
}
