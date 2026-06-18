import 'dart:io';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:image_picker/image_picker.dart';
import 'package:flutter/foundation.dart' show kIsWeb;
import '../models/rating_summary.dart';
import '../services/product_service.dart';
import '../utils/constants.dart';

class RatingReviewsScreen extends StatefulWidget {
  final String productName;
  final String productId;

  const RatingReviewsScreen(
      {super.key, required this.productName, required this.productId});

  @override
  State<RatingReviewsScreen> createState() => _RatingReviewsScreenState();
}

class _RatingReviewsScreenState extends State<RatingReviewsScreen> {
  static final Map<String, List<String>> _localReviewPhotos = {};
  bool _withPhotoOnly = false;
  RatingSummary? _ratingSummary;
  bool _isLoadingSummary = true;
  List<Map<String, dynamic>> _serverReviews = [];
  bool _isLoadingReviews = true;

  String _resolveReviewPhotoUrl(String photoUrl) {
    if (photoUrl.isEmpty) return '';
    if (!kIsWeb && File(photoUrl).existsSync()) {
      return photoUrl;
    }
    return AppConstants.resolveUrl(photoUrl);
  }

  @override
  void initState() {
    super.initState();
    _loadSummary();
    _loadReviews();
  }

  Future<void> _loadSummary() async {
    final summary = await ProductService().getRatingSummary(widget.productId);
    if (mounted) {
      setState(() {
        _ratingSummary = summary;
        _isLoadingSummary = false;
      });
    }
  }

  Future<void> _loadReviews() async {
    final reviews = await ProductService().getProductReviews(widget.productId);
    if (mounted) {
      final processedReviews = reviews.map((r) {
        final comment = r['content'] ?? r['comment'] ?? '';
        if (_localReviewPhotos.containsKey(comment)) {
          final localPhotos = _localReviewPhotos[comment]!;
          final mutableReview = Map<String, dynamic>.from(r);
          mutableReview['photos'] = localPhotos;
          mutableReview['hasPhoto'] = true;
          return mutableReview;
        }
        return r;
      }).toList();

      setState(() {
        _serverReviews = processedReviews;
        _isLoadingReviews = false;
      });
    }
  }

  Widget _buildImageWidget(String path, double width, double height) {
    if (kIsWeb ||
        path.startsWith('http://') ||
        path.startsWith('https://') ||
        path.startsWith('blob:')) {
      return Image.network(
        path,
        width: width,
        height: height,
        fit: BoxFit.cover,
        errorBuilder: (context, error, stackTrace) =>
            _buildErrorImage(width, height),
      );
    } else {
      return Image.file(
        File(path),
        width: width,
        height: height,
        fit: BoxFit.cover,
        errorBuilder: (context, error, stackTrace) =>
            _buildErrorImage(width, height),
      );
    }
  }

  Widget _buildErrorImage(double width, double height) {
    return Container(
      width: width,
      height: height,
      color: const Color(0xFFE0E0E0),
      child: const Icon(Icons.image, color: Colors.grey),
    );
  }

  @override
  Widget build(BuildContext context) {
    final scale = (MediaQuery.of(context).size.width / 375).clamp(0.5, 1.5);

    final filteredReviews = _withPhotoOnly
        ? _serverReviews.where((review) => review['hasPhoto'] == true).toList()
        : _serverReviews;

    return Scaffold(
      backgroundColor: const Color(0xFFF9F9F9),
      appBar: AppBar(
        backgroundColor: const Color(0xFFF9F9F9),
        elevation: 0,
        centerTitle: true,
        leading: IconButton(
          icon: Icon(Icons.arrow_back_ios_new,
              color: const Color(0xFF222222), size: 20 * scale),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Rating and reviews',
          style: GoogleFonts.outfit(
            fontSize: 18 * scale,
            fontWeight: FontWeight.w700,
            color: const Color(0xFF222222),
          ),
        ),
      ),
      body: Stack(
        children: [
          SingleChildScrollView(
            physics: const BouncingScrollPhysics(),
            padding: EdgeInsets.symmetric(horizontal: 16 * scale),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                SizedBox(height: 16 * scale),

                // SUMMARY UI
                if (_isLoadingSummary)
                  const Center(
                      child:
                          CircularProgressIndicator(color: Color(0xFFDB3022)))
                else if (_ratingSummary != null)
                  _buildRatingSummary(scale),

                SizedBox(height: 16 * scale),

                // Header section: "X reviews" and "With photo" Checkbox
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      '${filteredReviews.length} reviews',
                      style: GoogleFonts.outfit(
                        fontSize: 24 * scale,
                        fontWeight: FontWeight.w700,
                        color: const Color(0xFF222222),
                      ),
                    ),
                    GestureDetector(
                      onTap: () {
                        setState(() {
                          _withPhotoOnly = !_withPhotoOnly;
                        });
                      },
                      child: Row(
                        children: [
                          Checkbox(
                            value: _withPhotoOnly,
                            onChanged: (val) {
                              setState(() {
                                _withPhotoOnly = val ?? false;
                              });
                            },
                            activeColor: const Color(0xFF222222),
                            checkColor: Colors.white,
                            side: const BorderSide(
                                color: Color(0xFF222222), width: 1.5),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(4),
                            ),
                          ),
                          Text(
                            'With photo',
                            style: GoogleFonts.inter(
                              fontSize: 14 * scale,
                              color: const Color(0xFF222222),
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
                SizedBox(height: 16 * scale),

                if (_isLoadingReviews)
                  const Center(
                    child: CircularProgressIndicator(color: Color(0xFFDB3022)),
                  )
                else if (filteredReviews.isEmpty)
                  Padding(
                    padding: EdgeInsets.symmetric(vertical: 40 * scale),
                    child: Center(
                      child: Text(
                        _withPhotoOnly
                            ? 'No reviews with photos yet'
                            : 'No reviews yet. Be the first to review!',
                        textAlign: TextAlign.center,
                        style: GoogleFonts.inter(
                          fontSize: 14 * scale,
                          color: const Color(0xFF9B9B9B),
                        ),
                      ),
                    ),
                  )
                else
                  ListView.builder(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemCount: filteredReviews.length,
                    itemBuilder: (context, index) {
                      final review = filteredReviews[index];
                      return Padding(
                        padding: EdgeInsets.only(bottom: 24 * scale),
                        child: _buildReviewCard(review, scale),
                      );
                    },
                  ),
                SizedBox(
                    height: 80 * scale), // Space for Floating Action Button
              ],
            ),
          ),

          // Floating "Write a review" Button
          Positioned(
            right: 16 * scale,
            bottom: 24 * scale,
            child: FloatingActionButton.extended(
              onPressed: () {
                if (_ratingSummary?.hasUserReviewed == true) {
                  _showAlreadyReviewedDialog(context, scale);
                  return;
                }
                _showWriteReviewBottomSheet(context, scale);
              },
              backgroundColor: const Color(0xFFDB3022),
              elevation: 6,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(25 * scale),
              ),
              icon: Icon(Icons.edit, color: Colors.white, size: 18 * scale),
              label: Text(
                'Write a review',
                style: GoogleFonts.inter(
                  fontSize: 14 * scale,
                  fontWeight: FontWeight.w600,
                  color: Colors.white,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildRatingSummary(double scale) {
    final summary = _ratingSummary!;
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Left: Big Average Rating
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              summary.averageRating.toStringAsFixed(1),
              style: GoogleFonts.outfit(
                fontSize: 44 * scale,
                fontWeight: FontWeight.w700,
                color: const Color(0xFF222222),
                height: 1.0,
              ),
            ),
            SizedBox(height: 8 * scale),
            Text(
              '${summary.totalRatings} ratings',
              style: GoogleFonts.inter(
                fontSize: 14 * scale,
                color: const Color(0xFF9B9B9B),
              ),
            ),
          ],
        ),
        SizedBox(width: 24 * scale),
        // Right: Progress bars
        Expanded(
          child: Column(
            children: [
              for (int star = 5; star >= 1; star--)
                _buildProgressBar(star, summary.ratingsCount[star] ?? 0,
                    summary.totalRatings, scale),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildProgressBar(int stars, int count, int total, double scale) {
    final double fraction = total > 0 ? count / total : 0.0;
    return Padding(
      padding: EdgeInsets.only(bottom: 6 * scale),
      child: Row(
        children: [
          // Stars visual
          Row(
            children: List.generate(
              5,
              (index) => Icon(
                Icons.star,
                size: 12 * scale,
                color: index < stars
                    ? const Color(0xFFFFBA49)
                    : const Color(0xFFE0E0E0),
              ),
            ),
          ),
          SizedBox(width: 8 * scale),
          // Progress bar
          Expanded(
            child: ClipRRect(
              borderRadius: BorderRadius.circular(4 * scale),
              child: LinearProgressIndicator(
                value: fraction,
                minHeight: 6 * scale,
                backgroundColor: const Color(0xFFE0E0E0),
                valueColor:
                    const AlwaysStoppedAnimation<Color>(Color(0xFFDB3022)),
              ),
            ),
          ),
          SizedBox(width: 12 * scale),
          // Count
          SizedBox(
            width: 24 * scale,
            child: Text(
              '$count',
              textAlign: TextAlign.right,
              style: GoogleFonts.inter(
                fontSize: 12 * scale,
                color: const Color(0xFF222222),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildReviewCard(Map<String, dynamic> review, double scale) {
    return Stack(
      clipBehavior: Clip.none,
      children: [
        // Main Container Card
        Container(
          margin: EdgeInsets.only(left: 20 * scale, top: 10 * scale),
          padding: EdgeInsets.fromLTRB(
              24 * scale, 24 * scale, 24 * scale, 16 * scale),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(8 * scale),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.04),
                blurRadius: 8 * scale,
                offset: Offset(0, 4 * scale),
              ),
            ],
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Reviewer Name
              Text(
                review['name'],
                style: GoogleFonts.inter(
                  fontSize: 14 * scale,
                  fontWeight: FontWeight.w700,
                  color: const Color(0xFF222222),
                ),
              ),
              SizedBox(height: 4 * scale),

              // Rating stars and Date Row
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Row(
                    children: List.generate(
                      5,
                      (i) => Icon(
                        Icons.star,
                        size: 14 * scale,
                        color: i < review['rating']
                            ? const Color(0xFFFFBA49)
                            : const Color(0xFFE0E0E0),
                      ),
                    ),
                  ),
                  Text(
                    review['date'],
                    style: GoogleFonts.inter(
                      fontSize: 11 * scale,
                      color: const Color(0xFF9B9B9B),
                    ),
                  ),
                ],
              ),
              SizedBox(height: 12 * scale),

              // Review Content Text
              Text(
                review['content'],
                style: GoogleFonts.inter(
                  fontSize: 14 * scale,
                  color: const Color(0xFF222222),
                  height: 1.4,
                ),
              ),
              SizedBox(height: 16 * scale),

              // Horizontal Scrollable Photos List (if any)
              if (review['hasPhoto'] == true &&
                  review['photos'] != null &&
                  (review['photos'] as List).isNotEmpty) ...[
                SizedBox(
                  height: 104 * scale,
                  child: ListView.builder(
                    scrollDirection: Axis.horizontal,
                    physics: const BouncingScrollPhysics(),
                    itemCount: (review['photos'] as List).length,
                    itemBuilder: (context, idx) {
                      final photoUrl = review['photos'][idx];
                      final resolvedUrl = _resolveReviewPhotoUrl(photoUrl);
                      return Padding(
                        padding: EdgeInsets.only(right: 12 * scale),
                        child: ClipRRect(
                          borderRadius: BorderRadius.circular(8 * scale),
                          child: _buildImageWidget(
                              resolvedUrl, 104 * scale, 104 * scale),
                        ),
                      );
                    },
                  ),
                ),
                SizedBox(height: 16 * scale),
              ],

              // Helpful feedback row
              Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  GestureDetector(
                    onTap: () {
                      setState(() {
                        review['helpful'] = !review['helpful'];
                      });
                    },
                    child: Row(
                      children: [
                        Text(
                          'Helpful',
                          style: GoogleFonts.inter(
                            fontSize: 11 * scale,
                            color: review['helpful']
                                ? const Color(0xFFDB3022)
                                : const Color(0xFF9B9B9B),
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                        SizedBox(width: 4 * scale),
                        Icon(
                          Icons.thumb_up,
                          size: 13 * scale,
                          color: review['helpful']
                              ? const Color(0xFFDB3022)
                              : const Color(0xFF9B9B9B),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),

        // User Avatar floating on left border
        Positioned(
          left: 0,
          top: 0,
          child: Container(
            width: 40 * scale,
            height: 40 * scale,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              border: Border.all(color: Colors.white, width: 2),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.06),
                  blurRadius: 4 * scale,
                  offset: Offset(0, 2 * scale),
                ),
              ],
            ),
            child: ClipRRect(
              borderRadius: BorderRadius.circular(20 * scale),
              child: review['avatarUrl'] != null &&
                      (review['avatarUrl'] as String).startsWith('http')
                  ? Image.network(
                      review['avatarUrl'],
                      width: 40 * scale,
                      height: 40 * scale,
                      fit: BoxFit.cover,
                      errorBuilder: (context, error, stackTrace) =>
                          _buildInitialsAvatar(review, scale),
                    )
                  : _buildInitialsAvatar(review, scale),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildInitialsAvatar(Map<String, dynamic> review, double scale) {
    return Container(
      color: const Color(0xFFDB3022).withOpacity(0.15),
      child: Center(
        child: Text(
          review['avatar'] ?? 'U',
          style: GoogleFonts.inter(
            fontSize: 14 * scale,
            fontWeight: FontWeight.w700,
            color: const Color(0xFFDB3022),
          ),
        ),
      ),
    );
  }

  void _showAlreadyReviewedDialog(BuildContext context, double scale) {
    showDialog(
      context: context,
      builder: (BuildContext dialogContext) {
        return Dialog(
          alignment: Alignment.topCenter,
          insetPadding: EdgeInsets.only(
            top: 80 * scale,
            left: 16 * scale,
            right: 16 * scale,
          ),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16 * scale),
          ),
          child: Padding(
            padding: EdgeInsets.all(20 * scale),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Icon(
                  Icons.info_outline,
                  color: const Color(0xFFDB3022),
                  size: 48 * scale,
                ),
                SizedBox(height: 16 * scale),
                Text(
                  'Thông báo',
                  style: GoogleFonts.outfit(
                    fontSize: 18 * scale,
                    fontWeight: FontWeight.w700,
                    color: const Color(0xFF222222),
                  ),
                ),
                SizedBox(height: 8 * scale),
                Text(
                  'Bạn đã đánh giá sản phẩm này rồi.',
                  textAlign: TextAlign.center,
                  style: GoogleFonts.inter(
                    fontSize: 14 * scale,
                    color: const Color(0xFF9B9B9B),
                  ),
                ),
                SizedBox(height: 24 * scale),
                SizedBox(
                  width: double.infinity,
                  height: 44 * scale,
                  child: ElevatedButton(
                    onPressed: () {
                      Navigator.pop(dialogContext); // Close dialog
                      Navigator.pop(context); // Go back to product detail screen
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFFDB3022),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(22 * scale),
                      ),
                      elevation: 0,
                    ),
                    child: Text(
                      'QUAY LẠI SẢN PHẨM',
                      style: GoogleFonts.inter(
                        fontSize: 13 * scale,
                        fontWeight: FontWeight.w600,
                        color: Colors.white,
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  void _showReviewPreviewBottomSheet(
    BuildContext context,
    double scale,
    int rating,
    String content,
    List<String> photos,
  ) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (ctx) {
        return Container(
          constraints: BoxConstraints(
            maxHeight: MediaQuery.of(ctx).size.height * 0.85,
          ),
          decoration: const BoxDecoration(
            color: Color(0xFFF9F9F9),
            borderRadius: BorderRadius.only(
              topLeft: Radius.circular(30),
              topRight: Radius.circular(30),
            ),
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // Drag handle
              Padding(
                padding: EdgeInsets.only(top: 12 * scale, bottom: 4 * scale),
                child: Container(
                  width: 60 * scale,
                  height: 6 * scale,
                  decoration: BoxDecoration(
                    color: const Color(0xFFC4C4C4),
                    borderRadius: BorderRadius.circular(3 * scale),
                  ),
                ),
              ),

              // Header row
              Padding(
                padding: EdgeInsets.symmetric(
                    horizontal: 4 * scale, vertical: 8 * scale),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    IconButton(
                      icon: Icon(Icons.arrow_back_ios_new,
                          color: const Color(0xFF222222), size: 20 * scale),
                      onPressed: () => Navigator.pop(ctx),
                    ),
                    Text(
                      'Your review',
                      style: GoogleFonts.outfit(
                        fontSize: 18 * scale,
                        fontWeight: FontWeight.w700,
                        color: const Color(0xFF222222),
                      ),
                    ),
                    SizedBox(width: 48 * scale),
                  ],
                ),
              ),

              // Scrollable content
              Flexible(
                child: SingleChildScrollView(
                  padding: EdgeInsets.fromLTRB(
                      20 * scale, 8 * scale, 20 * scale, 24 * scale),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      // Review card preview
                      Container(
                        width: double.infinity,
                        padding: EdgeInsets.fromLTRB(
                            24 * scale, 24 * scale, 24 * scale, 20 * scale),
                        decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(8 * scale),
                          boxShadow: [
                            BoxShadow(
                              color: Colors.black.withOpacity(0.06),
                              blurRadius: 12 * scale,
                              offset: Offset(0, 4 * scale),
                            ),
                          ],
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            // Avatar + Name + Date
                            Row(
                              children: [
                                Container(
                                  width: 40 * scale,
                                  height: 40 * scale,
                                  decoration: BoxDecoration(
                                    shape: BoxShape.circle,
                                    boxShadow: [
                                      BoxShadow(
                                        color: Colors.black.withOpacity(0.1),
                                        blurRadius: 4 * scale,
                                        offset: Offset(0, 2 * scale),
                                      ),
                                    ],
                                  ),
                                  child: ClipRRect(
                                    borderRadius:
                                        BorderRadius.circular(20 * scale),
                                    child: Image.network(
                                      'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150',
                                      fit: BoxFit.cover,
                                      errorBuilder: (_, __, ___) => Container(
                                        color: const Color(0xFFDB3022)
                                            .withOpacity(0.15),
                                        child: Center(
                                          child: Text(
                                            'U',
                                            style: GoogleFonts.inter(
                                              fontSize: 14 * scale,
                                              fontWeight: FontWeight.w700,
                                              color: const Color(0xFFDB3022),
                                            ),
                                          ),
                                        ),
                                      ),
                                    ),
                                  ),
                                ),
                                SizedBox(width: 12 * scale),
                                Expanded(
                                  child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        'You',
                                        style: GoogleFonts.inter(
                                          fontSize: 14 * scale,
                                          fontWeight: FontWeight.w700,
                                          color: const Color(0xFF222222),
                                        ),
                                      ),
                                      Text(
                                        'Today',
                                        style: GoogleFonts.inter(
                                          fontSize: 11 * scale,
                                          color: const Color(0xFF9B9B9B),
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                                // Star rating
                                Row(
                                  children: List.generate(
                                    5,
                                    (i) => Icon(
                                      Icons.star,
                                      size: 14 * scale,
                                      color: i < rating
                                          ? const Color(0xFFFFBA49)
                                          : const Color(0xFFE0E0E0),
                                    ),
                                  ),
                                ),
                              ],
                            ),
                            SizedBox(height: 16 * scale),

                            // Review text
                            Text(
                              content,
                              style: GoogleFonts.inter(
                                fontSize: 13 * scale,
                                color: const Color(0xFF222222),
                                height: 1.5,
                              ),
                            ),

                            // Photos (if any)
                            if (photos.isNotEmpty) ...[
                              SizedBox(height: 16 * scale),
                              SizedBox(
                                height: 104 * scale,
                                child: ListView.builder(
                                  scrollDirection: Axis.horizontal,
                                  itemCount: photos.length,
                                  itemBuilder: (_, idx) {
                                    final photoUrl = photos[idx];
                                    return Padding(
                                      padding: EdgeInsets.only(
                                          right: 12 * scale),
                                      child: ClipRRect(
                                        borderRadius: BorderRadius.circular(
                                            8 * scale),
                                        child:
                                            _buildImageWidget(
                                                photoUrl,
                                                104 * scale,
                                                104 * scale),
                                      ),
                                    );
                                  },
                                ),
                              ),
                            ],

                            SizedBox(height: 16 * scale),

                            // Helpful row (preview only)
                            Row(
                              children: [
                                Text(
                                  'Helpful',
                                  style: GoogleFonts.inter(
                                    fontSize: 11 * scale,
                                    color: const Color(0xFF9B9B9B),
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                                SizedBox(width: 4 * scale),
                                Icon(
                                  Icons.thumb_up_outlined,
                                  size: 13 * scale,
                                  color: const Color(0xFF9B9B9B),
                                ),
                              ],
                            ),
                          ],
                        ),
                      ),

                      SizedBox(height: 32 * scale),

                      // Info label
                      Text(
                        'Your review will be published after moderation.',
                        textAlign: TextAlign.center,
                        style: GoogleFonts.inter(
                          fontSize: 12 * scale,
                          color: const Color(0xFF9B9B9B),
                        ),
                      ),
                      SizedBox(height: 24 * scale),

                      // Buttons
                      Row(
                        children: [
                          // Edit button
                          Expanded(
                            child: SizedBox(
                              height: 48 * scale,
                              child: OutlinedButton(
                                onPressed: () => Navigator.pop(ctx),
                                style: OutlinedButton.styleFrom(
                                  side: const BorderSide(
                                      color: Color(0xFF222222), width: 1.5),
                                  shape: RoundedRectangleBorder(
                                    borderRadius:
                                        BorderRadius.circular(24 * scale),
                                  ),
                                ),
                                child: Text(
                                  'Edit',
                                  style: GoogleFonts.inter(
                                    fontSize: 14 * scale,
                                    fontWeight: FontWeight.w600,
                                    color: const Color(0xFF222222),
                                  ),
                                ),
                              ),
                            ),
                          ),
                          SizedBox(width: 16 * scale),

                          // Confirm & Send button
                          Expanded(
                            child: SizedBox(
                              height: 48 * scale,
                              child: ElevatedButton(
                                onPressed: () async {
                                  // Show loading indicator
                                  showDialog(
                                    context: ctx,
                                    barrierDismissible: false,
                                    builder: (loadingCtx) => const Center(
                                      child: CircularProgressIndicator(
                                        color: Color(0xFFDB3022),
                                      ),
                                    ),
                                  );

                                  try {
                                    final errorMsg = await ProductService().createReview(
                                      widget.productId,
                                      rating,
                                      content,
                                    );

                                    // Pop loading indicator
                                    Navigator.pop(ctx);

                                    if (errorMsg == null) {
                                      if (photos.isNotEmpty) {
                                        _localReviewPhotos[content] = List.from(photos);
                                      }
                                      // Reload from server
                                      _loadSummary();
                                      _loadReviews();

                                      // Close preview and write review bottom sheets
                                      Navigator.pop(ctx); // Close preview
                                      Navigator.pop(context); // Close write review

                                      ScaffoldMessenger.of(context).showSnackBar(
                                        SnackBar(
                                          content: Text(
                                            'Cảm ơn bạn đã gửi đánh giá!',
                                            style: GoogleFonts.inter(
                                                fontWeight: FontWeight.w500),
                                          ),
                                          backgroundColor: const Color(0xFF2AA95C),
                                          behavior: SnackBarBehavior.floating,
                                        ),
                                      );
                                    } else {
                                      if (errorMsg.contains('đã đánh giá') || errorMsg.contains('already reviewed')) {
                                        Navigator.pop(ctx); // Close preview bottom sheet
                                        Navigator.pop(context); // Close write review bottom sheet
                                        _showAlreadyReviewedDialog(context, scale);
                                      } else {
                                        ScaffoldMessenger.of(ctx).showSnackBar(
                                          SnackBar(
                                            content: Text(errorMsg),
                                            backgroundColor: const Color(0xFFDB3022),
                                          ),
                                        );
                                      }
                                    }
                                  } catch (e) {
                                    Navigator.pop(ctx); // Pop loading indicator if crash
                                    ScaffoldMessenger.of(ctx).showSnackBar(
                                      SnackBar(
                                        content: Text('Đã xảy ra lỗi: $e'),
                                        backgroundColor: const Color(0xFFDB3022),
                                      ),
                                    );
                                  }
                                },
                                style: ElevatedButton.styleFrom(
                                  backgroundColor: const Color(0xFFDB3022),
                                  elevation: 4,
                                  shadowColor: const Color(0xFFDB3022)
                                      .withOpacity(0.4),
                                  shape: RoundedRectangleBorder(
                                    borderRadius:
                                        BorderRadius.circular(24 * scale),
                                  ),
                                ),
                                child: Text(
                                  'Send',
                                  style: GoogleFonts.inter(
                                    fontSize: 14 * scale,
                                    fontWeight: FontWeight.w700,
                                    color: Colors.white,
                                    letterSpacing: 1.0,
                                  ),
                                ),
                              ),
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  void _showWriteReviewBottomSheet(BuildContext context, double scale) {

    int currentRating = 5;
    final textController = TextEditingController();
    final List<String> pickedPhotos = [];

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setBottomSheetState) {
            return Container(
              decoration: const BoxDecoration(
                color: Color(0xFFF9F9F9),
                borderRadius: BorderRadius.only(
                  topLeft: Radius.circular(30),
                  topRight: Radius.circular(30),
                ),
              ),
              child: AnimatedPadding(
                duration: const Duration(milliseconds: 150),
                curve: Curves.easeOut,
                padding: EdgeInsets.only(
                  left: 16 * scale,
                  right: 16 * scale,
                  top: 8 * scale,
                  bottom: MediaQuery.of(context).viewInsets.bottom + 24 * scale,
                ),
                child: SingleChildScrollView(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      // Centered grey drag handle
                      Center(
                        child: Container(
                          width: 60 * scale,
                          height: 6 * scale,
                          decoration: BoxDecoration(
                            color: const Color(0xFFC4C4C4),
                            borderRadius: BorderRadius.circular(3 * scale),
                          ),
                        ),
                      ),
                      SizedBox(height: 24 * scale),

                      // Title
                      Center(
                        child: Text(
                          'What is you rate?',
                          style: GoogleFonts.outfit(
                            fontSize: 20 * scale,
                            fontWeight: FontWeight.w700,
                            color: const Color(0xFF222222),
                          ),
                        ),
                      ),
                      SizedBox(height: 16 * scale),

                      // Stars selector
                      Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: List.generate(
                          5,
                          (i) => GestureDetector(
                            onTap: () {
                              setBottomSheetState(() {
                                currentRating = i + 1;
                              });
                            },
                            child: Padding(
                              padding:
                                  EdgeInsets.symmetric(horizontal: 6 * scale),
                              child: Icon(
                                i < currentRating
                                    ? Icons.star
                                    : Icons.star_border,
                                size: 36 * scale,
                                color: i < currentRating
                                    ? const Color(0xFFFFBA49)
                                    : const Color(0xFF9B9B9B),
                              ),
                            ),
                          ),
                        ),
                      ),
                      SizedBox(height: 24 * scale),

                      // Description
                      Center(
                        child: Text(
                          'Please share your opinion\nabout the product',
                          textAlign: TextAlign.center,
                          style: GoogleFonts.inter(
                            fontSize: 18 * scale,
                            fontWeight: FontWeight.w700,
                            color: const Color(0xFF222222),
                            height: 1.2,
                          ),
                        ),
                      ),
                      SizedBox(height: 16 * scale),

                      // Text Field Container
                      Container(
                        decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(4 * scale),
                          boxShadow: [
                            BoxShadow(
                              color: Colors.black.withOpacity(0.04),
                              blurRadius: 8 * scale,
                              offset: Offset(0, 4 * scale),
                            ),
                          ],
                        ),
                        padding: EdgeInsets.symmetric(
                            horizontal: 16 * scale, vertical: 8 * scale),
                        child: TextField(
                          controller: textController,
                          maxLines: 5,
                          decoration: InputDecoration(
                            hintText: 'Your review',
                            hintStyle: GoogleFonts.inter(
                              color: const Color(0xFF9B9B9B),
                              fontSize: 14 * scale,
                            ),
                            border: InputBorder.none,
                          ),
                          style: GoogleFonts.inter(
                            fontSize: 14 * scale,
                            color: const Color(0xFF222222),
                          ),
                        ),
                      ),
                      SizedBox(height: 24 * scale),

                      // Image picker row: Picked photos on left, Add your photos on right
                      SingleChildScrollView(
                        scrollDirection: Axis.horizontal,
                        physics: const BouncingScrollPhysics(),
                        child: Row(
                          children: [
                            // 1. Render picked photos first (left)
                            ...pickedPhotos.asMap().entries.map((entry) {
                              final idx = entry.key;
                              final photoUrl = entry.value;
                              return Padding(
                                padding: EdgeInsets.only(right: 12 * scale),
                                child: Stack(
                                  clipBehavior: Clip.none,
                                  children: [
                                    ClipRRect(
                                      borderRadius:
                                          BorderRadius.circular(8 * scale),
                                      child: _buildImageWidget(
                                          photoUrl, 104 * scale, 104 * scale),
                                    ),
                                    Positioned(
                                      right: -4 * scale,
                                      top: -4 * scale,
                                      child: GestureDetector(
                                        onTap: () {
                                          setBottomSheetState(() {
                                            pickedPhotos.removeAt(idx);
                                          });
                                        },
                                        child: Container(
                                          decoration: const BoxDecoration(
                                            color: Color(0xFFDB3022),
                                            shape: BoxShape.circle,
                                          ),
                                          padding: EdgeInsets.all(2 * scale),
                                          child: Icon(
                                            Icons.close,
                                            color: Colors.white,
                                            size: 14 * scale,
                                          ),
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              );
                            }),

                            // 2. Render "Add your photos" button at the end (right)
                            GestureDetector(
                              onTap: () async {
                                try {
                                  final ImagePicker picker = ImagePicker();
                                  final XFile? image = await picker.pickImage(
                                    source: ImageSource.gallery,
                                    imageQuality: 85,
                                  );
                                  if (image != null) {
                                    setBottomSheetState(() {
                                      pickedPhotos.add(image.path);
                                    });
                                  }
                                } catch (e) {
                                  if (!context.mounted) return;
                                  ScaffoldMessenger.of(context).showSnackBar(
                                    SnackBar(
                                      content: Text('Lỗi chọn ảnh: $e'),
                                      backgroundColor: Colors.red,
                                    ),
                                  );
                                }
                              },
                              child: Container(
                                width: 104 * scale,
                                height: 104 * scale,
                                decoration: BoxDecoration(
                                  color: Colors.white,
                                  borderRadius:
                                      BorderRadius.circular(8 * scale),
                                  boxShadow: [
                                    BoxShadow(
                                      color: Colors.black.withOpacity(0.04),
                                      blurRadius: 4 * scale,
                                      offset: Offset(0, 2 * scale),
                                    ),
                                  ],
                                ),
                                child: Column(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Container(
                                      width: 44 * scale,
                                      height: 44 * scale,
                                      decoration: const BoxDecoration(
                                        color: Color(0xFFDB3022),
                                        shape: BoxShape.circle,
                                      ),
                                      child: Icon(
                                        Icons.camera_alt,
                                        color: Colors.white,
                                        size: 22 * scale,
                                      ),
                                    ),
                                    SizedBox(height: 8 * scale),
                                    Text(
                                      'Add your photos',
                                      style: GoogleFonts.inter(
                                        fontSize: 11 * scale,
                                        fontWeight: FontWeight.w600,
                                        color: const Color(0xFF222222),
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                      SizedBox(height: 32 * scale),

                      // SEND REVIEW button
                      SizedBox(
                        width: double.infinity,
                        height: 48 * scale,
                        child: ElevatedButton(
                          onPressed: () {
                            if (textController.text.trim().isEmpty) {
                              ScaffoldMessenger.of(context).showSnackBar(
                                const SnackBar(
                                  content:
                                      Text('Vui lòng nhập nhận xét của bạn!'),
                                  backgroundColor: Colors.red,
                                ),
                              );
                              return;
                            }
                            // Show preview before submitting
                            _showReviewPreviewBottomSheet(
                              context,
                              scale,
                              currentRating,
                              textController.text.trim(),
                              List<String>.from(pickedPhotos),
                            );
                          },
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color(0xFFDB3022),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(24 * scale),
                            ),
                            elevation: 4,
                            shadowColor:
                                const Color(0xFFDB3022).withOpacity(0.4),
                          ),
                          child: Text(
                            'SEND REVIEW',
                            style: GoogleFonts.inter(
                              color: Colors.white,
                              fontWeight: FontWeight.w600,
                              fontSize: 14 * scale,
                              letterSpacing: 1.2,
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            );
          },
        );
      },
    );
  }
}
