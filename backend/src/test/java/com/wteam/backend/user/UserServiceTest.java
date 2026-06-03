package com.wteam.backend.user;

import com.wteam.backend.common.enums.Role;
import com.wteam.backend.exception.user.UserNotFoundException;
import com.wteam.backend.user.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("User Retrieval Tests")
    class RetrievalTests {

        @Test
        @DisplayName("getAllUsers should return page of user responses")
        void getAllUsers_shouldReturnPageOfUserResponses() {
            User user = mock(User.class);
            UserResponse userResponse = mock(UserResponse.class);
            Page<User> usersPage = new PageImpl<>(List.of(user));
            Pageable pageable = Pageable.unpaged();

            when(userRepository.findAllWithProfile(true, Role.USER, pageable)).thenReturn(usersPage);
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            Page<UserResponse> result = userService.getAllUsers(true, Role.USER, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(userResponse, result.getContent().getFirst());

            verify(userRepository).findAllWithProfile(true, Role.USER, pageable);
            verify(userMapper).toResponse(user);
        }

        @Test
        @DisplayName("getUserById when user exists should return response")
        void getUserById_whenUserExists_shouldReturnResponse() {
            Long userId = 1L;
            User user = mock(User.class);
            UserResponse userResponse = mock(UserResponse.class);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            UserResponse result = userService.getUserById(userId);

            assertNotNull(result);
            assertEquals(userResponse, result);
        }

        @Test
        @DisplayName("getUserById when user does not exist should throw exception")
        void getUserById_whenUserDoesNotExist_shouldThrowException() {
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        }

        @Test
        @DisplayName("getUserByEmail when user exists should return response")
        void getUserByEmail_whenUserExists_shouldReturnResponse() {
            String email = "test@example.com";
            User user = mock(User.class);
            UserResponse userResponse = mock(UserResponse.class);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            UserResponse result = userService.getUserByEmail(email);

            assertNotNull(result);
            assertEquals(userResponse, result);
        }

        @Test
        @DisplayName("getUserByEmail when email is empty should throw exception")
        void getUserByEmail_whenEmailIsEmpty_shouldThrowException() {
            assertThrows(IllegalArgumentException.class, () -> userService.getUserByEmail(""));
        }
    }

    @Nested
    @DisplayName("User Mutation and Management Tests")
    class MutationTests {

        @Test
        @DisplayName("updateRole should modify user role")
        void updateRole_shouldModifyUserRole() {
            Long userId = 1L;
            Role newRole = Role.ADMIN;
            User user = new User();
            user.setId(userId);
            user.setRole(Role.USER);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            userService.updateRole(newRole, userId);

            assertEquals(newRole, user.getRole());
        }

        @Test
        @DisplayName("updateRole with null role should throw exception")
        void updateRole_withNullRole_shouldThrowException() {
            assertThrows(IllegalArgumentException.class, () -> userService.updateRole(null, 1L));
        }

        @Test
        @DisplayName("deleteUserById should call repository delete")
        void deleteUserById_shouldCallRepositoryDelete() {
            Long userId = 1L;
            User user = mock(User.class);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            userService.deleteUserById(userId);

            verify(userRepository).delete(user);
        }

        @Test
        @DisplayName("deactivateUser should disable user and record block info")
        void deactivateUser_shouldDisableUserAndRecordBlockInfo() {
            Long userId = 1L;
            Long adminId = 99L;
            String reason = "spamming";
            User user = new User();
            user.setId(userId);
            user.setActive(true);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            userService.deactivateUser(userId, adminId, reason);

            assertFalse(user.isActive());
            assertEquals(adminId, user.getBlockedById());
            assertEquals(reason, user.getBlockReason());
            assertNotNull(user.getBlockedAt());
        }

        @Test
        @DisplayName("activateUser should enable user and clear block info")
        void activateUser_shouldEnableUserAndClearBlockInfo() {
            Long userId = 1L;
            User user = new User();
            user.setId(userId);
            user.setActive(false);
            user.setBlockedById(99L);
            user.setBlockedAt(Instant.now());
            user.setBlockReason("rules");

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            userService.activateUser(userId);

            assertTrue(user.isActive());
            assertNull(user.getBlockedById());
            assertNull(user.getBlockedAt());
            assertNull(user.getBlockReason());
        }

        @Test
        @DisplayName("existsById should return status from repository")
        void existsById_shouldReturnStatusFromRepository() {
            Long userId = 1L;
            when(userRepository.existsById(userId)).thenReturn(true);

            assertTrue(userService.existsById(userId));
            verify(userRepository).existsById(userId);
        }
    }
}
