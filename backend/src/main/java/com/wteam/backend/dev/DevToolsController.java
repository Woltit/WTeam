package com.wteam.backend.dev;

import com.wteam.backend.system_listeners.ReviewPublishScheduler;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev-tools")
@Profile("dev")
@RequiredArgsConstructor
@Tag(name = "Dev Tools")
public class DevToolsController {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewPublishScheduler reviewPublishScheduler;

    @PostMapping("/time-travel/bookings/{bookingId}")
    public String timeTravelBooking(@PathVariable Long bookingId) {
        String sql = "UPDATE bookings SET start_date = CURRENT_DATE - 5, end_date = CURRENT_DATE - 1, status = 'COMPLETED' WHERE id = ?";
        int rowsUpdated = jdbcTemplate.update(sql, bookingId);
        if (rowsUpdated > 0) {
            return "Booking " + bookingId + " successfully time-traveled.";
        } else {
            return "Booking " + bookingId + " not found.";
        }
    }

    @PostMapping("/trigger-review-publish")
    public String triggerReviewPublish() {
        reviewPublishScheduler.autoPublishOldReviews();
        return "Review publish triggered manually.";
    }
}
