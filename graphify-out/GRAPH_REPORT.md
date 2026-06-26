# Graph Report - WTeam  (2026-06-26)

## Corpus Check
- 359 files · ~305,580 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1720 nodes · 4979 edges · 114 communities (98 shown, 16 thin omitted)
- Extraction: 91% EXTRACTED · 9% INFERRED · 0% AMBIGUOUS · INFERRED: 425 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `ffa5c817`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]
- [[_COMMUNITY_Community 9|Community 9]]
- [[_COMMUNITY_Community 10|Community 10]]
- [[_COMMUNITY_Community 11|Community 11]]
- [[_COMMUNITY_Community 12|Community 12]]
- [[_COMMUNITY_Community 13|Community 13]]
- [[_COMMUNITY_Community 14|Community 14]]
- [[_COMMUNITY_Community 15|Community 15]]
- [[_COMMUNITY_Community 16|Community 16]]
- [[_COMMUNITY_Community 17|Community 17]]
- [[_COMMUNITY_Community 18|Community 18]]
- [[_COMMUNITY_Community 19|Community 19]]
- [[_COMMUNITY_Community 20|Community 20]]
- [[_COMMUNITY_Community 21|Community 21]]
- [[_COMMUNITY_Community 22|Community 22]]
- [[_COMMUNITY_Community 23|Community 23]]
- [[_COMMUNITY_Community 24|Community 24]]
- [[_COMMUNITY_Community 25|Community 25]]
- [[_COMMUNITY_Community 26|Community 26]]
- [[_COMMUNITY_Community 27|Community 27]]
- [[_COMMUNITY_Community 28|Community 28]]
- [[_COMMUNITY_Community 29|Community 29]]
- [[_COMMUNITY_Community 30|Community 30]]
- [[_COMMUNITY_Community 31|Community 31]]
- [[_COMMUNITY_Community 32|Community 32]]
- [[_COMMUNITY_Community 33|Community 33]]
- [[_COMMUNITY_Community 34|Community 34]]
- [[_COMMUNITY_Community 35|Community 35]]
- [[_COMMUNITY_Community 36|Community 36]]
- [[_COMMUNITY_Community 37|Community 37]]
- [[_COMMUNITY_Community 38|Community 38]]
- [[_COMMUNITY_Community 39|Community 39]]
- [[_COMMUNITY_Community 40|Community 40]]
- [[_COMMUNITY_Community 41|Community 41]]
- [[_COMMUNITY_Community 42|Community 42]]
- [[_COMMUNITY_Community 43|Community 43]]
- [[_COMMUNITY_Community 44|Community 44]]
- [[_COMMUNITY_Community 45|Community 45]]
- [[_COMMUNITY_Community 46|Community 46]]
- [[_COMMUNITY_Community 47|Community 47]]
- [[_COMMUNITY_Community 48|Community 48]]
- [[_COMMUNITY_Community 49|Community 49]]
- [[_COMMUNITY_Community 50|Community 50]]
- [[_COMMUNITY_Community 51|Community 51]]
- [[_COMMUNITY_Community 52|Community 52]]
- [[_COMMUNITY_Community 53|Community 53]]
- [[_COMMUNITY_Community 54|Community 54]]
- [[_COMMUNITY_Community 55|Community 55]]
- [[_COMMUNITY_Community 56|Community 56]]
- [[_COMMUNITY_Community 57|Community 57]]
- [[_COMMUNITY_Community 58|Community 58]]
- [[_COMMUNITY_Community 59|Community 59]]
- [[_COMMUNITY_Community 60|Community 60]]
- [[_COMMUNITY_Community 61|Community 61]]
- [[_COMMUNITY_Community 62|Community 62]]
- [[_COMMUNITY_Community 63|Community 63]]
- [[_COMMUNITY_Community 64|Community 64]]
- [[_COMMUNITY_Community 65|Community 65]]
- [[_COMMUNITY_Community 66|Community 66]]
- [[_COMMUNITY_Community 67|Community 67]]
- [[_COMMUNITY_Community 68|Community 68]]
- [[_COMMUNITY_Community 69|Community 69]]
- [[_COMMUNITY_Community 70|Community 70]]
- [[_COMMUNITY_Community 71|Community 71]]
- [[_COMMUNITY_Community 72|Community 72]]
- [[_COMMUNITY_Community 73|Community 73]]
- [[_COMMUNITY_Community 74|Community 74]]
- [[_COMMUNITY_Community 75|Community 75]]
- [[_COMMUNITY_Community 76|Community 76]]
- [[_COMMUNITY_Community 77|Community 77]]
- [[_COMMUNITY_Community 78|Community 78]]
- [[_COMMUNITY_Community 79|Community 79]]
- [[_COMMUNITY_Community 81|Community 81]]
- [[_COMMUNITY_Community 82|Community 82]]

## God Nodes (most connected - your core abstractions)
1. `User` - 87 edges
2. `UserPrincipalDto` - 50 edges
3. `useLanguage()` - 44 edges
4. `Role` - 39 edges
5. `Item` - 24 edges
6. `ResourceNotFoundException` - 23 edges
7. `BookingStatus` - 23 edges
8. `Booking` - 22 edges
9. `NotificationEvent` - 21 edges
10. `VerificationStatus` - 20 edges

## Surprising Connections (you probably didn't know these)
- `AiPage()` --calls--> `useLanguage()`  [EXTRACTED]
  frontend/src/pages/AiPage.tsx → frontend/src/contexts/LanguageContext.tsx
- `ChatsPage()` --calls--> `useLanguage()`  [EXTRACTED]
  frontend/src/pages/ChatsPage.tsx → frontend/src/contexts/LanguageContext.tsx
- `CreateItemPage()` --calls--> `useLanguage()`  [EXTRACTED]
  frontend/src/pages/CreateItemPage.tsx → frontend/src/contexts/LanguageContext.tsx
- `NotificationsPage()` --calls--> `useLanguage()`  [EXTRACTED]
  frontend/src/pages/NotificationsPage.tsx → frontend/src/contexts/LanguageContext.tsx
- `AiSession` --inherits--> `BaseEntityPart`  [EXTRACTED]
  backend/src/main/java/com/wteam/backend/ai_session/AiSession.java → backend/src/main/java/com/wteam/backend/common/entity/BaseEntityPart.java

## Import Cycles
- 4-file cycle: `frontend/src/api/auth.ts -> frontend/src/api/axios.ts -> frontend/src/store/store.ts -> frontend/src/store/slices/authSlice.ts -> frontend/src/api/auth.ts`
- 4-file cycle: `frontend/src/api/axios.ts -> frontend/src/store/store.ts -> frontend/src/store/slices/authSlice.ts -> frontend/src/api/users.ts -> frontend/src/api/axios.ts`

## Communities (114 total, 16 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.05
Nodes (39): AdminController, AiSessionController, BlockUserRequest, BookingController, CategoryController, ChatRoomController, DeleteMapping, DevToolsController (+31 more)

### Community 1 - "Community 1"
Cohesion: 0.07
Nodes (33): As, B, JacksonTest, Class, Consumer, Contract, DefaultTypeResolverBuilder, DefaultTyping (+25 more)

### Community 2 - "Community 2"
Cohesion: 0.06
Nodes (26): Async, NotificationSender, Disabled, DltHandler, NotificationEvent, NotificationResponse, EmailSenderService, ResendEmailIntegrationTest (+18 more)

### Community 3 - "Community 3"
Cohesion: 0.09
Nodes (10): BackendApplicationTests, BookingControllerTest, BookingRepositoryTest, DisplayName, ItemControllerTest, Test, UserProfileServiceTest, MutationTests (+2 more)

### Community 4 - "Community 4"
Cohesion: 0.07
Nodes (21): AdminService, AdminStatsResponse, BookingStatus, BookingRepository, CategoryRepository, ChatRoomRepository, Collection, AdminStatsResponse (+13 more)

### Community 5 - "Community 5"
Cohesion: 0.06
Nodes (24): ItemNotAvailableException, DefaultOAuth2UserService, PendingProfileResponse, PublicProfileResponse, UnavailableDateRange, UserProfileRequest, UserProfileResponse, UserProfileVerificationRequest (+16 more)

### Community 6 - "Community 6"
Cohesion: 0.10
Nodes (17): ApplicationEventPublisher, Builder, CacheManager, ItemReviewResponse, UserReviewResponse, Instant, ItemReview, ItemReviewRepository (+9 more)

### Community 7 - "Community 7"
Cohesion: 0.09
Nodes (23): AuthenticationConfiguration, AuthenticationManager, AuthenticationProvider, TestcontainersConfiguration, Bean, CaffeineCacheManager, Cloudinary, ImageService (+15 more)

### Community 8 - "Community 8"
Cohesion: 0.05
Nodes (42): dependencies, axios, lucide-react, react, react-dom, react-hot-toast, react-redux, react-router (+34 more)

### Community 9 - "Community 9"
Cohesion: 0.09
Nodes (23): AuthContext, AuthContextValue, AuthProvider(), LanguageProvider(), Theme, ThemeContext, ThemeContextValue, ThemeProvider() (+15 more)

### Community 10 - "Community 10"
Cohesion: 0.12
Nodes (13): RedisTest, BeforeEach, BigDecimal, Category, AiQueryResponse, BookingResponse, ItemResponse, UserRoleUpdateRequest (+5 more)

### Community 11 - "Community 11"
Cohesion: 0.13
Nodes (14): AuthController, AuthResponse, Cookie, RefreshTokenRequest, FilterChain, HttpServletRequest, HttpServletResponse, MdcLoggingFilter (+6 more)

### Community 12 - "Community 12"
Cohesion: 0.12
Nodes (11): AccessDeniedException, CacheEvict, ItemRequest, IllegalArgumentException, ItemMapper, ItemService, ItemReviewService, ItemRequest (+3 more)

### Community 13 - "Community 13"
Cohesion: 0.11
Nodes (9): AuthControllerTest, AuthService, AuthServiceTest, AuthResponse, LoginRequest, RegisterRequest, AuthSecurityIntegrationTest, PasswordEncoder (+1 more)

### Community 14 - "Community 14"
Cohesion: 0.11
Nodes (23): BookingResponse, ReviewModal(), useLanguage(), AdminPage(), BOOKING_STATUSES, BookingsTab(), CategoriesTab(), ItemsTab() (+15 more)

### Community 15 - "Community 15"
Cohesion: 0.11
Nodes (10): Booking, BookingDelivery, Dispute, ChatRoomResponse, BaseEntityFull, ItemServiceTest, PrePersist, UserProfile (+2 more)

### Community 16 - "Community 16"
Cohesion: 0.10
Nodes (9): BookingMapper, ConstraintValidatorContext, GrantedAuthority, Nullable, Object, Override, PasswordsMatch, SecurityUser (+1 more)

### Community 17 - "Community 17"
Cohesion: 0.15
Nodes (8): Cacheable, Caching, ItemImageService, Long, MultipartFile, UserProfileService, UserService, UserProfileResponse

### Community 18 - "Community 18"
Cohesion: 0.09
Nodes (11): ResourceNotFoundException, BookingNotFoundException, CategoryNotFoundException, ChatRoomNotFoundException, ItemImageNotFoundException, ItemNotFoundException, NotificationNotFoundException, RefreshTokenNotFoundException (+3 more)

### Community 19 - "Community 19"
Cohesion: 0.18
Nodes (13): BadCredentialsException, ConstraintViolationException, DataIntegrityViolationException, Exception, ErrorResponse, GlobalExceptionHandler, ExceptionHandler, HttpMessageNotReadableException (+5 more)

### Community 20 - "Community 20"
Cohesion: 0.11
Nodes (14): AdminStatsResponse, CategoryStat, api, paymentsApi, StripeCheckoutResponse, ReviewModalProps, Language, LanguageContext (+6 more)

### Community 21 - "Community 21"
Cohesion: 0.12
Nodes (16): Extension, TodoAnalysisTask, TodoItem, TypeOfComment, Extension, TodoAnalysisTask, TodoItem, TypeOfComment (+8 more)

### Community 22 - "Community 22"
Cohesion: 0.18
Nodes (8): Booking, BookingService, BookingResponse, StripeCheckoutResponse, Scheduled, ReviewPublishScheduler, Transactional, UnavailableDateRange

### Community 23 - "Community 23"
Cohesion: 0.07
Nodes (27): 1.1. Системні вимоги, 1.2. Отримання вихідного коду, 1.3. Налаштування змінних оточення, 1.4. Запуск інфраструктури (PostgreSQL, Kafka), 1.5. Запуск бекенду, 1.6. Запуск фронтенду, 1.7. Перевірка та доступи, 1.8. Онлайн-доступ (задеплоєна версія) (+19 more)

### Community 24 - "Community 24"
Cohesion: 0.09
Nodes (8): Page, UserProfileRequest, Page, ProfilePage(), BlockUserRequest, PendingProfileResponse, PublicProfileResponse, UserProfileResponse

### Community 25 - "Community 25"
Cohesion: 0.18
Nodes (7): AiSessionControllerTest, AiJsonResponse, AiSessionService, AiSessionServiceTest, AiQueryResponse, AiSession, AiQueryRequest

### Community 26 - "Community 26"
Cohesion: 0.11
Nodes (9): Page, UnavailableDateRange, UserReviewResponse, AvailabilityCalendar(), DAY_NAMES, pluralDays(), Props, toStr() (+1 more)

### Community 27 - "Community 27"
Cohesion: 0.12
Nodes (8): AppException, ResourceAlreadyExistsException, UnauthorizedException, HttpStatus, RefreshTokenInvalidException, ReviewAlreadyExistsException, SecurityProblemSupport, UserAlreadyExistsException

### Community 28 - "Community 28"
Cohesion: 0.13
Nodes (8): Page, CONDITIONS, CreateItemPage(), ItemForm(), ItemFormProps, ItemCondition, ItemImageResponse, ItemRequest

### Community 29 - "Community 29"
Cohesion: 0.15
Nodes (4): ChatRoomControllerTest, MessageRequest, MessageResponse, MessageServiceTest

### Community 30 - "Community 30"
Cohesion: 0.16
Nodes (7): AiSession, ChatRoom, ChatRoomServiceTest, BaseEntityPart, ItemImage, MessageRepositoryTest, Transaction

### Community 31 - "Community 31"
Cohesion: 0.19
Nodes (11): Layout(), Navbar(), Props, ProtectedRoute(), ThemeToggle(), useAuth(), useTheme(), EditItemPage() (+3 more)

### Community 32 - "Community 32"
Cohesion: 0.15
Nodes (6): UserDeviceTokenRequest, UserDeviceTokenResponse, UserDeviceToken, UserDeviceTokenMapper, UserDeviceTokenRepository, UserDeviceTokenService

### Community 33 - "Community 33"
Cohesion: 0.11
Nodes (18): compilerOptions, allowImportingTsExtensions, erasableSyntaxOnly, jsx, lib, module, moduleDetection, moduleResolution (+10 more)

### Community 34 - "Community 34"
Cohesion: 0.18
Nodes (6): AiPage(), ChatPage(), ChatsPage(), AiQueryResponse, ChatRoomResponse, MessageResponse

### Community 35 - "Community 35"
Cohesion: 0.16
Nodes (6): ProviderNotFoundException, InternalServerErrorException, ImageUploadException, NotificationDeliveryException, NotificationPublishException, Throwable

### Community 36 - "Community 36"
Cohesion: 0.23
Nodes (7): ChannelInterceptor, Message, MessageChannel, CustomUserDetailsService, UserDetails, JwtChannelInterceptor, JwtChannelInterceptorTest

### Community 37 - "Community 37"
Cohesion: 0.11
Nodes (17): compilerOptions, allowImportingTsExtensions, erasableSyntaxOnly, lib, module, moduleDetection, moduleResolution, noEmit (+9 more)

### Community 38 - "Community 38"
Cohesion: 0.21
Nodes (5): ChatRoomService, ChatRoomResponse, MessageRepository, MessageService, MessageResponse

### Community 39 - "Community 39"
Cohesion: 0.23
Nodes (6): Claims, Date, Function, JwtService, SecretKey, JwtServiceTest

### Community 41 - "Community 41"
Cohesion: 0.22
Nodes (6): Category, CategoryMapper, CategoryService, CategoryRequest, CategoryResponse, CategoryResponse

### Community 42 - "Community 42"
Cohesion: 0.12
Nodes (15): AI / ML, 🤖 AI-асистент, 📄 API документація, Backend, Frontend, 🖥️ Адміністративна панель, 🏗️ Архітектура, 🔐 Безпека (+7 more)

### Community 43 - "Community 43"
Cohesion: 0.17
Nodes (5): ForbiddenOperationException, ChatAccessDeniedException, ItemImageAccessDeniedException, UserDeviceTokenAccessDeniedException, ProfileIncompleteException

### Community 45 - "Community 45"
Cohesion: 0.15
Nodes (13): 27. JwtAuthFilter — завантажує UserDetails з БД на кожен запит (Backend), 28. Зберігається сира UUID як tokenHash (Backend), 29. OAuth2 передає токени через GET-параметри URL (Backend), 30. WebSocket URL захардкоджений у фронтенді (Frontend), 31. ChatRoomService — тип `otherUserName` як ID рядком (Backend), 32. BookingRepository.item() — загадковий метод без реалізації (Backend), 33. Тест `updateItem_whenUserIsOwner` потребує SecurityContext мок — ознака проблеми (Tests), 34. Відсутня перевірка чи рентер не є власником (Backend) (+5 more)

### Community 46 - "Community 46"
Cohesion: 0.24
Nodes (5): ApplicationEvent, EventListener, IllegalStateException, ReviewPublishedEvent, TrustScoreRecalculationListener

### Community 47 - "Community 47"
Cohesion: 0.21
Nodes (4): BadRequestException, InvalidBookingStateException, InvalidReviewStateException, ProfileAlreadyVerifiedException

### Community 48 - "Community 48"
Cohesion: 0.29
Nodes (4): Bucket, HandlerInterceptor, RateLimitingInterceptor, RateLimitingService

### Community 49 - "Community 49"
Cohesion: 0.18
Nodes (9): AiSession, Booking, Category, Constraints, Item, RefreshToken, User, UserProfile (+1 more)

### Community 50 - "Community 50"
Cohesion: 0.27
Nodes (4): LoginRequest, RefreshTokenRepository, RefreshTokenService, RefreshToken

### Community 51 - "Community 51"
Cohesion: 0.27
Nodes (7): Authentication, WebConfig, InterceptorRegistry, NonNull, OAuth2SuccessHandler, SimpleUrlAuthenticationSuccessHandler, WebMvcConfigurer

### Community 52 - "Community 52"
Cohesion: 0.33
Nodes (5): ChannelRegistration, MessageBrokerRegistry, StompEndpointRegistry, WebSocketConfig, WebSocketMessageBrokerConfigurer

### Community 53 - "Community 53"
Cohesion: 0.36
Nodes (6): NamedEnumAwareH2Dialect, H2Dialect, JdbcType, JdbcTypeRegistry, ServiceRegistry, TypeContributions

### Community 54 - "Community 54"
Cohesion: 0.22
Nodes (8): 🏗️ АРХІТЕКТУРНІ ЗМІНИ, 📊 Загальна картина, Нові функції, 🔍 Повний аналіз проекту WTeam (RentGo), Що варто зробити, Що варто прибрати, ✅ Що зроблено добре, 🟢 ЩО МОЖНА ДОДАТИ

### Community 55 - "Community 55"
Cohesion: 0.22
Nodes (9): 10. Пошук на BrowsePage — клієнтський, але API підтримує серверний, 11. Самовиклик через ObjectProvider — складно і непотрібно, 12. AI сервіс — завжди завантажує 50 items (Backend), 13. RestClient без пулу з'єднань (Backend), 14. Kafka є, але майже не використовується, 15. Депозит розраховується помилково, 8. Dispute модуль — "мертвий" код, 9. Фільтрація і пагінація відбувається на фронтенді (MyBookingsPage) (+1 more)

### Community 56 - "Community 56"
Cohesion: 0.32
Nodes (3): ApplicationEnvironmentPreparedEvent, BackendApplication, PortAvailabilityListener

### Community 57 - "Community 57"
Cohesion: 0.36
Nodes (4): TodoPlugin, TodoPlugin, Plugin, Project

### Community 58 - "Community 58"
Cohesion: 0.25
Nodes (8): 1. Конфлікт стану автентифікації (Frontend), 2. Витік внутрішніх деталей помилок у production (Backend), 3. AccessDeniedException кидається не через Spring Security (Backend), 4. Обхід ADMIN-перевірки через SecurityContext у Service (Backend), 5. Некоректна валідація у setStatusForBooking (Backend), 6. Токени зберігаються у localStorage (Frontend), 7. Stripe webhook не захищений від повторних запитів (Backend), 🔴 КРИТИЧНІ БАГИ ТА ПРОБЛЕМИ БЕЗПЕКИ

### Community 59 - "Community 59"
Cohesion: 0.25
Nodes (8): 20. LanguageContext.tsx занадто великий, 21. Компоненти-гіганти без декомпозиції, 22. Відсутній error boundary, 23. `confirm()` і `prompt()` замість модальних вікон, 24. Змішування Tailwind і кастомного CSS, 25. Hardcoded тексти змішані з i18n, 26. Redux використовується тільки для Auth, і то неправильно, 🔵 ПРОБЛЕМИ АРХІТЕКТУРИ ФРОНТЕНДУ

### Community 60 - "Community 60"
Cohesion: 0.38
Nodes (3): FirebaseConfig, StripeConfig, PostConstruct

### Community 61 - "Community 61"
Cohesion: 0.33
Nodes (4): Entity, Mapper, Request, Response

### Community 63 - "Community 63"
Cohesion: 0.60
Nodes (3): MessageMapping, SendTo, ChatWebSocketController

### Community 64 - "Community 64"
Cohesion: 0.40
Nodes (5): 16. N+1 проблема в MyBookingsPage, 17. Кеш items інвалідується повністю при будь-якому update, 18. Відсутня lazy loading зображень, 19. WebSocket конфігурація без Nginx підтримки, 🟡 ПРОБЛЕМИ ПРОДУКТИВНОСТІ

### Community 65 - "Community 65"
Cohesion: 0.40
Nodes (5): `MODER` роль існує, але не використовується, Notification система: гарна архітектура, Refresh Token Rotation — правильно реалізовано, 🟡 ДОДАТКОВІ АРХІТЕКТУРНІ СПОСТЕРЕЖЕННЯ, Тести: добра структура, але є gaps

### Community 66 - "Community 66"
Cohesion: 0.50
Nodes (3): Expanding the ESLint configuration, React Compiler, React + TypeScript + Vite

### Community 67 - "Community 67"
Cohesion: 0.50
Nodes (4): 🟠 Важливо (архітектура/продуктивність), 📋 ПРІОРИТЕТНИЙ ПЛАН ВИПРАВЛЕНЬ, 🟡 Середньо (якість коду), 🔴 Терміново (безпека/критичні баги)

### Community 68 - "Community 68"
Cohesion: 0.50
Nodes (4): Довгострокові, 🟢 ДОДАТКОВІ ПРОПОЗИЦІЇ НОВИХ ФУНКЦІЙ, Короткострокові (легко реалізувати на базі існуючого), Середньострокові

## Knowledge Gaps
- **215 isolated node(s):** `TypeOfComment`, `TypeOfComment`, `AiSessionRepository`, `BookingDeliveryRepository`, `BookingDeliveryService` (+210 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **16 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Role` connect `Community 10` to `Community 0`, `Community 3`, `Community 4`, `Community 5`, `Community 13`, `Community 14`, `Community 15`, `Community 16`, `Community 17`, `Community 24`, `Community 29`?**
  _High betweenness centrality (0.082) - this node is a cross-community bridge._
- **Why does `BookingStatus` connect `Community 4` to `Community 2`, `Community 6`, `Community 40`, `Community 10`, `Community 12`, `Community 14`, `Community 22`, `Community 26`?**
  _High betweenness centrality (0.069) - this node is a cross-community bridge._
- **Why does `version` connect `Community 1` to `Community 8`?**
  _High betweenness centrality (0.055) - this node is a cross-community bridge._
- **What connects `TypeOfComment`, `TypeOfComment`, `AiSessionRepository` to the rest of the system?**
  _215 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.054625984251968504 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.06625258799171843 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.06286748077792854 - nodes in this community are weakly interconnected._