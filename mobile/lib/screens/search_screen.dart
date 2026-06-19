import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import '../models/product.dart';
import '../services/product_service.dart';
import '../utils/constants.dart';
import 'product_detail_screen.dart';

Future<void> openProductSearch(BuildContext context,
    {String initialQuery = ''}) {
  return Navigator.of(context, rootNavigator: true).push(
    MaterialPageRoute(
      builder: (_) => SearchScreen(initialQuery: initialQuery),
    ),
  );
}

class SearchScreen extends StatefulWidget {
  final String initialQuery;

  const SearchScreen({super.key, this.initialQuery = ''});

  @override
  State<SearchScreen> createState() => _SearchScreenState();
}

class _SearchScreenState extends State<SearchScreen> {
  final ProductService _productService = ProductService();
  late final TextEditingController _searchController;
  List<Product> _products = [];
  bool _isLoading = true;
  String _query = '';

  @override
  void initState() {
    super.initState();
    _query = widget.initialQuery;
    _searchController = TextEditingController(text: widget.initialQuery)
      ..addListener(() {
        setState(() => _query = _searchController.text);
      });
    _loadProducts();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _loadProducts() async {
    final products = await _productService.getAllProducts();
    if (!mounted) return;
    setState(() {
      _products = products;
      _isLoading = false;
    });
  }

  List<Product> get _filteredProducts {
    final query = _query.trim().toLowerCase();
    if (query.isEmpty) return _products;

    return _products.where((product) {
      final searchableText = [
        product.productName,
        product.brandName ?? '',
        product.shortDescription,
        product.productDescription,
        ...product.tags.map((tag) => tag.tagName),
        ...product.colors,
        ...product.sizes,
      ].join(' ').toLowerCase();
      return searchableText.contains(query);
    }).toList();
  }

  @override
  Widget build(BuildContext context) {
    final scale = (MediaQuery.of(context).size.width / 375).clamp(0.5, 1.5);
    final results = _filteredProducts;

    return Scaffold(
      backgroundColor: const Color(0xFFF9F9F9),
      body: SafeArea(
        child: Column(
          children: [
            Container(
              color: Colors.white,
              padding: EdgeInsets.fromLTRB(
                  4 * scale, 8 * scale, 12 * scale, 10 * scale),
              child: Row(
                children: [
                  IconButton(
                    icon: Icon(Icons.arrow_back_ios_new,
                        color: const Color(0xFF222222), size: 18 * scale),
                    onPressed: () => Navigator.maybePop(context),
                  ),
                  Expanded(
                    child: Container(
                      height: 44 * scale,
                      decoration: BoxDecoration(
                        color: const Color(0xFFF2F2F2),
                        borderRadius: BorderRadius.circular(22 * scale),
                      ),
                      padding: EdgeInsets.symmetric(horizontal: 14 * scale),
                      child: Row(
                        children: [
                          Icon(Icons.search,
                              size: 22 * scale, color: const Color(0xFF9B9B9B)),
                          SizedBox(width: 8 * scale),
                          Expanded(
                            child: TextField(
                              controller: _searchController,
                              autofocus: true,
                              textInputAction: TextInputAction.search,
                              decoration: InputDecoration(
                                hintText: 'Search',
                                hintStyle: GoogleFonts.inter(
                                  fontSize: 16 * scale,
                                  color: const Color(0xFF9B9B9B),
                                ),
                                border: InputBorder.none,
                                isCollapsed: true,
                              ),
                              style: GoogleFonts.inter(
                                fontSize: 16 * scale,
                                color: const Color(0xFF222222),
                              ),
                            ),
                          ),
                          if (_query.isNotEmpty)
                            GestureDetector(
                              onTap: _searchController.clear,
                              child: Icon(Icons.close,
                                  size: 20 * scale,
                                  color: const Color(0xFF9B9B9B)),
                            ),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ),
            Expanded(
              child: _isLoading
                  ? const Center(child: CircularProgressIndicator())
                  : results.isEmpty
                      ? _EmptySearchState(scale: scale)
                      : ListView.separated(
                          padding: EdgeInsets.all(16 * scale),
                          physics: const BouncingScrollPhysics(),
                          itemCount: results.length,
                          separatorBuilder: (_, __) =>
                              SizedBox(height: 12 * scale),
                          itemBuilder: (context, index) {
                            return _SearchResultTile(
                              product: results[index],
                              scale: scale,
                            );
                          },
                        ),
            ),
          ],
        ),
      ),
    );
  }
}

class _SearchResultTile extends StatelessWidget {
  final Product product;
  final double scale;

  const _SearchResultTile({
    required this.product,
    required this.scale,
  });

  @override
  Widget build(BuildContext context) {
    final imageUrl = AppConstants.resolveUrl(product.imageUrl);
    final reviewCount = product.reviewCount ?? 0;
    final salePrice = product.salePrice.round();
    final comparePrice = product.comparePrice?.round();

    return Material(
      color: Colors.white,
      borderRadius: BorderRadius.circular(8 * scale),
      child: InkWell(
        borderRadius: BorderRadius.circular(8 * scale),
        onTap: () {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (_) => ProductDetailScreen(product: product),
            ),
          );
        },
        child: Padding(
          padding: EdgeInsets.all(10 * scale),
          child: Row(
            children: [
              ClipRRect(
                borderRadius: BorderRadius.circular(8 * scale),
                child: Container(
                  width: 76 * scale,
                  height: 96 * scale,
                  color: const Color(0xFFEFEFEF),
                  child: imageUrl.isEmpty
                      ? Icon(Icons.image_not_supported_outlined,
                          color: const Color(0xFF9B9B9B), size: 28 * scale)
                      : Image.network(
                          imageUrl,
                          fit: BoxFit.cover,
                          headers: AppConstants.imageHeaders(imageUrl),
                          errorBuilder: (_, __, ___) => Icon(
                            Icons.image_not_supported_outlined,
                            color: const Color(0xFF9B9B9B),
                            size: 28 * scale,
                          ),
                        ),
                ),
              ),
              SizedBox(width: 12 * scale),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      product.brandName ?? '',
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: GoogleFonts.inter(
                        fontSize: 12 * scale,
                        color: const Color(0xFF9B9B9B),
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                    SizedBox(height: 3 * scale),
                    Text(
                      product.productName,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                      style: GoogleFonts.outfit(
                        fontSize: 17 * scale,
                        color: const Color(0xFF222222),
                        fontWeight: FontWeight.w700,
                        height: 1.15,
                      ),
                    ),
                    SizedBox(height: 7 * scale),
                    Row(
                      children: [
                        ...List.generate(5, (i) {
                          final isFilled =
                              i < product.displayRatingAverage.floor();
                          return Icon(
                            Icons.star,
                            size: 13 * scale,
                            color: isFilled
                                ? const Color(0xFFFFBA49)
                                : const Color(0xFF9B9B9B),
                          );
                        }),
                        SizedBox(width: 4 * scale),
                        Text(
                          '($reviewCount)',
                          style: GoogleFonts.inter(
                            fontSize: 11 * scale,
                            color: const Color(0xFF9B9B9B),
                          ),
                        ),
                      ],
                    ),
                    SizedBox(height: 8 * scale),
                    Row(
                      children: [
                        if (comparePrice != null &&
                            comparePrice > salePrice) ...[
                          Text(
                            '$comparePrice\$',
                            style: GoogleFonts.inter(
                              fontSize: 14 * scale,
                              color: const Color(0xFF9B9B9B),
                              decoration: TextDecoration.lineThrough,
                              decorationColor: const Color(0xFF9B9B9B),
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                          SizedBox(width: 8 * scale),
                        ],
                        Text(
                          '$salePrice\$',
                          style: GoogleFonts.inter(
                            fontSize: 16 * scale,
                            color: const Color(0xFFDB3022),
                            fontWeight: FontWeight.w800,
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _EmptySearchState extends StatelessWidget {
  final double scale;

  const _EmptySearchState({required this.scale});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: EdgeInsets.symmetric(horizontal: 32 * scale),
        child: Text(
          'No products found',
          textAlign: TextAlign.center,
          style: GoogleFonts.outfit(
            fontSize: 20 * scale,
            fontWeight: FontWeight.w700,
            color: const Color(0xFF222222),
          ),
        ),
      ),
    );
  }
}
