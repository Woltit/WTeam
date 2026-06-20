package com.wteam.backend.user_review.dto;

import com.wteam.backend.common.enums.ReviewStatus;
import com.wteam.backend.common.enums.TargetRole;
import lombok.Builder;
import java.time.Instant;

@Builder
public record UserReviewResponse (
    Long id,
    Long targetUserId,
    Long reviewerId,
    Long bookingId,
    TargetRole targetRole,
    Short rating,
    String comment,
    ReviewStatus status,
    Instant createdAt
) {}
