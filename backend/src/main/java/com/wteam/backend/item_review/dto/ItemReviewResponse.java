package com.wteam.backend.item_review.dto;

import com.wteam.backend.common.enums.ReviewStatus;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class ItemReviewResponse {
    private Long id;
    private Long itemId;
    private Long reviewerId;
    private Long bookingId;
    private Short rating;
    private String comment;
    private ReviewStatus status;
    private Instant createdAt;
}
