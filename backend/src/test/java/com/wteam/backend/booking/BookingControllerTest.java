package com.wteam.backend.booking;

import com.wteam.backend.booking.dto.BookingResponse;
import com.wteam.backend.common.enums.BookingStatus;
import com.wteam.backend.common.enums.Role;
import com.wteam.backend.exception.booking.ItemNotAvailableException;
import com.wteam.backend.security.SecurityUser;
import com.wteam.backend.security.TestSecurityConfig;
import com.wteam.backend.security.jwt.JwtService;
import com.wteam.backend.security.oauth2.CustomOAuth2UserService;
import com.wteam.backend.security.oauth2.CustomOidcUserService;
import com.wteam.backend.security.oauth2.OAuth2SuccessHandler;
import com.wteam.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@Import(TestSecurityConfig.class)
@DisplayName("BookingController WebMvcTest")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private final MockMvc mockMvc;

    @MockitoBean BookingService bookingService;
    @MockitoBean JwtService jwtService;
    @MockitoBean UserDetailsService userDetailsService;
    @MockitoBean CustomOAuth2UserService customOAuth2UserService;
    @MockitoBean CustomOidcUserService customOidcUserService;
    @MockitoBean OAuth2SuccessHandler oAuth2SuccessHandler;

    private SecurityUser regularUser;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L); user.setEmail("user@test.com");
        user.setRole(Role.USER); user.setActive(true);
        regularUser = SecurityUser.create(user, null);
    }

    private BookingResponse sampleResponse() {
        return new BookingResponse(1L, 10L, 1L,
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5),
                new BigDecimal("400"), new BigDecimal("200"), new BigDecimal("100"),
                BookingStatus.PENDING, "");
    }

    @Test
    @DisplayName("POST /bookings → 201 for authenticated user with valid request")
    void createBooking_whenAuthenticated_returns201() throws Exception {
        when(bookingService.createBooking(any(), any())).thenReturn(sampleResponse());

        String body = """
                {"itemId":10,"startDate":"2026-08-01","endDate":"2026-08-05"}
                """;

        mockMvc.perform(post("/bookings")
                        .with(csrf())
                        .with(user(regularUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /bookings → 401 when request is unauthenticated")
    void createBooking_whenUnauthenticated_returns401() throws Exception {
        String body = """
                {"itemId":10,"startDate":"2026-08-01","endDate":"2026-08-05"}
                """;

        mockMvc.perform(post("/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /bookings → 409 when the item is not available for requested dates")
    void createBooking_whenDatesOverlap_returns409() throws Exception {
        when(bookingService.createBooking(any(), any()))
                .thenThrow(new ItemNotAvailableException(10L,
                        LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5)));

        String body = """
                {"itemId":10,"startDate":"2026-08-01","endDate":"2026-08-05"}
                """;

        mockMvc.perform(post("/bookings")
                        .with(csrf())
                        .with(user(regularUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /bookings/my → 200 returns current user's bookings page")
    void getMyBookings_whenAuthenticated_returns200() throws Exception {
        when(bookingService.findAllByRenterId(eq(1L), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/bookings/my")
                        .with(user(regularUser)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /bookings (admin) → 403 for a non-admin user")
    void getAllBookings_whenNotAdmin_returns403() throws Exception {
        mockMvc.perform(get("/bookings")
                        .with(user(regularUser)))
                .andExpect(status().isForbidden());
    }
}
