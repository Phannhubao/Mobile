package com.phannhubao.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingSummaryResponse {
    private double averageRating;
    private int totalRatings;
    private Map<Integer, Integer> ratingsCount;
    private boolean hasUserReviewed;
}
