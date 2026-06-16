package com.wteam.backend.user_review;

import com.wteam.backend.item_review.ItemReviewService;
import com.wteam.backend.item_review.dto.ItemReviewRequest;
import com.wteam.backend.item_review.dto.ItemReviewResponse;
import com.wteam.backend.security.SecurityUser;
import com.wteam.backend.user_review.dto.UserReviewRequest;
import com.wteam.backend.user_review.dto.UserReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Відгуки", description = "API для залишення та перегляду відгуків на товари та користувачів")
public class ReviewController {

    private final ItemReviewService itemReviewService;
    private final UserReviewService userReviewService;

    @PostMapping("/bookings/{bookingId}/reviews/item")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Залишити відгук на товар", description = "Залишає відгук на річ після успішного завершення оренди")
    public ItemReviewResponse submitItemReview(
            @PathVariable Long bookingId,
            @Valid @RequestBody ItemReviewRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUser currentUser
    ) {
        return itemReviewService.submitItemReview(bookingId, currentUser.getId(), request);
    }

    @PostMapping("/bookings/{bookingId}/reviews/user")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Залишити відгук на користувача", description = "Залишає відгук на орендодавця або орендаря після успішного завершення оренди")
    public UserReviewResponse submitUserReview(
            @PathVariable Long bookingId,
            @Valid @RequestBody UserReviewRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUser currentUser
    ) {
        return userReviewService.submitUserReview(bookingId, currentUser.getId(), request);
    }

    @GetMapping("/items/{itemId}/reviews")
    @Operation(summary = "Відгуки на товар", description = "Отримує всі опубліковані відгуки для конкретного товару")
    public List<ItemReviewResponse> getItemReviews(@PathVariable Long itemId) {
        return itemReviewService.getPublishedReviewsForItem(itemId);
    }

    @GetMapping("/users/{userId}/reviews")
    @Operation(summary = "Відгуки на користувача", description = "Отримує всі опубліковані відгуки для конкретного користувача")
    public List<UserReviewResponse> getUserReviews(@PathVariable Long userId) {
        return userReviewService.getPublishedReviewsForUser(userId);
    }
}
