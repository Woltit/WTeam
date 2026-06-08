package com.wteam.backend.ai_session;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wteam.backend.ai_session.dto.AiQueryRequest;
import com.wteam.backend.ai_session.dto.AiQueryResponse;
import com.wteam.backend.common.enums.RentingStatus;
import com.wteam.backend.item.Item;
import com.wteam.backend.item.ItemRepository;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiSessionService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4o-mini";

    private final AiSessionRepository aiSessionRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Value("${openai.api-key}")
    private String openAiApiKey;

    @Transactional
    public AiQueryResponse processQuery(AiQueryRequest request, Long userId) {
        List<Item> availableItems = itemRepository
                .findAllByStatusAndIsVerifiedTrue(RentingStatus.AVAILABLE, PageRequest.of(0, 50))
                .getContent();

        String itemsContext = buildItemsContext(availableItems);
        String prompt = buildPrompt(request.query(), itemsContext);

        String rawResponse = callOpenAi(prompt);
        AiJsonResponse parsed = parseAiResponse(rawResponse);

        User user = userId != null
                ? userRepository.findById(userId).orElse(null)
                : null;

        AiSession session = AiSession.builder()
                .user(user)
                .userQuery(request.query())
                .aiResponse(parsed.response())
                .recommendedItemIds(parsed.recommendedItemIds().stream().mapToLong(Long::longValue).boxed().toArray(Long[]::new))
                .build();

        AiSession saved = aiSessionRepository.save(session);

        return new AiQueryResponse(
                saved.getId(),
                parsed.response(),
                parsed.recommendedItemIds()
        );
    }

    private String buildItemsContext(List<Item> items) {
        if (items.isEmpty()) return "Наразі немає доступних речей на платформі.";

        StringBuilder sb = new StringBuilder();
        for (Item item : items) {
            sb.append("ID:%d | %s | %s | %.2f грн/день | %s\n".formatted(
                    item.getId(),
                    item.getTitle(),
                    item.getCategory() != null ? item.getCategory().getName() : "без категорії",
                    item.getPricePerDay(),
                    item.getCity()
            ));
        }
        return sb.toString();
    }

    private String buildPrompt(String userQuery, String itemsContext) {
        return """
                Ти — AI-асистент платформи RentGo (маркетплейс оренди речей в Україні).

                Доступні речі для оренди на платформі:
                %s

                Запит користувача: "%s"

                Проаналізуй запит і порекомендуй конкретні речі зі списку вище.
                Відповідай ТІЛЬКИ у форматі JSON:
                {
                  "response": "Текст відповіді для користувача українською мовою",
                  "recommendedItemIds": [список ID рекомендованих речей зі списку]
                }
                Якщо жодна річ не підходить — поверни порожній масив.
                """.formatted(itemsContext, userQuery);
    }

    private String callOpenAi(String prompt) {
        RestClient restClient = RestClient.create();

        Map<String, Object> body = Map.of(
                "model", MODEL,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7,
                "response_format", Map.of("type", "json_object")
        );

        Map<?, ?> response = restClient.post()
                .uri(OPENAI_URL)
                .header("Authorization", "Bearer " + openAiApiKey)
                .header("Content-Type", "application/json")
                .body(body)
                .retrieve()
                .body(Map.class);

        List<?> choices = (List<?>) response.get("choices");
        Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
        Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
        return (String) message.get("content");
    }

    private AiJsonResponse parseAiResponse(String rawJson) {
        try {
            return MAPPER.readValue(rawJson, AiJsonResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", rawJson, e);
            return new AiJsonResponse("Не вдалося обробити відповідь AI. Спробуйте ще раз.", List.of());
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AiJsonResponse(
            String response,
            List<Long> recommendedItemIds
    ) {}
}
