package com.wteam.backend.ai_session;

import com.wteam.backend.ai_session.dto.AiQueryRequest;
import com.wteam.backend.ai_session.dto.AiQueryResponse;
import com.wteam.backend.security.SecurityUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiSessionController {

    private final AiSessionService aiSessionService;

    @PostMapping("/recommend")
    public ResponseEntity<AiQueryResponse> recommend(
            @Valid @RequestBody AiQueryRequest request,
            @AuthenticationPrincipal SecurityUser currentUser
    ) {
        Long userId = currentUser != null ? currentUser.getId() : null;
        return ResponseEntity.ok(aiSessionService.processQuery(request, userId));
    }
}
