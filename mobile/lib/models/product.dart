import 'tag.dart';

class Product {
  final String id;
  final String slug;
  final String productName;
  final String? brandName;
  final String? imageUrl;
  final String? sku;
  final double salePrice;
  final double? comparePrice;
  final double? buyingPrice;
  final int quantity;
  final String shortDescription;
  final String productDescription;
  final String? productType;
  final bool published;
  final bool disableOutOfStock;
  final double? ratingAverage;
  final int? reviewCount;
  final String? note;
  final List<Tag> tags;
  final List<String> sizes;
  final List<String> colors;
  final List<String> categoryIds;

  bool get hasReviews => (reviewCount ?? 0) > 0;

  double get displayRatingAverage => hasReviews ? (ratingAverage ?? 0.0) : 0.0;

  Product({
    required this.id,
    required this.slug,
    required this.productName,
    this.brandName,
    this.imageUrl,
    this.sku,
    required this.salePrice,
    this.comparePrice,
    this.buyingPrice,
    required this.quantity,
    required this.shortDescription,
    required this.productDescription,
    this.productType,
    required this.published,
    required this.disableOutOfStock,
    this.ratingAverage,
    this.reviewCount,
    this.note,
    required this.tags,
    required this.sizes,
    required this.colors,
    required this.categoryIds,
  });

  factory Product.fromJson(Map<String, dynamic> json) {
    return Product(
      id: json['id'] ?? '',
      slug: json['slug'] ?? '',
      productName: json['productName'] ?? '',
      brandName: json['brandName'],
      imageUrl: json['imageUrl'],
      sku: json['sku'],
      salePrice: (json['salePrice'] as num?)?.toDouble() ?? 0.0,
      comparePrice: (json['comparePrice'] as num?)?.toDouble(),
      buyingPrice: (json['buyingPrice'] as num?)?.toDouble(),
      quantity: json['quantity'] ?? 0,
      shortDescription: json['shortDescription'] ?? '',
      productDescription: json['productDescription'] ?? '',
      productType: json['productType'],
      published: json['published'] ?? false,
      disableOutOfStock: json['disableOutOfStock'] ?? true,
      ratingAverage: (json['ratingAverage'] as num?)?.toDouble(),
      reviewCount: json['reviewCount'],
      note: json['note'],
      tags:
          (json['tags'] as List?)?.map((item) => Tag.fromJson(item)).toList() ??
              [],
      sizes: (json['sizes'] as List?)?.map((e) => e.toString()).toList() ?? [],
      colors:
          (json['colors'] as List?)?.map((e) => e.toString()).toList() ?? [],
      categoryIds:
          (json['categoryIds'] as List?)?.map((e) => e.toString()).toList() ??
              [],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'slug': slug,
      'productName': productName,
      'brandName': brandName,
      'imageUrl': imageUrl,
      'sku': sku,
      'salePrice': salePrice,
      'comparePrice': comparePrice,
      'buyingPrice': buyingPrice,
      'quantity': quantity,
      'shortDescription': shortDescription,
      'productDescription': productDescription,
      'productType': productType,
      'published': published,
      'disableOutOfStock': disableOutOfStock,
      'ratingAverage': ratingAverage,
      'reviewCount': reviewCount,
      'note': note,
      'tags': tags.map((t) => t.toJson()).toList(),
      'sizes': sizes,
      'colors': colors,
      'categoryIds': categoryIds,
    };
  }
}
