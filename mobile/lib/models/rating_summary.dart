class RatingSummary {
  final double averageRating;
  final int totalRatings;
  final Map<int, int> ratingsCount;
  final bool hasUserReviewed;

  RatingSummary({
    required this.averageRating,
    required this.totalRatings,
    required this.ratingsCount,
    this.hasUserReviewed = false,
  });

  factory RatingSummary.fromJson(Map<String, dynamic> json) {
    Map<int, int> parsedCounts = {};
    if (json['ratingsCount'] != null) {
      json['ratingsCount'].forEach((key, value) {
        parsedCounts[int.parse(key.toString())] = value as int;
      });
    } else {
      for (int i = 1; i <= 5; i++) {
        parsedCounts[i] = 0;
      }
    }

    return RatingSummary(
      averageRating: (json['averageRating'] ?? 0.0).toDouble(),
      totalRatings: json['totalRatings'] ?? 0,
      ratingsCount: parsedCounts,
      hasUserReviewed: json['hasUserReviewed'] ?? false,
    );
  }
}
