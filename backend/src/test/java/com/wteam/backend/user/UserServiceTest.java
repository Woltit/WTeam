//package com.wteam.backend.user;
//
//import com.wteam.backend.common.enums.Role;
//import com.wteam.backend.common.enums.VerificationStatus;
//import com.wteam.backend.exception.user.UserNotFoundException;
//import com.wteam.backend.exception.user_profile.ProfileIncompleteException;
//import com.wteam.backend.exception.user_profile.ProfileNotFoundException;
//import com.wteam.backend.user.dto.UserResponse;
//import com.wteam.backend.user_profile.UserProfile;
//import com.wteam.backend.user_profile.dto.UserProfileRequest;
//import com.wteam.backend.user_profile.dto.UserProfileResponse;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
///**
// * Тести для бізнес-логіки управління користувачами та їх профілями.
// */
//@ExtendWith(MockitoExtension.class)
//@DisplayName("UserService: Тести управління користувачами")
//class UserServiceTest {
//
//    @Mock
//    UserRepository userRepository;
//
//    @Mock
//    UserMapper userMapper;
//
//    @InjectMocks
//    UserService userService;
//
//    @Nested
//    @DisplayName("Пошук та отримання користувачів")
//    class UserSearchTests {
//
//        @Test
//        @DisplayName("Отримання всіх користувачів: повертає сторінку з DTO")
//        void getAllUsers_shouldReturnPageOfUserResponses() {
//            User user = mock(User.class);
//            UserResponse userResponse = mock(UserResponse.class);
//            Page<User> usersPage = new PageImpl<>(List.of(user));
//            Pageable pageable = Pageable.unpaged();
//
//            when(userRepository.findAllWithProfile(pageable)).thenReturn(usersPage);
//            when(userMapper.toResponse(user)).thenReturn(userResponse);
//
//            Page<UserResponse> result = userService.getAllUsers(pageable);
//
//            assertNotNull(result);
//            assertEquals(1, result.getTotalElements());
//            assertEquals(userResponse, result.getContent().getFirst());
//
//            verify(userRepository).findAllWithProfile(pageable);
//            verify(userMapper).toResponse(user);
//        }
//
//        @Test
//        @DisplayName("Пошук за ID: повертає користувача, якщо він існує")
//        void getUserById_whenUserExists_shouldReturnUserResponse() {
//            Long userId = 1L;
//            User user = mock(User.class);
//            UserResponse userResponse = mock(UserResponse.class);
//
//            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//            when(userMapper.toResponse(user)).thenReturn(userResponse);
//
//            UserResponse result = userService.getUserById(userId);
//
//            assertNotNull(result);
//            assertEquals(userResponse, result);
//
//            verify(userRepository).findById(userId);
//        }
//
//        @Test
//        @DisplayName("Пошук за ID: викликає UserNotFoundException, якщо користувача не знайдено")
//        void getUserById_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
//            Long userId = 1L;
//            when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
//            assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
//        }
//
//        @Test
//        @DisplayName("Пошук за Email: повертає користувача, якщо він існує")
//        void getUserByEmail_whenUserExists_shouldReturnUserResponse() {
//            String email = "test@example.com";
//            User user = mock(User.class);
//            UserResponse userResponse = mock(UserResponse.class);
//
//            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
//            when(userMapper.toResponse(user)).thenReturn(userResponse);
//
//            UserResponse result = userService.getUserByEmail(email);
//
//            assertNotNull(result);
//            assertEquals(userResponse, result);
//
//            verify(userRepository).findByEmail(email);
//        }
//
//        @Test
//        @DisplayName("Пошук за Email: викликає IllegalArgumentException, якщо email порожній")
//        void getUserByEmail_whenEmailIsEmpty_shouldThrowIllegalArgumentException() {
//            assertThrows(IllegalArgumentException.class, () -> userService.getUserByEmail(""));
//        }
//
//        @Test
//        @DisplayName("Фільтрація: повертає лише активних користувачів")
//        void getAllUsersWhoIsActive_shouldReturnPageOfUserResponses() {
//            User user = mock(User.class);
//            UserResponse userResponse = mock(UserResponse.class);
//            Page<User> usersPage = new PageImpl<>(List.of(user));
//            Pageable pageable = Pageable.unpaged();
//
//            when(userRepository.findAllByIsActiveTrue(pageable)).thenReturn(usersPage);
//            when(userMapper.toResponse(user)).thenReturn(userResponse);
//
//            Page<UserResponse> result = userService.getAllUsersWhoIsActive(pageable);
//
//            assertNotNull(result);
//            assertEquals(1, result.getTotalElements());
//            assertEquals(userResponse, result.getContent().getFirst());
//
//            verify(userRepository).findAllByIsActiveTrue(pageable);
//            verify(userMapper).toResponse(user);
//        }
//
//        @Test
//        @DisplayName("Фільтрація: повертає лише неактивних користувачів")
//        void getAllUsersWhoIsNotActive_shouldReturnPageOfUserResponses() {
//            User user = mock(User.class);
//            UserResponse userResponse = mock(UserResponse.class);
//            Page<User> usersPage = new PageImpl<>(List.of(user));
//            Pageable pageable = Pageable.unpaged();
//
//            when(userRepository.findAllByIsActiveFalse(pageable)).thenReturn(usersPage);
//            when(userMapper.toResponse(user)).thenReturn(userResponse);
//
//            Page<UserResponse> result = userService.getAllUsersWhoIsNotActive(pageable);
//
//            assertNotNull(result);
//            assertEquals(1, result.getTotalElements());
//            assertEquals(userResponse, result.getContent().getFirst());
//
//            verify(userRepository).findAllByIsActiveFalse(pageable);
//        }
//
//        @Test
//        @DisplayName("Фільтрація: повертає користувачів за роллю")
//        void getAllUsersByRole_shouldReturnPageOfUserResponses() {
//            Role role = Role.USER;
//            User user = mock(User.class);
//            UserResponse userResponse = mock(UserResponse.class);
//            Page<User> usersPage = new PageImpl<>(List.of(user));
//            Pageable pageable = Pageable.unpaged();
//
//            when(userRepository.findAllByRole(role, pageable)).thenReturn(usersPage);
//            when(userMapper.toResponse(user)).thenReturn(userResponse);
//
//            Page<UserResponse> result = userService.getAllUsersByRole(role, pageable);
//
//            assertNotNull(result);
//            assertEquals(1, result.getTotalElements());
//
//            verify(userRepository).findAllByRole(role, pageable);
//        }
//
//        @Test
//        @DisplayName("Перевірка існування: повертає true, якщо ID існує")
//        void existsById_shouldReturnTrueWhenUserExists() {
//            Long userId = 1L;
//            when(userRepository.existsById(userId)).thenReturn(true);
//
//            boolean result = userService.existsById(userId);
//
//            assertTrue(result);
//
//            verify(userRepository).existsById(userId);
//        }
//    }
//
//    @Nested
//    @DisplayName("Управління аккаунтом та ролями")
//    class UserManagementTests {
//
//        @Test
//        @DisplayName("Оновлення ролі: змінює роль користувача")
//        void updateRole_whenUserExists_shouldUpdateRole() {
//            Long userId = 1L;
//            Role newRole = Role.ADMIN;
//            User user = new User();
//            user.setId(userId);
//            user.setRole(Role.USER);
//
//            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//            userService.updateRole(newRole, userId);
//
//            assertEquals(newRole, user.getRole());
//
//            verify(userRepository).findById(userId);
//        }
//
//        @Test
//        @DisplayName("Оновлення ролі: викликає IllegalArgumentException, якщо роль null")
//        void updateRole_whenRoleIsNull_shouldThrowIllegalArgumentException() {
//            assertThrows(IllegalArgumentException.class, () -> userService.updateRole(null, 1L));
//        }
//
//        @Test
//        @DisplayName("Видалення: видаляє користувача з бази даних")
//        void deleteUserById_whenUserExists_shouldDeleteUser() {
//            Long userId = 1L;
//            User user = mock(User.class);
//
//            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//            userService.deleteUserById(userId);
//
//            verify(userRepository).delete(user);
//        }
//
//        @Test
//        @DisplayName("Деактивація: блокує користувача та записує причину")
//        void deactivateUser_whenUserExists_shouldSetIsActiveToFalse() {
//            Long userId = 1L;
//            Long adminId = 2L;
//            String reason = "Violation";
//            User user = new User();
//            user.setId(userId);
//            user.setActive(true);
//
//            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//            userService.deactivateUser(userId, adminId, reason);
//
//            assertFalse(user.isActive());
//            assertEquals(adminId, user.getBlockedById());
//            assertEquals(reason, user.getBlockReason());
//            assertNotNull(user.getBlockedAt());
//        }
//
//        @Test
//        @DisplayName("Активація: розблоковує користувача та очищує дані блокування")
//        void activateUser_whenUserExists_shouldSetIsActiveToTrue() {
//            Long userId = 1L;
//            User user = new User();
//            user.setId(userId);
//            user.setActive(false);
//
//            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//            userService.activateUser(userId);
//
//            assertTrue(user.isActive());
//            assertNull(user.getBlockedById());
//            assertNull(user.getBlockReason());
//            assertNull(user.getBlockedAt());
//        }
//    }
//
//    @Nested
//    @DisplayName("Робота з профілем користувача")
//    class UserProfileTests {
//
//        @Test
//        @DisplayName("Отримання профілю: повертає дані профілю, якщо вони існують")
//        void getProfile_whenUserAndProfileExist_shouldReturnProfileResponse() {
//            Long userId = 1L;
//            UserProfile profile = mock(UserProfile.class);
//            User user = mock(User.class);
//            UserProfileResponse profileResponse = mock(UserProfileResponse.class);
//
//            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//            when(user.getUserProfile()).thenReturn(profile);
//            when(userMapper.toProfileResponse(profile)).thenReturn(profileResponse);
//
//            UserProfileResponse result = userService.getProfile(userId);
//
//            assertNotNull(result);
//            assertEquals(profileResponse, result);
//        }
//
//        @Test
//        @DisplayName("Отримання профілю: викликає UserNotFoundException, якщо користувача не знайдено")
//        void getProfile_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
//            Long userId = 1L;
//            when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
//            assertThrows(UserNotFoundException.class, () -> userService.getProfile(userId));
//        }
//
//        @Test
//        @DisplayName("Отримання профілю: викликає ProfileNotFoundException, якщо профіль відсутній")
//        void getProfile_whenProfileDoesNotExist_shouldThrowProfileNotFoundException() {
//            Long userId = 1L;
//            User user = mock(User.class);
//
//            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//            when(user.getUserProfile()).thenReturn(null);
//
//            assertThrows(ProfileNotFoundException.class, () -> userService.getProfile(userId));
//        }
//
//        @Test
//        @DisplayName("Оновлення профілю: успішно оновлює поля через мапер")
//        void updateProfile_whenUserAndProfileExist_shouldReturnUserResponse() {
//            Long userId = 1L;
//            UserProfileRequest request = mock(UserProfileRequest.class);
//            UserProfile profile = mock(UserProfile.class);
//            User user = mock(User.class);
//            UserResponse userResponse = mock(UserResponse.class);
//
//            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//            when(user.getUserProfile()).thenReturn(profile);
//            when(userMapper.toResponse(user)).thenReturn(userResponse);
//
//            UserResponse result = userService.updateProfile(userId, request);
//
//            assertNotNull(result);
//            assertEquals(userResponse, result);
//            verify(userMapper).updateProfileFromRequest(request, profile);
//        }
//
//        @Test
//        @DisplayName("Верифікація: змінює статус верифікації профілю")
//        void updateVerificationStatus_shouldUpdateStatusAndReturnUserResponse() {
//            Long userId = 1L;
//            VerificationStatus status = VerificationStatus.VERIFIED;
//            UserProfile profile = new UserProfile();
//            User user = mock(User.class);
//            UserResponse userResponse = mock(UserResponse.class);
//
//            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//            when(user.getUserProfile()).thenReturn(profile);
//            when(userMapper.toResponse(user)).thenReturn(userResponse);
//
//            UserResponse result = userService.updateVerificationStatus(userId, status);
//
//            assertEquals(status, profile.getVerificationStatus());
//            assertEquals(userResponse, result);
//        }
//    }
//
//    @Nested
//    @DisplayName("Валідація бізнес-правил")
//    class UserValidationTests {
//
//        @Test
//        @DisplayName("Валідація оголошень: дозволяє дію, якщо профіль повністю заповнений та верифікований")
//        void validateUserCanPlaceOffers_whenProfileIsComplete_shouldNotThrowException() {
//            Long userId = 1L;
//            UserProfile profile = mock(UserProfile.class);
//            User user = mock(User.class);
//
//            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//            when(user.getUserProfile()).thenReturn(profile);
//            when(profile.getPhoneNumber()).thenReturn("+380991112233");
//            when(profile.getBirthDate()).thenReturn(LocalDate.now().minusYears(20));
//            when(profile.getVerificationStatus()).thenReturn(VerificationStatus.VERIFIED);
//
//            assertDoesNotThrow(() -> userService.validateUserCanPlaceOffers(userId));
//        }
//
//        @Test
//        @DisplayName("Валідація оголошень: викликає ProfileIncompleteException, якщо дані профілю неповні")
//        void validateUserCanPlaceOffers_whenProfileIsIncomplete_shouldThrowProfileIncompleteException() {
//            Long userId = 1L;
//            UserProfile profile = mock(UserProfile.class);
//            User user = mock(User.class);
//
//            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//            when(user.getUserProfile()).thenReturn(profile);
//
//            assertThrows(ProfileIncompleteException.class, () -> userService.validateUserCanPlaceOffers(userId));
//        }
//    }
//}
