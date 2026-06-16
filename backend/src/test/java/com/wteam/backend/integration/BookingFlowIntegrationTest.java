package com.wteam.backend.integration;

import com.wteam.backend.TestcontainersConfiguration;
import com.wteam.backend.auth.dto.AuthResponse;
import com.wteam.backend.auth.dto.LoginRequest;
import com.wteam.backend.auth.dto.RegisterRequest;
import com.wteam.backend.booking.dto.BookingResponse;
import com.wteam.backend.category.Category;
import com.wteam.backend.category.CategoryRepository;
import com.wteam.backend.chat_room.dto.ChatRoomResponse;
import com.wteam.backend.common.enums.BookingStatus;
import com.wteam.backend.common.enums.Role;
import com.wteam.backend.common.enums.VerificationStatus;
import com.wteam.backend.item.dto.ItemResponse;
import com.wteam.backend.message.dto.MessageResponse;
import com.wteam.backend.user.UserRepository;
import com.wteam.backend.user_profile.UserProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@DisplayName("Booking Flow Integration Tests")
class BookingFlowIntegrationTest {

    private static final String API = "";

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired UserProfileRepository userProfileRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired CategoryRepository categoryRepository;

    private String register(String email, String password) throws Exception {
        RegisterRequest req = new RegisterRequest(email, password, password);
        MvcResult res = mockMvc.perform(post(API + "/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readValue(res.getResponse().getContentAsString(), AuthResponse.class).accessToken();
    }

    private String login(String email, String password) throws Exception {
        LoginRequest req = new LoginRequest(email, password);
        MvcResult res = mockMvc.perform(post(API + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readValue(res.getResponse().getContentAsString(), AuthResponse.class).accessToken();
    }

    private Long createItem(String ownerToken, Long categoryId) throws Exception {
        String body = """
                {
                  "categoryId":%d,"title":"Integration Test Drill","description":"A drill",
                  "tags":["tool"],"condition":"IDEAL","pricePerDay":100,"depositAmount":50,
                  "city":"Kyiv","address":"1 Main St","latitude":50.45,"longitude":30.52
                }
                """.formatted(categoryId);
        MvcResult res = mockMvc.perform(post(API + "/items")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readValue(res.getResponse().getContentAsString(), ItemResponse.class).id();
    }

    private void verifyItem(Long itemId) {
        // Directly set verified flag via repository for test setup
        var item = categoryRepository.findAll(); // just to ensure flush
    }

    private Category ensureCategory() {
        return categoryRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> categoryRepository.save(
                        Category.builder()
                                .name("Integration Test Cat " + System.nanoTime())
                                .slug("int-cat-" + System.nanoTime())
                                .build()
                ));
    }

    private void makeOwnerAdmin(String email) {
        userRepository.findByEmail(email).ifPresent(u -> {
            u.setRole(Role.ADMIN);
            userRepository.save(u);
        });
    }

    private void makeProfileVerified(String email) {
        userRepository.findByEmail(email).flatMap(u ->
                userProfileRepository.findByUserId(u.getId())
        ).ifPresent(profile -> {
            profile.setPhoneNumber("+380991234567");
            profile.setBirthDate(LocalDate.of(1990, 1, 1));
            profile.setVerificationStatus(VerificationStatus.VERIFIED);
            userProfileRepository.save(profile);
        });
    }

    @Test
    @DisplayName("Full booking flow: register → login → create item → book → approve → chat → message")
    void fullBookingFlow_shouldCompleteAllStepsSuccessfully() throws Exception {
        long ts = System.nanoTime();
        String ownerEmail  = "owner-" + ts + "@flow.com";
        String renterEmail = "renter-" + ts + "@flow.com";
        String password    = "password123";

        // 1. Register & login both users
        String ownerToken  = register(ownerEmail, password);
        String renterToken = register(renterEmail, password);
        makeOwnerAdmin(ownerEmail);
        ownerToken = login(ownerEmail, password); // re-login after role change

        // 2. Owner creates item (requires verified profile + admin role for verification)
        makeProfileVerified(ownerEmail);
        Category cat = ensureCategory();
        Long itemId = createItem(ownerToken, cat.getId());
        assertNotNull(itemId);

        // 3. Admin verifies item so it's visible
        mockMvc.perform(patch(API + "/items/" + itemId + "/verification")
                        .param("verified", "true")
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());

        // 4. Renter creates booking
        String bookingBody = """
                {"itemId":%d,"startDate":"2026-10-01","endDate":"2026-10-05"}
                """.formatted(itemId);
        MvcResult bookingResult = mockMvc.perform(post(API + "/bookings")
                        .header("Authorization", "Bearer " + renterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingBody))
                .andExpect(status().isCreated())
                .andReturn();
        BookingResponse booking = objectMapper.readValue(
                bookingResult.getResponse().getContentAsString(), BookingResponse.class);
        assertEquals(BookingStatus.PENDING, booking.status());

        // 5. Owner approves booking via admin endpoint
        String statusBody = """
                {"status":"APPROVED"}
                """;
        mockMvc.perform(patch(API + "/bookings/" + booking.id() + "/status")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody))
                .andExpect(status().isOk());

        // 6. Renter opens chat room
        MvcResult chatResult = mockMvc.perform(post(API + "/chat-rooms/booking/" + booking.id())
                        .header("Authorization", "Bearer " + renterToken))
                .andExpect(status().isOk())
                .andReturn();
        ChatRoomResponse chatRoom = objectMapper.readValue(
                chatResult.getResponse().getContentAsString(), ChatRoomResponse.class);
        assertNotNull(chatRoom.id());

        // 7. Renter sends message
        String msgBody = """
                {"messageText":"Hello, is the drill available?"}
                """;
        MvcResult msgResult = mockMvc.perform(post(API + "/chat-rooms/" + chatRoom.id() + "/messages")
                        .header("Authorization", "Bearer " + renterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(msgBody))
                .andExpect(status().isCreated())
                .andReturn();
        MessageResponse message = objectMapper.readValue(
                msgResult.getResponse().getContentAsString(), MessageResponse.class);
        assertEquals("Hello, is the drill available?", message.messageText());
    }

    @Test
    @DisplayName("Concurrent double booking: only one request should succeed, the other gets 409")
    void concurrentDoubleBooking_onlyOneSucceeds() throws Exception {
        long ts = System.nanoTime();
        String ownerEmail   = "owner-conc-" + ts + "@flow.com";
        String renter1Email = "renter1-conc-" + ts + "@flow.com";
        String renter2Email = "renter2-conc-" + ts + "@flow.com";
        String password     = "password123";

        String ownerToken   = register(ownerEmail, password);
        String renter1Token = register(renter1Email, password);
        String renter2Token = register(renter2Email, password);
        makeOwnerAdmin(ownerEmail);
        ownerToken = login(ownerEmail, password);

        makeProfileVerified(ownerEmail);
        Category cat = ensureCategory();
        Long itemId = createItem(ownerToken, cat.getId());
        mockMvc.perform(patch(API + "/items/" + itemId + "/verification")
                .param("verified", "true")
                .header("Authorization", "Bearer " + ownerToken));

        String bookingBody = """
                {"itemId":%d,"startDate":"2026-11-10","endDate":"2026-11-15"}
                """.formatted(itemId);

        final String token1 = renter1Token;
        final String token2 = renter2Token;

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<Integer> task1 = () -> {
            MockHttpServletResponse res = mockMvc.perform(post(API + "/bookings")
                            .header("Authorization", "Bearer " + token1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bookingBody))
                    .andReturn().getResponse();
            return res.getStatus();
        };
        Callable<Integer> task2 = () -> {
            MockHttpServletResponse res = mockMvc.perform(post(API + "/bookings")
                            .header("Authorization", "Bearer " + token2)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bookingBody))
                    .andReturn().getResponse();
            return res.getStatus();
        };

        Future<Integer> f1 = executor.submit(task1);
        Future<Integer> f2 = executor.submit(task2);
        executor.shutdown();

        List<Integer> statuses = new ArrayList<>();
        statuses.add(f1.get());
        statuses.add(f2.get());

        long created  = statuses.stream().filter(s -> s == 201).count();
        long conflict = statuses.stream().filter(s -> s == 409).count();

        assertEquals(1, created,  "Exactly one booking should succeed (201)");
        assertEquals(1, conflict, "Exactly one booking should fail with conflict (409)");
    }
}
