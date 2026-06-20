package com.wteam.backend.item_review;

import com.wteam.backend.common.interfaces.Mapper;
import com.wteam.backend.item_review.dto.ItemReviewRequest;
import com.wteam.backend.item_review.dto.ItemReviewResponse;
import org.springframework.stereotype.Component;

@Component
public class ItemReviewMapper implements Mapper<ItemReviewRequest, ItemReviewResponse, ItemReview> {

    @Override
    public ItemReviewResponse toResponse(ItemReview review) {
        if (review == null) return null;

        return ItemReviewResponse.builder()
                .id(review.getId())
                .itemId(review.getItem().getId())
                .reviewerId(review.getReviewer().getId())
                .bookingId(review.getBooking().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .status(review.getStatus())
                .createdAt(review.getCreatedAt())
                .build();
    }

    @Override
    public ItemReview toEntity(ItemReviewRequest dto) {
        if (dto == null) return null;

        ItemReview review = new ItemReview();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        return review;
    }
}
