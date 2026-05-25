package com.wteam.backend.user;

import com.wteam.backend.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserService userService;

    @Test
    void getAllUsersWhoIsActive_shouldReturnPageOfUserResponses() {
        User user = mock(User.class);
        UserResponse userResponse = mock(UserResponse.class);
        Page<User> usersPage = new PageImpl<>(List.of(user));
        Pageable pageable = Pageable.unpaged();

        when(userRepository.findAllByIsActiveTrue(pageable)).thenReturn(usersPage);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        Page<UserResponse> result = userService.getAllUsersWhoIsActive(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(userResponse, result.getContent().getFirst());

        verify(userRepository, times(1)).findAllByIsActiveTrue(pageable);
        verify(userMapper, times(1)).toResponse(user);
    }
}
