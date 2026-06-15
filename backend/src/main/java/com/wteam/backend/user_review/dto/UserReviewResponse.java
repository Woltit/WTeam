package com.wteam.backend.user_review.dto;

import com.wteam.backend.common.enums.ReviewStatus;
import com.wteam.backend.common.enums.TargetRole;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class UserReviewResponse {
    private Long id;
    private Long targetUserId;
    private Long reviewerId;
    private Long bookingId;
    private TargetRole targetRole;
    private Short rating;
    private String comment;
    private ReviewStatus status;
    private Instant createdAt;
}
