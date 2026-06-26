package com.wteam.backend.user_review;

import com.wteam.backend.item_review.ItemReviewService;
import com.wteam.backend.item_review.dto.ItemReviewRequest;
import com.wteam.backend.item_review.dto.ItemReviewResponse;
import com.wteam.backend.security.annotation.CurrentUser;
import com.wteam.backend.security.dto.UserPrincipalDto;
import com.wteam.backend.user_review.dto.UserReviewRequest;
import com.wteam.backend.user_review.dto.UserReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Відгуки", description = "API для залишення та перегляду відгуків на товари та користувачів")
public class ReviewController {
    private final ItemReviewService itemReviewService;
    private final UserReviewService userReviewService;

    @PostMapping("/bookings/{bookingId}/reviews/item")
    @Operation(summary = "Залишити відгук на товар", description = "Залишає відгук на річ після успішного завершення оренди")
    public ResponseEntity<ItemReviewResponse> submitItemReview(
            @PathVariable Long bookingId,
            @Valid @RequestBody ItemReviewRequest request,
            @Parameter(hidden = true) @CurrentUser UserPrincipalDto currentUser
    ) {
        ItemReviewResponse review = itemReviewService.submitItemReview(bookingId, currentUser.id(), request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/items/{itemId}/reviews")
                .buildAndExpand(review.getItemId())
                .toUri();
        return ResponseEntity.created(location).body(review);
    }

    @PostMapping("/bookings/{bookingId}/reviews/user")
    @Operation(summary = "Залишити відгук на користувача", description = "Залишає відгук на орендодавця або орендаря після успішного завершення оренди")
    public ResponseEntity<UserReviewResponse> submitUserReview(
            @PathVariable Long bookingId,
            @Valid @RequestBody UserReviewRequest request,
            @Parameter(hidden = true) @CurrentUser UserPrincipalDto currentUser
    ) {
        UserReviewResponse review = userReviewService.submitUserReview(bookingId, currentUser.id(), request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/{userId}/reviews")
                .buildAndExpand(review.targetUserId())
                .toUri();
        
        return ResponseEntity.created(location).body(review);
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
