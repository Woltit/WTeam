package com.wteam.backend.ai_session.dto;

import java.util.List;

public record AiQueryResponse(
        Long sessionId,
        String aiResponse,
        List<Long> recommendedItemIds
) {}
