package com.wteam.backend.ai_session;

import com.wteam.backend.ai_session.dto.AiQueryRequest;
import com.wteam.backend.ai_session.dto.AiQueryResponse;
import com.wteam.backend.common.enums.RentingStatus;
import com.wteam.backend.item.Item;
import com.wteam.backend.item.ItemRepository;
import com.wteam.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AiSessionService Unit Tests")
class AiSessionServiceTest {

    @Mock private AiSessionRepository aiSessionRepository;
    @Mock private ItemRepository itemRepository;
    @Mock private UserRepository userRepository;

    private AiSessionService aiSessionService;

    @BeforeEach
    void setUp() {
        aiSessionService = spy(new AiSessionService(aiSessionRepository, itemRepository, userRepository));
    }

    private AiSession savedSession(String response, Long[] ids) {
        return AiSession.builder()
                .id(1L).aiResponse(response).userQuery("query")
                .recommendedItemIds(ids).build();
    }

    @Test
    @DisplayName("processQuery should include Ukrainian placeholder when no items are available")
    void processQuery_whenNoItems_promptShouldContainUkrainianPlaceholder() {
        String fakeJson = "{\"response\":\"no items\",\"recommendedItemIds\":[]}";
        doReturn(fakeJson).when(aiSessionService).callOpenAi(anyString());
        when(itemRepository.findAllByStatusAndIsVerifiedTrue(eq(RentingStatus.AVAILABLE), any()))
                .thenReturn(new PageImpl<>(List.of()));
        when(aiSessionRepository.save(any())).thenReturn(savedSession("no items", new Long[0]));

        aiSessionService.processQuery(new AiQueryRequest("need something"), null);

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiSessionService).callOpenAi(promptCaptor.capture());
        assertThat(promptCaptor.getValue()).contains("Наразі немає доступних речей");
    }

    @Test
    @DisplayName("processQuery should include item details in prompt when items are available")
    void processQuery_whenItemsExist_promptShouldContainItemInfo() {
        Item item = new Item();
        item.setId(5L);
        item.setTitle("Drill");
        item.setPricePerDay(BigDecimal.valueOf(50));
        item.setCity("Kyiv");

        String fakeJson = "{\"response\":\"ok\",\"recommendedItemIds\":[5]}";
        doReturn(fakeJson).when(aiSessionService).callOpenAi(anyString());
        when(itemRepository.findAllByStatusAndIsVerifiedTrue(eq(RentingStatus.AVAILABLE), any()))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(aiSessionRepository.save(any())).thenReturn(savedSession("ok", new Long[]{5L}));

        aiSessionService.processQuery(new AiQueryRequest("drill"), null);

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiSessionService).callOpenAi(promptCaptor.capture());
        assertThat(promptCaptor.getValue()).contains("Drill").contains("Kyiv");
    }

    @Test
    @DisplayName("processQuery should return fallback response when AI returns invalid JSON")
    void processQuery_whenAiReturnsInvalidJson_shouldReturnFallback() {
        doReturn("THIS_IS_NOT_JSON").when(aiSessionService).callOpenAi(anyString());
        when(itemRepository.findAllByStatusAndIsVerifiedTrue(eq(RentingStatus.AVAILABLE), any()))
                .thenReturn(new PageImpl<>(List.of()));
        when(aiSessionRepository.save(any())).thenReturn(savedSession("Не вдалося", new Long[0]));

        AiQueryResponse result = aiSessionService.processQuery(new AiQueryRequest("query"), null);

        assertNotNull(result);
        verify(aiSessionRepository).save(any(AiSession.class));
    }

    @Test
    @DisplayName("processQuery should save an AiSession to the repository")
    void processQuery_shouldSaveSessionToRepository() {
        String fakeJson = "{\"response\":\"great choice\",\"recommendedItemIds\":[1,2]}";
        doReturn(fakeJson).when(aiSessionService).callOpenAi(anyString());
        when(itemRepository.findAllByStatusAndIsVerifiedTrue(eq(RentingStatus.AVAILABLE), any()))
                .thenReturn(new PageImpl<>(List.of()));

        AiSession saved = savedSession("great choice", new Long[]{1L, 2L});
        when(aiSessionRepository.save(any())).thenReturn(saved);

        AiQueryResponse result = aiSessionService.processQuery(new AiQueryRequest("need a drill"), null);

        verify(aiSessionRepository).save(any(AiSession.class));
        assertNotNull(result);
        assertEquals("great choice", result.aiResponse());
        assertEquals(List.of(1L, 2L), result.recommendedItemIds());
    }

    @Test
    @DisplayName("processQuery with userId should associate session with user")
    void processQuery_withUserId_shouldSaveSessionWithUser() {
        String fakeJson = "{\"response\":\"ok\",\"recommendedItemIds\":[]}";
        doReturn(fakeJson).when(aiSessionService).callOpenAi(anyString());
        when(itemRepository.findAllByStatusAndIsVerifiedTrue(eq(RentingStatus.AVAILABLE), any()))
                .thenReturn(new PageImpl<>(List.of()));
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        when(aiSessionRepository.save(any())).thenReturn(savedSession("ok", new Long[0]));

        aiSessionService.processQuery(new AiQueryRequest("search"), 1L);

        ArgumentCaptor<AiSession> captor = ArgumentCaptor.forClass(AiSession.class);
        verify(aiSessionRepository).save(captor.capture());
        assertEquals("search", captor.getValue().getUserQuery());
    }
}
