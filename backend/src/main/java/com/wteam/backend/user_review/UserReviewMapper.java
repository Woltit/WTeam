package com.wteam.backend.user_review;

import com.wteam.backend.common.interfaces.Mapper;
import com.wteam.backend.user_review.dto.UserReviewResponse;
import io.jsonwebtoken.lang.Assert;
import org.springframework.stereotype.Component;

@Component
public final class UserReviewMapper implements Mapper<Void, UserReviewResponse, UserReview> {

    @Override
    public UserReviewResponse toResponse(UserReview review) {
        Assert.notNull(review, "Review cannot be null");

        return UserReviewResponse.builder()
                .id(review.getId())
                .targetUserId(review.getTargetUser().getId())
                .reviewerId(review.getReviewer().getId())
                .bookingId(review.getBooking().getId())
                .targetRole(review.getTargetRole())
                .rating(review.getRating())
                .comment(review.getComment())
                .status(review.getStatus())
                .createdAt(review.getCreatedAt())
                .build();
    }

    @Override
    public UserReview toEntity(Void dto) {
        return null;
    }
}
