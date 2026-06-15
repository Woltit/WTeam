package com.wteam.backend.admin;

import com.wteam.backend.admin.dto.AdminStatsResponse;
import com.wteam.backend.booking.BookingRepository;
import com.wteam.backend.common.enums.BookingStatus;
import com.wteam.backend.item.ItemRepository;
import com.wteam.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public AdminStatsResponse getStats() {
        long totalUsers = userRepository.count();
        long activeBookings = bookingRepository.countByStatusIn(
                List.of(BookingStatus.PENDING, BookingStatus.APPROVED, BookingStatus.IN_PROGRESS)
        );
        long completedBookings = bookingRepository.countByStatus(BookingStatus.COMPLETED);
        long totalItems = itemRepository.count();
        var topCategories = itemRepository.findTopCategoriesByItemCount(PageRequest.of(0, 5));

        return new AdminStatsResponse(totalUsers, activeBookings, completedBookings, totalItems, topCategories);
    }
}
