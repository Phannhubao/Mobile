package com.phannhubao.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private Long id;
    private String name;
    private String avatar;
    private String avatarUrl;
    private int rating;
    private String date;
    private String content;
    private boolean helpful;
    private boolean hasPhoto;
    private List<String> photos;
}
