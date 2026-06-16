import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

import '../utils/constants.dart';

class CartService {
  Future<String?> _getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(AppConstants.accessTokenKey);
  }

  Future<Map<String, String>?> _headers() async {
    final token = await _getToken();
    if (token == null || token.isEmpty) return null;
    return {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer $token',
      ...AppConstants.getHeaders,
    };
  }

  Future<Map<String, String>> _requiredHeaders() async {
    final headers = await _headers();
    if (headers == null) {
      throw Exception('Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng.');
    }
    return headers;
  }

  Future<List<Map<String, dynamic>>?> getCart() async {
    try {
      final headers = await _headers();
      if (headers == null) return null;
      final response = await http.get(
        Uri.parse('${AppConstants.baseUrl}/api/cart'),
        headers: headers,
      );
      if (response.statusCode != 200) return null;

      final List<dynamic> data = jsonDecode(utf8.decode(response.bodyBytes));
      return data.cast<Map<String, dynamic>>();
    } catch (e) {
      print('Error loading cart: $e');
      return null;
    }
  }

  Future<Map<String, dynamic>> addItem({
    required String productId,
    required String selectedSize,
    required String selectedColor,
    int quantity = 1,
  }) async {
    try {
      final headers = await _requiredHeaders();
      final response = await http.post(
        Uri.parse('${AppConstants.baseUrl}/api/cart'),
        headers: headers,
        body: jsonEncode({
          'productId': productId,
          'selectedSize': selectedSize,
          'selectedColor': selectedColor,
          'quantity': quantity,
        }),
      );
      if (response.statusCode != 200) {
        throw Exception(
          _responseMessage(response, 'Không thể thêm sản phẩm vào giỏ hàng'),
        );
      }
      return jsonDecode(utf8.decode(response.bodyBytes))
          as Map<String, dynamic>;
    } catch (e) {
      print('Error adding cart item: $e');
      rethrow;
    }
  }

  String _responseMessage(http.Response response, String fallback) {
    try {
      final body = jsonDecode(utf8.decode(response.bodyBytes));
      if (body is Map && body['message'] != null) {
        return body['message'].toString();
      }
    } catch (_) {
      // Keep the fallback below when the backend returns non-JSON content.
    }
    return '$fallback (HTTP ${response.statusCode})';
  }

  Future<Map<String, dynamic>?> updateQuantity(
      String itemId, int quantity) async {
    try {
      final headers = await _headers();
      if (headers == null) return null;
      final response = await http.put(
        Uri.parse('${AppConstants.baseUrl}/api/cart/$itemId'),
        headers: headers,
        body: jsonEncode({'quantity': quantity}),
      );
      if (response.statusCode != 200) return null;
      return jsonDecode(utf8.decode(response.bodyBytes))
          as Map<String, dynamic>;
    } catch (e) {
      print('Error updating cart item: $e');
      return null;
    }
  }

  Future<bool> removeItem(String itemId) async {
    try {
      final headers = await _headers();
      if (headers == null) return false;
      final response = await http.delete(
        Uri.parse('${AppConstants.baseUrl}/api/cart/$itemId'),
        headers: headers,
      );
      return response.statusCode == 204;
    } catch (e) {
      print('Error removing cart item: $e');
      return false;
    }
  }

  Future<bool> clearCart() async {
    try {
      final headers = await _headers();
      if (headers == null) return false;
      final response = await http.delete(
        Uri.parse('${AppConstants.baseUrl}/api/cart'),
        headers: headers,
      );
      return response.statusCode == 204;
    } catch (e) {
      print('Error clearing cart: $e');
      return false;
    }
  }
}
