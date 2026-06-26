# 🔍 Повний аналіз проекту WTeam (RentGo)

> Маркетплейс оренди речей. Spring Boot 4 / Java 25 бекенд + React 19 / Vite / TypeScript фронтенд.

---

## 📊 Загальна картина

Проект має **добре продуману базову архітектуру** — чітке розділення по модулях, власний TodoPlugin для Gradle, OpenAPI документація, Kafka для подій, Redis для кешування, Stripe для платежів, Firebase для push-сповіщень, AI-рекомендації через GPT-4o-mini. Це вже добра основа, але є **критичні проблеми** які потрібно виправити.

---

## 🔴 КРИТИЧНІ БАГИ ТА ПРОБЛЕМИ БЕЗПЕКИ

### 1. Конфлікт стану автентифікації (Frontend)
**Файли:** [`AuthContext.tsx`](file:///c:/Users/Admin/work_projects/WTeam/frontend/src/contexts/AuthContext.tsx), [`authSlice.ts`](file:///c:/Users/Admin/work_projects/WTeam/frontend/src/store/slices/authSlice.ts), [`axios.ts`](file:///c:/Users/Admin/work_projects/WTeam/frontend/src/api/axios.ts)

**Проблема:** В проекті паралельно існує **два незалежних сховища** стану авторизації:
- `AuthContext` (React Context) — використовує `localStorage.setItem('accessToken', ...)`
- `authSlice` (Redux) — використовує `localStorage.setItem('token', ...)`
- `axios.ts` — читає `localStorage.getItem('accessToken')`

Redux-слайс практично **не використовується** ніде в коді (жоден компонент не читає з нього), а весь реальний стан зберігається в Context. Але при цьому Redux все одно залишається у бандлі і займає місце. Це технічний борг і потенційна точка плутанини.

```diff
- localStorage.setItem('token', token);       // authSlice — нікуди не читається
+ localStorage.setItem('accessToken', token);  // або прибрати Redux повністю
```

### 2. Витік внутрішніх деталей помилок у production (Backend)
**Файл:** [`GlobalExceptionHandler.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/exception/GlobalExceptionHandler.java#L187-L191)

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("Unhandled exception occurred: ", e);
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e.getMessage());
                                                                                            // ⬆️ Витік!
}
```
`e.getMessage()` може містити внутрішні деталі: SQL запити, шляхи до файлів, імена класів — що є **вразливістю безпеки**. В production `detailedMessage` має бути порожнім або взагалі не передаватись.

### 3. AccessDeniedException кидається не через Spring Security (Backend)
**Файл:** [`ItemService.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/item/ItemService.java#L94-L96)

```java
// updateItem — лінія 94-96
if (!item.getOwner().getId().equals(userId)) {
    throw new IllegalArgumentException("You are not the owner of this item"); // ❌ 400 замість 403!
}
```
`IllegalArgumentException` повертає HTTP **400 Bad Request**, а `AccessDeniedException` — **403 Forbidden**. Це семантично неправильно і заплутує клієнта.

### 4. Обхід ADMIN-перевірки через SecurityContext у Service (Backend)
**Файл:** [`ItemService.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/item/ItemService.java#L113-L115)

```java
// deleteItem — лінія 113-115
boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch("ROLE_ADMIN"::equals);
```
**Антипатерн:** Сервіс не повинен знати про SecurityContext — це порушує SRP. Якщо метод потрібен адміну, треба передавати `isAdmin` як параметр або використовувати `@PreAuthorize` на рівні контролера. Також `SecurityContextHolder.getContext()` може повернути `null` у тестах.

### 5. Некоректна валідація у setStatusForBooking (Backend)
**Файл:** [`BookingService.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/booking/BookingService.java#L156-L158)

```java
// Перевірка тільки на CANCELLED, але не на COMPLETED/REJECTED
if (booking.getStatus() == BookingStatus.CANCELLED) {
    throw new InvalidBookingStateException("Cannot change status of a cancelled booking");
}
```
Відсутня перевірка на `COMPLETED`, `REJECTED` — тобто завершені бронювання можна знову APPROVED або IN_PROGRESS. Потрібен повноцінний state machine.

### 6. Токени зберігаються у localStorage (Frontend)
**Файл:** [`axios.ts`](file:///c:/Users/Admin/work_projects/WTeam/frontend/src/api/axios.ts)

`localStorage` вразливий до **XSS атак**. Для production-додатку access token краще зберігати в пам'яті (`useState`), а refresh token — у `httpOnly cookie`. Це стандартна практика.

### 7. Stripe webhook не захищений від повторних запитів (Backend)
**Файл:** [`PaymentService.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/payment/PaymentService.java#L119-L128)

```java
private void handleSuccessfulPayment(String sessionId) {
    Optional<Payment> paymentOpt = paymentRepository.findByProviderTransactionId(sessionId);
    if (paymentOpt.isPresent()) {
        Payment payment = paymentOpt.get();
        payment.setStatus(PaymentStatus.SUCCESS); // ❌ немає ідемпотентності
```
Якщо Stripe надішле webhook двічі (що він робить при помилках), платіж може бути оброблений двічі. Потрібна перевірка на `status != SUCCESS` перед обробкою.

---

## 🟠 АРХІТЕКТУРНІ ПРОБЛЕМИ

### 8. Dispute модуль — "мертвий" код
**Файл:** [`DisputeService.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/dispute/DisputeService.java)

`DisputeService` — порожній клас. `Dispute` сутність є, репозиторій є, але жодної бізнес-логіки і **жодного контролера**. Статус `DISPUTE` в `BookingStatus` вже присутній на фронтенді, але нічого не робить. Або потрібно реалізувати, або прибрати.

### 9. Фільтрація і пагінація відбувається на фронтенді (MyBookingsPage)
**Файл:** [`MyBookingsPage.tsx`](file:///c:/Users/Admin/work_projects/WTeam/frontend/src/pages/MyBookingsPage.tsx#L54-L56)

```typescript
// Завантажується ВСЕ одразу — 100 елементів!
const fetchMethod = activeTab === 'renter'
    ? bookingsApi.getMyBookings(0, 100)   // ← 100 bookings
    : bookingsApi.getOwnerBookings(0, 100);
```
Потім у `filteredBookings` і `paginatedBookings` — клієнтська пагінація та фільтрація. Це **анти-патерн**: при великій кількості бронювань — неефективно, повільно. Фільтрацію по статусу слід передати на бекенд як `?status=PENDING`.

### 10. Пошук на BrowsePage — клієнтський, але API підтримує серверний
**Файл:** [`BrowsePage.tsx`](file:///c:/Users/Admin/work_projects/WTeam/frontend/src/pages/BrowsePage.tsx#L48-L57)

```typescript
const filtered = items.filter(item => {
    const matchesSearch = !q || item.title.toLowerCase().includes(q) || ...
    const matchesCity = ...
    const matchesCategory = ...
```
`ItemRepository.searchActiveByKeyword()` вже є, але фронтенд **ігнорує** його і фільтрує локально вже завантажену сторінку. При пагінації (наприклад, сторінка 2 з 5) пошук взагалі **не знаходить елементи з інших сторінок**.

### 11. Самовиклик через ObjectProvider — складно і непотрібно
**Файл:** [`BookingService.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/booking/BookingService.java#L43-L47)

```java
private final ObjectProvider<BookingService> selfProvider;
private BookingService getSelf() { return selfProvider.getObject(); }
```
Це використовується для того, щоб `@CacheEvict` на `setStatusForBooking` спрацював при виклику з того ж класу. Альтернативи простіші: виокремити кешований метод в окремий `BookingCacheService`, або використати `@CachePut`, або взагалі переглянути доцільність такого кешування.

### 12. AI сервіс — завжди завантажує 50 items (Backend)
**Файл:** [`AiSessionService.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/ai_session/AiSessionService.java#L40-L42)

```java
List<Item> availableItems = itemRepository
    .findAllByStatusAndIsVerifiedTrue(RentingStatus.AVAILABLE, PageRequest.of(0, 50))
    .getContent();
```
При великій кількості товарів перші 50 за умовчанням (без сортування за релевантністю) можуть бути не найкращим вибором. Потрібне попереднє векторне similarity-search або хоча б сортування за рейтингом.

### 13. RestClient без пулу з'єднань (Backend)
**Файл:** [`AiSessionService.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/ai_session/AiSessionService.java#L107)

```java
RestClient restClient = RestClient.create(); // ← новий на кожен запит!
```
`RestClient` створюється на кожен виклик `callOpenAi`. Це неефективно — треба зробити його `@Bean` або `static final`.

### 14. Kafka є, але майже не використовується
**Файл:** [`kafka/KafkaConstants.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/kafka/KafkaConstants.java)

Kafka підключена, але в `KafkaConstants` лише константи без продюсерів/консьюмерів. Notifications відправляються через `ApplicationEventPublisher` (синхронно). Або Kafka треба використовувати для notification async pipeline, або прибрати (Kafka додає складності і ресурси).

### 15. Депозит розраховується помилково
**Файл:** [`Booking.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/booking/Booking.java#L113)

```java
this.depositTotal = depositPerDay.multiply(totalDays); // ← депозит * кількість днів?
```
Зазвичай депозит — це **фіксована** сума, а не per-day. Назва поля в `Item` — `depositAmount` (не `depositPerDay`), але множиться на кількість днів. Потрібно уточнити бізнес-логіку.

---

## 🟡 ПРОБЛЕМИ ПРОДУКТИВНОСТІ

### 16. N+1 проблема в MyBookingsPage
**Файл:** [`MyBookingsPage.tsx`](file:///c:/Users/Admin/work_projects/WTeam/frontend/src/pages/MyBookingsPage.tsx#L60-L68)

```typescript
const bookingsWithItems = await Promise.all(
    data.content.map(async (booking) => {
        const item = await itemsApi.getItemById(booking.itemId); // N окремих запитів!
    })
);
```
При 100 бронюваннях — **100 HTTP запитів**! Бекенд повинен повертати `BookingResponse` вже з вбудованим `ItemSummary` (назва, фото, місто). Або хоча б batch endpoint.

### 17. Кеш items інвалідується повністю при будь-якому update
```java
@CacheEvict(value = "items", key = "#itemId")
```
При кожному оновленні item, кеш саме цього item прибирається — це нормально. Але `getAllItemsWhichAreAvailable` не кешується зовсім, хоча це найчастіший запит (головна сторінка). Варто додати `@Cacheable` для списку доступних товарів з коротким TTL.

### 18. Відсутня lazy loading зображень
**Файл:** [`BrowsePage.tsx`](file:///c:/Users/Admin/work_projects/WTeam/frontend/src/pages/BrowsePage.tsx#L119-L125)

```tsx
<img src={...} alt={item.title} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
```
Немає `loading="lazy"` на зображеннях. При 12 карточках на сторінці — завантажуються всі зображення одразу.

### 19. WebSocket конфігурація без Nginx підтримки
**Файл:** [`nginx.conf`](file:///c:/Users/Admin/work_projects/WTeam/nginx/nginx.conf)

Nginx не проксіює WebSocket (`/ws/**`) — відсутні заголовки `Upgrade` та `Connection`. WebSocket з'єднання чатів можуть не працювати через Nginx в production.

```nginx
# Відсутнє:
location /ws/ {
    proxy_pass http://backend;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
}
```

---

## 🔵 ПРОБЛЕМИ АРХІТЕКТУРИ ФРОНТЕНДУ

### 20. LanguageContext.tsx занадто великий
**Файл:** [`LanguageContext.tsx`](file:///c:/Users/Admin/work_projects/WTeam/frontend/src/contexts/LanguageContext.tsx) — **47 733 байти!**

Весь i18n словник захардкоджений в одному файлі. Це порушує принцип розділення відповідальностей. Треба:
- Виокремити переклади в JSON файли (`locales/ua.json`, `locales/en.json`)
- Використати бібліотеку `react-i18next` або аналог
- Або зберігати у окремих `constants/translations/` файлах

### 21. Компоненти-гіганти без декомпозиції
- [`AdminPage.tsx`](file:///c:/Users/Admin/work_projects/WTeam/frontend/src/pages/AdminPage.tsx) — 610 рядків, 5 "вкладених компонентів" в одному файлі
- [`MyBookingsPage.tsx`](file:///c:/Users/Admin/work_projects/WTeam/frontend/src/pages/MyBookingsPage.tsx) — 560 рядків

Кожен "tab" компонент в AdminPage (`StatsTab`, `UsersTab` тощо) варто винести у `pages/admin/` або `components/admin/`.

### 22. Відсутній error boundary
У React 19 є вбудовані можливості для error boundaries. Жоден компонент не обгорнутий у `<ErrorBoundary>`, тому некритична помилка в одному компоненті може "вбити" весь додаток.

### 23. `confirm()` і `prompt()` замість модальних вікон
**Файл:** [`AdminPage.tsx`](file:///c:/Users/Admin/work_projects/WTeam/frontend/src/pages/AdminPage.tsx)

```typescript
const reason = prompt(t('admin.usersPromptBlockReason')); // ← нативний prompt()
if (confirm(t('admin.usersConfirmDelete').replace('{email}', u.email))) // ← confirm()
```
`prompt()` та `confirm()` — застарілі браузерні діалоги, що:
- Блокують UI thread
- Не підтримують кастомні стилі
- На мобільних пристроях виглядають жахливо
- Можуть бути заблоковані браузером

### 24. Змішування Tailwind і кастомного CSS
**Файли:** [`index.css`](file:///c:/Users/Admin/work_projects/WTeam/frontend/src/index.css) (38 897 байт), [`MyBookingsPage.tsx`](file:///c:/Users/Admin/work_projects/WTeam/frontend/src/pages/MyBookingsPage.tsx)

```tsx
// Tailwind класи:
className="flex flex-col gap-2 mt-3 w-full"
// Кастомний CSS:
className="btn btn-primary btn-sm flex-1 text-center justify-center"
```
Два підходи використовуються паралельно. Це ускладнює підтримку. Треба обрати один підхід і дотримуватись його.

### 25. Hardcoded тексти змішані з i18n
```tsx
// AdminPage.tsx — hardcoded:
<span>Сторінка {page + 1} з {total}</span>
// MyBookingsPage.tsx — hardcoded:
"Оплатити (Stripe)"
// ItemDetailPage.tsx — hardcoded детектор мови:
new Date(review.createdAt).toLocaleDateString(t('nav.catalog') === 'Каталог' ? 'uk-UA' : 'en-US')
```

### 26. Redux використовується тільки для Auth, і то неправильно
Store має тільки один `authSlice`, але він не підключений до реального flow. Або треба **повністю перейти на Redux** (і прибрати `AuthContext`), або **повністю прибрати Redux** та залишити Context API.

---

## 🟢 ЩО МОЖНА ДОДАТИ

### Нові функції

1. **Реальна система диспутів** — `DisputeService` вже підготований, але порожній. Додати:
   - Форма подачі диспуту (фронтенд + ендпоінт)
   - Статус `DISPUTE` у бронюванні вже є, треба тільки логіку
   - Панель модератора для розгляду диспутів

2. **Серверний пошук та фільтрація** — `ItemRepository.searchActiveByKeyword()` вже є, але фронтенд не використовує. Підключити через `?keyword=`, `?city=`, `?categoryId=`, `?minPrice=`, `?maxPrice=`.

3. **Улюблені оголошення (Wishlist)** — базова функція маркетплейсу, якої немає.

4. **Система сповіщень на фронтенді** — `Notification` сутність є на бекенді, `NotificationController` є, але фронтенд не показує сповіщення в реальному часі (немає badge на іконці, немає дропдауну).

5. **Email-верифікація при реєстрації** — зараз користувач реєструється без підтвердження email. `spring-boot-starter-mail` вже є в залежностях.

6. **Карта (Map View)** — `latitude`/`longitude` в `Item` вже збираються, але ніде не відображаються. Можна додати Leaflet/Google Maps.

7. **Порівняння товарів** — для маркетплейсу корисна функція.

8. **Автоматичний перехід статусу бронювання** — scheduled job: `APPROVED → IN_PROGRESS` при настанні `startDate`, `IN_PROGRESS → COMPLETED` при `endDate + 1`.

9. **Refresh token rotation** — зараз при refresh старий refresh token залишається. Краще генерувати новий на кожен refresh (sliding window).

10. **Rate limiting для AI ендпоінту** — OpenAI API коштує гроші. Обмежити кількість запитів на день від одного юзера/IP.

---

## 🏗️ АРХІТЕКТУРНІ ЗМІНИ

### Що варто зробити

1. **Прибрати або використати Kafka** — або реалізувати async notification pipeline через Kafka (producer в `BookingService`, consumer в `NotificationService`), або прибрати Kafka і залишити `ApplicationEventPublisher`. Kafka без використання — зайвий Docker container.

2. **Service Interface pattern** — `ItemService` не має інтерфейсу. Для тестування і заміни реалізацій бажано: `ItemService` (interface) + `ItemServiceImpl`.

3. **BookingResponse повинен містити ItemSummary** — замість N+1 запитів з фронтенду, `BookingMapper` повинен маппити базову інформацію про Item безпосередньо у `BookingResponse`.

4. **Окремий `BookingStateMachine`** — логіка переходів станів розкидана по методах (`approveBooking`, `rejectBooking`, `cancelBooking`). Виокремити у `BookingStateMachine` або використати Spring State Machine.

5. **`@Transactional` на рівні сервісу, не контролера** — всі методи сервісу вже мають `@Transactional`, але деякі методи контролера викликають кілька сервісних методів без загальної транзакції.

6. **OpenAPI spec версія** — `openApiVersion = "3.0.0"` — поточна специфікація вже 3.1.0.

7. **Frontend: розділити API шар** — замість `api/axios.ts` що містить і конфіг і переклад помилок, зробити:
   - `api/client.ts` — базовий axios instance
   - `api/interceptors/authInterceptor.ts`
   - `api/interceptors/errorInterceptor.ts`

### Що варто прибрати

| Що | Чому |
|---|---|
| `authSlice.ts` (Redux) | Не використовується реально, дублює AuthContext |
| `Dispute` без контролера | Dead code — або реалізувати або прибрати |
| `org` директорія у backend | Схожа на тестові/зайві файли — перевірити |
| H2 в runtime | `runtimeOnly("com.h2database:h2")` — якщо тільки для тестів, перенести в `testRuntimeOnly` |
| Hardcoded `spring-boot-h2console` | Залежність в `implementation`, а не `developmentOnly` |

---

## 📋 ПРІОРИТЕТНИЙ ПЛАН ВИПРАВЛЕНЬ

### 🔴 Терміново (безпека/критичні баги)
1. Виправити витік `e.getMessage()` у production в `GlobalExceptionHandler`
2. Додати ідемпотентність у `handleSuccessfulPayment` (Stripe webhook)
3. Виправити HTTP коди помилок в `ItemService` (403 vs 400)
4. Прибрати `SecurityContextHolder` з `ItemService`
5. Додати WebSocket проксі у Nginx

### 🟠 Важливо (архітектура/продуктивність)
6. Прибрати N+1 у `MyBookingsPage` — додати `ItemSummary` у `BookingResponse`
7. Перенести фільтрацію bookings на бекенд
8. Підключити серверний пошук у `BrowsePage`
9. Виправити `BookingStatus` state machine
10. `RestClient` у `AiSessionService` зробити singleton

### 🟡 Середньо (якість коду)
11. Об'єднати Auth state — вибрати Context або Redux, прибрати зайве
12. Розбити `AdminPage.tsx` на окремі файли
13. Перенести переклади у `locales/*.json`
14. Замінити `confirm()`/`prompt()` на UI модальні вікна
15. Додати `loading="lazy"` на зображення
16. Реалізувати або прибрати Dispute систему

---

## 🔴 ДОДАТКОВІ КРИТИЧНІ ЗНАХІДКИ (Частина 2)

### 27. JwtAuthFilter — завантажує UserDetails з БД на кожен запит (Backend)
**Файл:** [`JwtAuthFilter.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/security/jwt/JwtAuthFilter.java#L76)

```java
// Кожен HTTP-запит → UserDetailsService → запит до БД!
UserDetails userDetails = userDetailsService.loadUserByUsername(username);
```

Це означає, що **кожен** автентифікований запит робить зайвий запит до PostgreSQL для перевірки JWT. Оскільки JWT — stateless токен, id та роль можна читати прямо з claims (вони вже є там — `id` і `role`). `userDetailsService.loadUserByUsername()` потрібен лише для отримання `UserDetails`, але якщо роль та id вже є в токені — можна будувати `SecurityUser` прямо з claims без БД-запиту.

```java
// Поточний підхід: завжди до БД
UserDetails userDetails = userDetailsService.loadUserByUsername(username);

// Краще: витягти id та role з JWT claims, не йти в БД
String userId = jwtService.extractClaim(token, c -> c.get("id", Long.class));
String role   = jwtService.extractClaim(token, c -> c.get("role", String.class));
// Побудувати Authentication безпосередньо з claims
```
Це **найбільше вузьке місце продуктивності** у всій системі безпеки.

### 28. Зберігається сира UUID як tokenHash (Backend)
**Файл:** [`RefreshTokenService.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/refresh_token/RefreshTokenService.java#L24) + [`RefreshToken.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/refresh_token/RefreshToken.java)

```java
String tokenHash = UUID.randomUUID().toString(); // ← назва HASH, але зберігається сирий UUID
```

Поле **називається** `tokenHash`, але в БД і в HTTP-відповіді зберігається **сирий UUID** без хешування. Якщо зловмисник отримає доступ до таблиці `refresh_tokens` — він одразу отримає валідні токени. Refresh токени треба хешувати (SHA-256) перед збереженням у БД, як і паролі. У клієнта — сирий токен, у БД — hash.

### 29. OAuth2 передає токени через GET-параметри URL (Backend)
**Файл:** [`OAuth2SuccessHandler.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/security/oauth2/OAuth2SuccessHandler.java#L68-L72)

```java
String targetUri = UriComponentsBuilder.fromUriString(redirectUri)
    .queryParam("accessToken", accessToken)   // ← токен у URL!
    .queryParam("refreshToken", refreshToken.getTokenHash())
    .build().toUriString();
```

Передача токенів через URL — **вразливість безпеки** (OWASP A02): токени потрапляють в:
- `Referer` заголовок при переходах
- Логи сервера (Nginx, Apache)
- Браузерну історію
- Аналітичні системи

Краща практика: використовувати `httpOnly` cookie або POST body.

### 30. WebSocket URL захардкоджений у фронтенді (Frontend)
**Файл:** [`ChatPage.tsx`](file:///c:/Users/Admin/work_projects/WTeam/frontend/src/pages/ChatPage.tsx#L48)

```typescript
const WS_URL = 'http://localhost:8080/api/v1/ws'; // ← завжди localhost!
```

Це **неробоче в production**. URL має братись з env змінної:
```typescript
const WS_URL = (import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api/v1') + '/ws';
```

### 31. ChatRoomService — тип `otherUserName` як ID рядком (Backend)
**Файл:** [`ChatRoomService.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/chat_room/ChatRoomService.java#L72-L73)

```java
String otherUserName = otherUser.getId() + ""; // ← "123" замість реального імені!
// якщо є профіль — використати ім'я (profile підтягується окремо при потребі)
```

Коментар `// якщо є профіль — використати ім'я` залишається невиконаним. У `ChatRoomResponse` поле `otherUserName` завжди буде числом (ID), не іменем. На фронтенді `ChatPage` показує `room?.otherUserName` — отже, у чаті замість "Іван Петренко" завжди буде "42". Це явний баг.

### 32. BookingRepository.item() — загадковий метод без реалізації (Backend)
**Файл:** [`BookingRepository.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/booking/BookingRepository.java#L56)

```java
Long item(Item item); // ← що це робить? Spring Data не зможе цього вирішити!
```

Цей метод не відповідає жодному Spring Data convention і не має `@Query`. Це **мертвий код** або помилка, яка може призвести до `UnsatisfiedDependencyException` при старті якщо Spring Data спробує його конфігурувати.

### 33. Тест `updateItem_whenUserIsOwner` потребує SecurityContext мок — ознака проблеми (Tests)
**Файл:** [`ItemServiceTest.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/test/java/com/wteam/backend/item/ItemServiceTest.java#L208-L212)

```java
// У тестах updateItem потрібно мокати SecurityContextHolder
Authentication authentication = mock(Authentication.class);
SecurityContext securityContext = mock(SecurityContext.class);
when(securityContext.getAuthentication()).thenReturn(authentication);
SecurityContextHolder.setContext(securityContext);
```

Це пряме підтвердження проблеми №4 (SecurityContext у Service-шарі). Якби `isAdmin` передавався як параметр — тест був би простішим і чистішим. Крім того, `SecurityContextHolder.setContext()` **не thread-safe у parallel test execution**.

### 34. Відсутня перевірка чи рентер не є власником (Backend)
**Файл:** [`BookingService.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/booking/BookingService.java#L61-L97)

```java
// createBooking — немає перевірки:
if (item.getOwner().getId().equals(renterId)) {
    throw new IllegalArgumentException("Cannot rent your own item");
}
```

Зараз власник речі може сам собі забронювати власну річ, що не має сенсу з бізнесової точки зору і може спричинити edge cases у notifications та статусах.

### 35. ChatPage завантажує всі rooms щоб знайти одну (Frontend)
**Файл:** [`ChatPage.tsx`](file:///c:/Users/Admin/work_projects/WTeam/frontend/src/pages/ChatPage.tsx#L27)

```typescript
chatApi.getMyRooms().then(rooms => rooms.find(r => r.id === id) ?? null)
```

Щоб отримати дані одного чату, завантажується **список всіх кімнат**, а потім фільтрується на клієнті. Треба додати `GET /chat-rooms/{roomId}` endpoint.

### 36. Enums `DeliveryMethod`, `DeliveryStatus`, `DocumentType` — unused (Backend)
**Файл:** [`common/enums/`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/common/enums)

Знайдено enum-файли: `DeliveryMethod.java`, `DeliveryStatus.java`, `DocumentType.java` — їх наявність разом з `booking_delivery` та `user_verification_request` модулями вказує на **недореалізовані фічі** (доставка, документна верифікація). Вони додають складність без користі.

### 37. JwtService — SecretKey перераховується на кожен виклик (Backend)
**Файл:** [`JwtService.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/security/jwt/JwtService.java#L155-L158)

```java
private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey); // ← декодується ЩОРАЗУ
    return Keys.hmacShaKeyFor(keyBytes);
}
```

`SecretKey` — незмінний об'єкт. Декодування Base64 і побудова ключа на кожен виклик `generateToken`/`isTokenValid` — зайва робота. Варто зробити `private final SecretKey signingKey` у `@PostConstruct`.

### 38. `filterChain` не кидає `throws Exception` — компілятор не перевірить (Backend)
**Файл:** [`SecurityConfig.java`](file:///c:/Users/Admin/work_projects/WTeam/backend/src/main/java/com/wteam/backend/security/SecurityConfig.java#L72)

```java
public SecurityFilterChain filterChain(HttpSecurity http) {
// ↑ немає `throws Exception` — метод мовчки ковтає checked exceptions
```

Повинно бути:
```java
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
```
IntelliJ/Qodana можуть помітити, але це потенційна прихована проблема.

---

## 🟡 ДОДАТКОВІ АРХІТЕКТУРНІ СПОСТЕРЕЖЕННЯ

### Тести: добра структура, але є gaps

**Що є:**
- Чудова структура: `ai_session`, `auth`, `booking`, `chat_room`, `item`, `security`, `user_profile` — покриття по модулях
- Testcontainers для PostgreSQL та Kafka — правильний підхід
- Redis test у `RedisTest.java`
- Spring REST Docs (`spring-restdocs-mockmvc`) підключений — але чи генерується документація?

**Що відсутнє:**
- Тести для `PaymentService` (Stripe webhook logic)
- Тести для `BookingService.setStatusForBooking` state machine
- Інтеграційні тести для `ItemController` (немає в `integration/`)
- `@ParameterizedTest` для граничних умов (дати, ціни)

### Notification система: гарна архітектура
`NotificationSender` інтерфейс + `email/` та `push/` реалізації — правильний Strategy pattern. `NotificationMessageGenerator` для формування тексту — теж правильно. Але:
- `channel` у `Notification` entity завжди `IN_APP` (жоден event не передає Email/Push)
- Push через Firebase є, але коли він реально тригериться?

### Refresh Token Rotation — правильно реалізовано
`processRefreshToken` видаляє старий токен і генерує новий — це **sliding window rotation**, що є security best practice. Але з огляду на проблему №28 (сирий UUID без хешу) — ефект нівелюється.

### `MODER` роль існує, але не використовується
```java
// Role.java
USER, MODER, ADMIN
```
На фронтенді в `AdminPage` є UI для призначення `MODER`, але в `SecurityConfig` немає жодного `.hasRole('MODER')`. Роль декларована, але без жодної функціональності.

---

## 🟢 ДОДАТКОВІ ПРОПОЗИЦІЇ НОВИХ ФУНКЦІЙ

### Короткострокові (легко реалізувати на базі існуючого)

| Фіча | Що вже є | Що треба додати |
|------|----------|------------------|
| **Сповіщення в UI** | `Notification` entity + Controller | Bell icon у Navbar, polling або SSE |
| **Серверний пошук** | `searchActiveByKeyword()` | Передати параметри з BrowsePage |
| **Роль Модератора** | `MODER` в enum | Додати `hasRole('MODER')` до верифікаційних ендпоінтів |
| **Власна річ не в оренду** | — | Перевірка `owner == renter` у createBooking |
| **GET /chat-rooms/{id}** | `getAndCheckAccess()` вже є | Додати ендпоінт в контролер |
| **Автозавершення оренди** | `@Scheduled` + Flyway | `@Scheduled` job для автоматичного статусу |

### Середньострокові

1. **Wishlist/Favorites** — `user_favorites` таблиця, простий `ManyToMany`
2. **Система доставки** — `booking_delivery` модуль вже є (хоч і порожній), `DeliveryStatus/DeliveryMethod` enum теж
3. **Рейтинг власника** — агрегатний розрахунок з `user_review` таблиці, зберігати у `UserProfile.ownerTrustScore`
4. **Notifications realtime** — Server-Sent Events (SSE) endpoint `/notifications/stream` для live notifications без WebSocket
5. **Serializacja filtrów** — URL-based стан фільтрів (`?city=Kyiv&cat=5&search=tent`) для share-friendly посилань

### Довгострокові

1. **Dispute система** — `Dispute` entity + `DisputeService` + модераторська панель
2. **Повна доставка** — інтеграція з Nova Poshta / Ukrposhta API
3. **Мобільний застосунок** — API вже готовий, Firebase Admin SDK підключений
4. **Векторний пошук для AI** — PostgreSQL `pgvector` extension для similarity search замість завантаження 50 items
5. **Аудит лог** — Spring Data Envers або власна таблиця audit_logs для відстеження змін

---

## ✅ Що зроблено добре

- **Модульна структура** бекенду — кожен домен в окремому пакеті з чітким розмежуванням
- **Pessimistic locking** при створенні бронювання (`findByIdForUpdate`) — захист від race conditions
- **JWT refresh token** flow з дедуплікацією на фронтенді (один Promise для паралельних запитів)
- **MapStruct** для маппінгу — правильний підхід
- **Custom Gradle plugin** для TodoPlugin — зручний інструмент для команди
- **OpenAPI документація** — Swagger UI підключений
- **Bucket4j** для rate limiting — підключений (хоча варто перевірити конфіг)
- **Cloudinary** для зберігання зображень — правильний вибір
- **Flyway** для міграцій бази — правильний підхід
- **Price snapshot** у Booking — збереження ціни на момент бронювання — хороша практика
- **Availability calendar** на ItemDetailPage — якісний UX
- **React Hot Toast** для нотифікацій — правильний вибір
- **OAuth2** (Google/GitHub) — підключений через Spring Security
