import 'dart:convert';

import 'package:http/http.dart' as http;

import '../models/category.dart';
import '../models/tag.dart';
import '../utils/constants.dart';
import 'auth_service.dart';

class CatalogService {
  final AuthService _authService;

  CatalogService({AuthService? authService})
      : _authService = authService ?? AuthService();

  Future<List<CategoryModel>> getCategories() async {
    final response = await http.get(
      Uri.parse('${AppConstants.baseUrl}/api/categories'),
      headers: {
        'Content-Type': 'application/json',
        ...AppConstants.getHeaders,
      },
    );
    _ensureSuccess(response);
    final data = jsonDecode(utf8.decode(response.bodyBytes)) as List<dynamic>;
    return data
        .map((item) => CategoryModel.fromJson(item as Map<String, dynamic>))
        .toList();
  }

  Future<List<Tag>> getTags() async {
    final response = await http.get(
      Uri.parse('${AppConstants.baseUrl}/api/tags'),
      headers: {
        'Content-Type': 'application/json',
        ...AppConstants.getHeaders,
      },
    );
    _ensureSuccess(response);
    final data = jsonDecode(utf8.decode(response.bodyBytes)) as List<dynamic>;
    return data
        .map((item) => Tag.fromJson(item as Map<String, dynamic>))
        .toList();
  }

  Future<CategoryModel> createCategory(Map<String, dynamic> data) async {
    final response = await http.post(
      Uri.parse('${AppConstants.baseUrl}/api/categories'),
      headers: await _authorizedHeaders(),
      body: jsonEncode(data),
    );
    _ensureSuccess(response);
    return CategoryModel.fromJson(_decodeMap(response));
  }

  Future<CategoryModel> updateCategory(
    String id,
    Map<String, dynamic> data,
  ) async {
    final response = await http.put(
      Uri.parse('${AppConstants.baseUrl}/api/categories/$id'),
      headers: await _authorizedHeaders(),
      body: jsonEncode(data),
    );
    _ensureSuccess(response);
    return CategoryModel.fromJson(_decodeMap(response));
  }

  Future<void> deleteCategory(String id) async {
    final response = await http.delete(
      Uri.parse('${AppConstants.baseUrl}/api/categories/$id'),
      headers: await _authorizedHeaders(),
    );
    _ensureSuccess(response);
  }

  Future<Tag> createTag(Map<String, dynamic> data) async {
    final response = await http.post(
      Uri.parse('${AppConstants.baseUrl}/api/tags'),
      headers: await _authorizedHeaders(),
      body: jsonEncode(data),
    );
    _ensureSuccess(response);
    return Tag.fromJson(_decodeMap(response));
  }

  Future<Tag> updateTag(String id, Map<String, dynamic> data) async {
    final response = await http.put(
      Uri.parse('${AppConstants.baseUrl}/api/tags/$id'),
      headers: await _authorizedHeaders(),
      body: jsonEncode(data),
    );
    _ensureSuccess(response);
    return Tag.fromJson(_decodeMap(response));
  }

  Future<void> deleteTag(String id) async {
    final response = await http.delete(
      Uri.parse('${AppConstants.baseUrl}/api/tags/$id'),
      headers: await _authorizedHeaders(),
    );
    _ensureSuccess(response);
  }

  Future<Map<String, String>> _authorizedHeaders() async {
    final token = await _authService.getAccessToken();
    if (token == null || token.isEmpty) {
      throw Exception('Please sign in with an admin account');
    }
    return {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer $token',
      ...AppConstants.getHeaders,
    };
  }

  Map<String, dynamic> _decodeMap(http.Response response) {
    return jsonDecode(utf8.decode(response.bodyBytes)) as Map<String, dynamic>;
  }

  void _ensureSuccess(http.Response response) {
    if (response.statusCode >= 200 && response.statusCode < 300) {
      return;
    }

    String message = 'Request failed (${response.statusCode})';
    try {
      final body =
          jsonDecode(utf8.decode(response.bodyBytes)) as Map<String, dynamic>;
      message = body['error']?.toString() ??
          body['message']?.toString() ??
          (body.isNotEmpty ? body.values.first.toString() : message);
    } catch (_) {}
    throw Exception(message);
  }
}
