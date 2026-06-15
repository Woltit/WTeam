package com.wteam.backend.user_device_token;

import com.wteam.backend.security.annotation.CurrentUser;
import com.wteam.backend.security.dto.UserPrincipalDto;
import com.wteam.backend.user_device_token.dto.UserDeviceTokenRequest;
import com.wteam.backend.user_device_token.dto.UserDeviceTokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/user-device-tokens")
@RequiredArgsConstructor
public class UserDeviceTokenController {
    private final UserDeviceTokenService userDeviceTokenService;

    @PostMapping("/me/fcm-token")
    public ResponseEntity<UserDeviceTokenResponse> saveToken(
            @RequestBody @Valid UserDeviceTokenRequest request,
            @CurrentUser UserPrincipalDto user
    ) {
        UserDeviceTokenResponse savedToken = userDeviceTokenService.saveToken(user.id(), request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedToken.id())
                .toUri();

        return ResponseEntity.created(location)
                .body(savedToken);
    }

    @DeleteMapping("/me/fcm-token")
    public ResponseEntity<Void> deleteToken(
            @RequestParam String token,
            @CurrentUser UserPrincipalDto user
    ) {
        userDeviceTokenService.deleteTokenForUser(token, user.id());
        return ResponseEntity.noContent().build();
    }
}
