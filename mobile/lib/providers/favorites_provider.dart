import 'package:flutter/material.dart';
import '../models/product.dart';
import '../services/wishlist_service.dart';

class FavoriteItem {
  final Product product;
  final String selectedSize;
  final String selectedColor;
  final bool isSoldOut;

  FavoriteItem({
    required this.product,
    required this.selectedSize,
    required this.selectedColor,
    this.isSoldOut = false,
  });
}

class FavoritesProvider extends ChangeNotifier {
  final List<FavoriteItem> _favorites = [];
  final WishlistService _wishlistService = WishlistService();
  bool _isLoading = false;

  bool get isLoading => _isLoading;
  List<FavoriteItem> get favorites => List.unmodifiable(_favorites);

  Future<void> fetchFavorites() async {
    _isLoading = true;
    notifyListeners();
    try {
      final products = await _wishlistService.getUserWishlist();
      if (products != null) {
        _favorites.clear();
        for (var product in products) {
          _favorites.add(FavoriteItem(
            product: product,
            selectedSize: product.sizes.isNotEmpty ? product.sizes.first : '',
            selectedColor:
                product.colors.isNotEmpty ? product.colors.first : '',
            isSoldOut: product.quantity == 0,
          ));
        }
      }
    } catch (e) {
      print('Error fetching favorites: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  bool isFavorite(String productId) {
    return _favorites.any((item) => item.product.id == productId);
  }

  Future<bool> addFavorite(Product product, String size, String color) async {
    final previousIndex =
        _favorites.indexWhere((item) => item.product.id == product.id);
    final previousItem = previousIndex == -1 ? null : _favorites[previousIndex];

    // Optimistic update
    _favorites.removeWhere((item) => item.product.id == product.id);
    _favorites.add(FavoriteItem(
      product: product,
      selectedSize: size,
      selectedColor: color,
      isSoldOut: product.quantity == 0,
    ));
    notifyListeners();

    // API call
    final success = await _wishlistService.addToWishlist(product.id);
    if (!success) {
      // Revert if failed
      _favorites.removeWhere((item) => item.product.id == product.id);
      if (previousItem != null) {
        _favorites.insert(previousIndex, previousItem);
      }
      notifyListeners();
    }
    return success;
  }

  Future<bool> removeFavorite(String productId) async {
    final index = _favorites.indexWhere((item) => item.product.id == productId);
    if (index == -1) return true;

    final item = _favorites[index];
    _favorites.removeAt(index);
    notifyListeners();

    final success = await _wishlistService.removeFromWishlist(productId);
    if (!success) {
      // Revert if failed
      _favorites.insert(index, item);
      notifyListeners();
    }
    return success;
  }

  Future<bool> toggle(Product product, String size, String color) async {
    if (isFavorite(product.id)) {
      return removeFavorite(product.id);
    } else {
      return addFavorite(product, size, color);
    }
  }

  int get count => _favorites.length;
}
