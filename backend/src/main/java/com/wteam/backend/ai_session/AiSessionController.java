package com.wteam.backend.ai_session;

import com.wteam.backend.ai_session.dto.AiQueryRequest;
import com.wteam.backend.ai_session.dto.AiQueryResponse;
import com.wteam.backend.security.annotation.CurrentUser;
import com.wteam.backend.security.dto.UserPrincipalDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiSessionController {
    private final AiSessionService aiSessionService;

    @PostMapping("/recommend")
    public ResponseEntity<AiQueryResponse> recommend(
            @Valid @RequestBody AiQueryRequest request,
            @CurrentUser UserPrincipalDto currentUser
    ) {
        Long userId = currentUser != null ? currentUser.id() : null;
        return ResponseEntity.ok(aiSessionService.processQuery(request, userId));
    }
}
