package com.wteam.backend.user_review;

import com.wteam.backend.common.enums.ReviewStatus;
import com.wteam.backend.common.enums.TargetRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, Long> {
    Optional<UserReview> findByBookingIdAndReviewerIdAndTargetUserId(Long bookingId, Long reviewerId, Long targetUserId);
    List<UserReview> findByBookingId(Long bookingId);
    List<UserReview> findByStatusAndCreatedAtBefore(ReviewStatus status, java.time.Instant date);
    List<UserReview> findByTargetUserIdAndTargetRoleAndStatusOrderByCreatedAtAsc(Long targetUserId, TargetRole targetRole, ReviewStatus status);
    List<UserReview> findByTargetUserIdAndStatus(Long targetUserId, ReviewStatus status);
}
