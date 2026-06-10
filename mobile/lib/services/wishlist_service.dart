import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/product.dart';
import '../utils/constants.dart';

class WishlistService {
  Future<String?> _getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(AppConstants.accessTokenKey);
  }

  Future<List<Product>?> getUserWishlist() async {
    try {
      final token = await _getToken();
      if (token == null || token.isEmpty) return null;

      final url = '${AppConstants.baseUrl}/api/wishlist';
      print('>>> [WishlistService] GET $url');
      final response = await http.get(
        Uri.parse(url),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
          ...AppConstants.getHeaders,
        },
      );

      print(
          '>>> [WishlistService] getUserWishlist - status: ${response.statusCode}');
      print('>>> [WishlistService] getUserWishlist - body: ${response.body}');

      if (response.statusCode == 200) {
        final List<dynamic> body = jsonDecode(utf8.decode(response.bodyBytes));
        return body.map((item) => Product.fromJson(item)).toList();
      }
      return null;
    } catch (e) {
      print('>>> [WishlistService] Error getting wishlist: $e');
      return null;
    }
  }

  Future<bool> addToWishlist(String productId) async {
    try {
      final token = await _getToken();
      print('>>> [WishlistService] addToWishlist - productId: $productId');

      if (token == null || token.isEmpty) {
        print('>>> [WishlistService] addToWishlist - SKIPPED: no token!');
        return false;
      }

      final url = '${AppConstants.baseUrl}/api/wishlist/$productId';
      print('>>> [WishlistService] POST $url');
      final response = await http.post(
        Uri.parse(url),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
          ...AppConstants.getHeaders,
        },
      );

      print(
          '>>> [WishlistService] addToWishlist - status: ${response.statusCode}');
      print('>>> [WishlistService] addToWishlist - body: ${response.body}');

      return response.statusCode == 200;
    } catch (e) {
      print('>>> [WishlistService] Error adding to wishlist: $e');
      return false;
    }
  }

  Future<bool> removeFromWishlist(String productId) async {
    try {
      final token = await _getToken();
      print('>>> [WishlistService] removeFromWishlist - productId: $productId');

      if (token == null || token.isEmpty) {
        print('>>> [WishlistService] removeFromWishlist - SKIPPED: no token!');
        return false;
      }

      final url = '${AppConstants.baseUrl}/api/wishlist/$productId';
      print('>>> [WishlistService] DELETE $url');
      final response = await http.delete(
        Uri.parse(url),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
          ...AppConstants.getHeaders,
        },
      );

      print(
          '>>> [WishlistService] removeFromWishlist - status: ${response.statusCode}');
      print(
          '>>> [WishlistService] removeFromWishlist - body: ${response.body}');

      return response.statusCode == 200;
    } catch (e) {
      print('>>> [WishlistService] Error removing from wishlist: $e');
      return false;
    }
  }

  Future<bool> checkWishlistStatus(String productId) async {
    try {
      final token = await _getToken();
      if (token == null || token.isEmpty) return false;

      final response = await http.get(
        Uri.parse('${AppConstants.baseUrl}/api/wishlist/$productId/check'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
          ...AppConstants.getHeaders,
        },
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body) as bool;
      }
      return false;
    } catch (e) {
      print('>>> [WishlistService] Error checking wishlist status: $e');
      return false;
    }
  }
}
