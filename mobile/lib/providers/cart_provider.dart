import 'package:flutter/material.dart';
import '../models/product.dart';
import '../services/cart_service.dart';

class CartItem {
  String? id;
  final Product product;
  final String selectedSize;
  final String selectedColor;
  int quantity;

  CartItem({
    this.id,
    required this.product,
    required this.selectedSize,
    required this.selectedColor,
    this.quantity = 1,
  });
}

class PromoCode {
  final String code;
  final String title;
  final double discountPercent;
  final String discountLabel;
  final int daysRemaining;
  final String? imageUrl; // For custom preview if wanted

  PromoCode({
    required this.code,
    required this.title,
    required this.discountPercent,
    required this.discountLabel,
    required this.daysRemaining,
    this.imageUrl,
  });
}

class CartProvider extends ChangeNotifier {
  final List<CartItem> _items = [];
  final CartService _cartService = CartService();
  PromoCode? _activePromoCode;
  bool _isLoading = false;
  String? _errorMessage;

  List<CartItem> get items => List.unmodifiable(_items);
  PromoCode? get activePromoCode => _activePromoCode;
  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;

  int get itemCount => _items.fold(0, (sum, item) => sum + item.quantity);

  double get totalAmount {
    return _items.fold(
        0.0, (sum, item) => sum + (item.product.salePrice * item.quantity));
  }

  double get discountAmount {
    if (_activePromoCode == null) return 0.0;
    return totalAmount * (_activePromoCode!.discountPercent / 100.0);
  }

  double get finalAmount {
    final val = totalAmount - discountAmount;
    return val < 0 ? 0.0 : val;
  }

  Future<void> fetchCart() async {
    _isLoading = true;
    notifyListeners();
    final data = await _cartService.getCart();
    if (data != null) {
      _items
        ..clear()
        ..addAll(data.map(_fromJson));
    }
    _isLoading = false;
    notifyListeners();
  }

  Future<bool> addItem(Product product, String size, String color) async {
    _errorMessage = null;
    final existingIndex = _items.indexWhere((item) =>
        item.product.id == product.id &&
        item.selectedSize == size &&
        item.selectedColor == color);

    if (existingIndex >= 0) {
      _items[existingIndex].quantity++;
    } else {
      _items.add(CartItem(
        product: product,
        selectedSize: size,
        selectedColor: color,
        quantity: 1,
      ));
    }
    notifyListeners();

    try {
      final response = await _cartService.addItem(
        productId: product.id,
        selectedSize: size,
        selectedColor: color,
      );
      _replaceFromResponse(response);
      notifyListeners();
      return true;
    } catch (e) {
      _errorMessage = _cleanError(e);
      if (existingIndex >= 0) {
        _items[existingIndex].quantity--;
      } else {
        _items.removeWhere((item) =>
            item.product.id == product.id &&
            item.selectedSize == size &&
            item.selectedColor == color);
      }
      notifyListeners();
      return false;
    }
  }

  Future<void> removeItem(String productId, String size, String color) async {
    final index = _items.indexWhere((item) =>
        item.product.id == productId &&
        item.selectedSize == size &&
        item.selectedColor == color);
    if (index < 0) return;

    final item = _items.removeAt(index);
    notifyListeners();
    if (item.id != null && !await _cartService.removeItem(item.id!)) {
      _items.insert(index, item);
      notifyListeners();
    }
  }

  Future<void> incrementQuantity(
      String productId, String size, String color) async {
    final index = _items.indexWhere((item) =>
        item.product.id == productId &&
        item.selectedSize == size &&
        item.selectedColor == color);
    if (index >= 0) {
      _items[index].quantity++;
      notifyListeners();

      try {
        final response = await _cartService.addItem(
          productId: productId,
          selectedSize: size,
          selectedColor: color,
        );
        _replaceFromResponse(response);
      } catch (e) {
        _errorMessage = _cleanError(e);
        _items[index].quantity--;
      }
      notifyListeners();
    }
  }

  Future<void> decrementQuantity(
      String productId, String size, String color) async {
    final index = _items.indexWhere((item) =>
        item.product.id == productId &&
        item.selectedSize == size &&
        item.selectedColor == color);
    if (index >= 0) {
      final item = _items[index];
      if (_items[index].quantity > 1) {
        _items[index].quantity--;
        notifyListeners();
        if (item.id != null) {
          final response =
              await _cartService.updateQuantity(item.id!, item.quantity);
          if (response != null) {
            _replaceFromResponse(response);
          } else {
            item.quantity++;
          }
        }
      } else {
        _items.removeAt(index);
        notifyListeners();
        if (item.id != null && !await _cartService.removeItem(item.id!)) {
          _items.insert(index, item);
        }
      }
      notifyListeners();
    }
  }

  void applyPromoCode(PromoCode promo) {
    _activePromoCode = promo;
    notifyListeners();
  }

  void removePromoCode() {
    _activePromoCode = null;
    notifyListeners();
  }

  Future<void> clearCart() async {
    clearLocalCart();
    await _cartService.clearCart();
  }

  void clearLocalCart() {
    _items.clear();
    _activePromoCode = null;
    notifyListeners();
  }

  CartItem _fromJson(Map<String, dynamic> json) {
    return CartItem(
      id: json['id']?.toString(),
      product: Product.fromJson(json['product'] as Map<String, dynamic>),
      selectedSize: json['selectedSize']?.toString() ?? '',
      selectedColor: json['selectedColor']?.toString() ?? '',
      quantity: (json['quantity'] as num?)?.toInt() ?? 1,
    );
  }

  void _replaceFromResponse(Map<String, dynamic> response) {
    final serverItem = _fromJson(response);
    final index = _items.indexWhere((item) =>
        item.product.id == serverItem.product.id &&
        item.selectedSize == serverItem.selectedSize &&
        item.selectedColor == serverItem.selectedColor);
    if (index >= 0) {
      _items[index].id = serverItem.id;
      _items[index].quantity = serverItem.quantity;
    } else {
      _items.add(serverItem);
    }
  }

  String _cleanError(Object error) {
    return error.toString().replaceFirst('Exception: ', '');
  }
}
