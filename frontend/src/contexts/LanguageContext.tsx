import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';

export type Language = 'ua' | 'en';

type TranslationDictionary = Record<string, string>;

export const translations: Record<Language, TranslationDictionary> = {
    ua: {
        // Navbar
        'nav.catalog': 'Каталог',
        'nav.aiHelper': '✨ AI Помічник',
        'nav.chats': 'Чати',
        'nav.myBookings': 'Мої бронювання',
        'nav.addListing': 'Додати оголошення',
        'nav.profile': 'Профіль',
        'nav.admin': 'Адмін',
        'nav.logout': 'Вийти',
        'nav.login': 'Увійти',
        'nav.register': 'Реєстрація',

        // BrowsePage
        'browse.searchPlaceholder': 'Пошук за назвою, описом або тегами…',
        'browse.cityPlaceholder': 'Місто…',
        'browse.all': 'Усі',
        'browse.loadingError': 'Помилка завантаження товарів.',
        'browse.noItems': 'Наразі немає доступних товарів. Завітайте пізніше!',
        'browse.notFound': 'Нічого не знайдено за вашим запитом.',
        'browse.heroSub': 'Переглядайте тисячі товарів, доступних для оренди поруч з вами.',
        'browse.priceUnitDay': '/день',
        'browse.priceUnitWeek': '/тиждень',
        'browse.prev': '← Назад',
        'browse.next': 'Вперед →',
        'browse.pageInfo': 'Сторінка {page} з {total}',

        // Conditions
        'condition.IDEAL': 'Ідеальний',
        'condition.GOOD': 'Хороший',
        'condition.NORM': 'Нормальний',
        'condition.BAD': 'Задовільний',
        'condition.NEEDS_REPAIRING': 'Потребує ремонту',

        // Booking Statuses
        'bookingStatus.PENDING': 'Очікує підтвердження',
        'bookingStatus.APPROVED': 'Підтверджено',
        'bookingStatus.REJECTED': 'Відхилено',
        'bookingStatus.PAID': 'Оплачено',
        'bookingStatus.IN_PROGRESS': 'В процесі',
        'bookingStatus.COMPLETED': 'Завершено',
        'bookingStatus.CANCELLED': 'Скасовано',
        'bookingStatus.DISPUTE': 'Суперечка',

        // Profile Verification Statuses
        'verificationStatus.UNVERIFIED': 'Не верифіковано',
        'verificationStatus.PENDING': 'На перевірці',
        'verificationStatus.VERIFIED': 'Верифіковано',
        'verificationStatus.REJECTED': 'Відхилено',

        // LoginPage
        'login.title': 'Вхід до акаунту',
        'login.email': 'Email адреса',
        'login.password': 'Пароль',
        'login.submit': 'Увійти',
        'login.noAccount': 'Немає акаунту?',
        'login.registerLink': 'Зареєструватися',
        'login.error': 'Помилка авторизації. Перевірте введені дані.',

        // RegisterPage
        'register.title': 'Реєстрація',
        'register.email': 'Email адреса',
        'register.password': 'Пароль',
        'register.firstName': 'Ім\'я',
        'register.lastName': 'Прізвище',
        'register.submit': 'Зареєструватися',
        'register.hasAccount': 'Вже маєте акаунт?',
        'register.loginLink': 'Увійти',
        'register.error': 'Помилка реєстрації. Можливо, цей email вже використовується.',

        // Create/Edit Item
        'itemForm.createTitle': 'Додати нове оголошення',
        'itemForm.editTitle': 'Редагувати оголошення',
        'itemForm.title': 'Назва речі',
        'itemForm.description': 'Опис',
        'itemForm.category': 'Категорія',
        'itemForm.selectCategory': 'Оберіть категорію',
        'itemForm.condition': 'Стан',
        'itemForm.price': 'Ціна за день (₴)',
        'itemForm.deposit': 'Сума застави (₴)',
        'itemForm.city': 'Місто',
        'itemForm.address': 'Адреса',
        'itemForm.submitCreate': 'Створити оголошення',
        'itemForm.submitEdit': 'Зберегти зміни',
        'itemForm.successCreate': 'Оголошення успішно створено!',
        'itemForm.successEdit': 'Зміни збережено успішно!',
        'itemForm.error': 'Помилка збереження оголошення. Перевірте форму.',

        // ItemDetailPage
        'itemDetail.deposit': 'Застава',
        'itemDetail.condition': 'Стан',
        'itemDetail.price': 'Ціна',
        'itemDetail.owner': 'Власник',
        'itemDetail.address': 'Адреса',
        'itemDetail.description': 'Опис речі',
        'itemDetail.reviews': 'Відгуки',
        'itemDetail.noReviews': 'Відгуків про цей товар ще немає.',
        'itemDetail.bookTitle': 'Бронювання речі',
        'itemDetail.startDate': 'Дата початку',
        'itemDetail.endDate': 'Дата закінчення',
        'itemDetail.totalPrice': 'Загальна вартість',
        'itemDetail.submitBook': 'Забронювати зараз',
        'itemDetail.bookSuccess': 'Запит на бронювання успішно надіслано!',
        'itemDetail.bookError': 'Помилка створення бронювання.',
        'itemDetail.loginToBook': 'Увійдіть в акаунт, щоб забронювати.',
        'itemDetail.editBtn': 'Редагувати оголошення',
        'itemDetail.availability': '📅 Доступність',

        // ProfilePage
        'profile.title': 'Мій профіль',
        'profile.firstName': 'Ім\'я',
        'profile.lastName': 'Прізвище',
        'profile.email': 'Email',
        'profile.verification': 'Статус верифікації',
        'profile.renterScore': 'Рейтинг орендаря',
        'profile.ownerScore': 'Рейтинг власника',
        'profile.rentsCount': 'Успішних оренд',
        'profile.save': 'Зберегти зміни',
        'profile.success': 'Профіль успішно оновлено!',
        'profile.error': 'Помилка оновлення профілю.',

        // MyBookingsPage
        'bookings.title': 'Мої бронювання',
        'bookings.tabRents': 'Я орендую',
        'bookings.tabOffers': 'Мої пропозиції',
        'bookings.id': 'Бронювання #',
        'bookings.item': 'Річ',
        'bookings.renter': 'Орендар',
        'bookings.owner': 'Власник',
        'bookings.dates': 'Дати',
        'bookings.total': 'Сума',
        'bookings.status': 'Статус',
        'bookings.actions': 'Дії',
        'bookings.actionPay': 'Оплатити',
        'bookings.actionCancel': 'Скасувати',
        'bookings.actionApprove': 'Підтвердити',
        'bookings.actionReject': 'Відхилити',
        'bookings.actionComplete': 'Завершити',
        'bookings.actionReview': 'Залишити відгук',
        'bookings.noRents': 'Ви ще нічого не бронювали.',
        'bookings.noOffers': 'Ваші речі ще ніхто не бронював.',
        'bookings.statusSuccess': 'Статус успішно оновлено!',

        // ChatsPage / ChatPage
        'chats.title': 'Мої повідомлення',
        'chats.noChats': 'У вас ще немає активних діалогів.',
        'chats.placeholder': 'Напишіть повідомлення...',
        'chats.send': 'Надіслати',
        'chats.about': 'Річ:',

        // AiPage
        'ai.title': 'AI Помічник з оренди',
        'ai.placeholder': 'Спитай мене: "Де знайти намет?" або "Порадь дриль для ремонту"...',
        'ai.send': 'Запитати',
        'ai.recommendations': 'Рекомендовані речі для вас:',
        'ai.error': 'Помилка зв\'язку з AI помічником.',

        // ReviewModal
        'review.title': 'Залишити відгук',
        'review.rating': 'Рейтинг (1-5)',
        'review.comment': 'Коментар',
        'review.submit': 'Надіслати відгук',
        'review.success': 'Відгук успішно залишено!',
        'review.error': 'Помилка відправки відгуку.',
        'review.selectRatingError': 'Будь ласка, оберіть оцінку (від 1 до 5 зірок).',
        'review.rateItem': 'Оцінити товар',
        'review.rateRenter': 'Оцінити орендаря',
        'review.itemRating': 'Оцінка товару',
        'review.renterRating': 'Оцінка орендаря',
        'review.commentLabel': 'Ваш коментар',
        'review.placeholder': 'Поділіться вашими враженнями від оренди...',
        'review.cancel': 'Скасувати',
        'review.submitting': 'Відправка...',
        'review.close': 'Закрити',

        // ItemDetailPage extra
        'itemDetail.notFound': 'Товар не знайдено.',
        'itemDetail.deleteConfirm': 'Видалити цей товар? Цю дію неможливо скасувати.',
        'itemDetail.deleteError': 'Не вдалося видалити товар.',
        'itemDetail.deleteBtn': 'Видалити оголошення',
        'itemDetail.verified': 'Верифіковано',
        'itemDetail.trust': 'Рейтинг',
        'itemDetail.successfulRents': '{count} успішних оренд',
        'itemDetail.ownerReviews': 'Відгуки про власника',
        'itemDetail.bookAndMessage': 'Забронювати та написати власнику',
        'itemDetail.selectDatesError': 'Оберіть дати оренди в календарі.',
        'itemDetail.perDay': 'за день',
        'itemDetail.perWeek': 'за тиждень',
        'itemDetail.tags': 'Теги',

        // Calendar
        'cal.available': 'Доступно',
        'cal.booked': 'Зайнято',
        'cal.selected': 'Вибрано',
        'cal.selectStart': 'Оберіть дату початку оренди',
        'cal.selectEnd': 'Початок: {start} — оберіть дату закінчення',
        'cal.depositLabel': 'депозит',

        // Create/Edit Item extra
        'itemForm.createSubtitle': 'Заповніть форму нижче, щоб додати ваш товар у каталог.',
        'itemForm.editSubtitle': 'Оновіть інформацію про "{title}".',
        'itemForm.somethingWentWrong': 'Щось пішло не так. Спробуйте ще раз.',
        'itemForm.titlePlaceholder': 'наприклад, Електродриль',
        'itemForm.descPlaceholder': 'Опишіть товар, його характеристики та правила використання...',
        'itemForm.tagsLabel': 'Теги (через кому)',
        'itemForm.tagsPlaceholder': 'намет, туризм, відпочинок',
        'itemForm.priceWeek': 'Ціна за тиждень (₴)',
        'itemForm.cityPlaceholder': 'Київ',
        'itemForm.addressPlaceholder': 'вул. Хрещатик, 1',
        'itemForm.latitude': 'Широта',
        'itemForm.longitude': 'Довгота',

        // LoginPage extra
        'login.subtitle': 'Увійдіть, щоб орендувати або здавати речі',
        'login.or': 'або',
        'login.googleSubmit': 'Увійти через Google',

        // RegisterPage extra
        'register.subtitle': 'Створіть акаунт і почніть орендувати',
        'register.passwordMismatch': 'Паролі не збігаються.',
        'register.passwordLength': 'Пароль має містити від 8 до 20 символів.',
        'register.passwordPlaceholder': '8–20 символів',
        'register.confirmPassword': 'Підтвердіть пароль',
        'register.confirmPasswordPlaceholder': 'повторіть пароль',
        'register.googleSubmit': 'Продовжити з Google',

        // MyBookingsPage extra
        'bookings.loadError': 'Не вдалося завантажити бронювання.',
        'bookings.subtitle': 'Перегляд статусу та управління вашими замовленнями',
        'bookings.tabRentsLabel': 'Мої оренди',
        'bookings.tabOffersLabel': 'Здано в оренду',
        'bookings.loading': 'Завантаження бронювань...',
        'bookings.noRentsPrompt': 'У вас ще немає жодних бронювань. Перейдіть до каталогу, щоб орендувати щось.',
        'bookings.noOffersPrompt': 'У вас ще немає замовлень на ваші товари.',
        'bookings.goToCatalog': 'Перейти до каталогу',
        'bookings.itemPlaceholder': 'Товар #{id}',
        'bookings.period': 'Період:',
        'bookings.depositLabel': 'Застава:',
        'bookings.totalUnit': ' загалом',
        'bookings.itemDetails': 'Деталі товару',
        'bookings.cancelTitle': 'Скасувати бронювання',
        'bookings.cancelReasonLabel': 'Вкажіть причину скасування (необов\'язково):',
        'bookings.cancelPlaceholder': 'Причина скасування...',
        'bookings.cancelConfirm': 'Підтвердити скасування',
        'bookings.cancelBack': 'Назад',
        'bookings.alertApproveSuccess': 'Бронювання успішно підтверджено!',
        'bookings.alertRejectSuccess': 'Бронювання відхилено!',
        'bookings.alertCancelSuccess': 'Бронювання успішно скасовано!',
        'bookings.alertCompleteSuccess': 'Бронювання успішно завершено!',
        'bookings.actionError': 'Не вдалося оновити статус бронювання.',
        'bookings.subTabAll': 'Всі',
        'bookings.subTabPending': 'На підтвердження',
        'bookings.subTabActive': 'В процесі',
        'bookings.subTabUpcoming': 'Майбутні',
        'bookings.subTabCompleted': 'Завершені',
        'bookings.subTabCancelled': 'Скасовані',
        'bookings.noPendingRentsPrompt': 'У вас немає бронювань, що очікують підтвердження.',
        'bookings.noActiveRentsPrompt': 'У вас немає активних оренд в процесі.',
        'bookings.noUpcomingRentsPrompt': 'У вас немає запланованих майбутніх оренди.',
        'bookings.noCompletedRentsPrompt': 'У вас немає завершених оренд.',
        'bookings.noCancelledRentsPrompt': 'У вас немає скасованих оренд.',
        'bookings.noPendingOffersPrompt': 'У вас немає пропозицій, що очікують підтвердження.',
        'bookings.noActiveOffersPrompt': 'У вас немає активних пропозицій в процесі.',
        'bookings.noUpcomingOffersPrompt': 'У вас немає запланованих майбутніх пропозицій.',
        'bookings.noCompletedOffersPrompt': 'У вас немає завершених пропозицій.',
        'bookings.noCancelledOffersPrompt': 'У вас немає скасованих пропозицій.',

        // ChatsPage extra
        'chats.loadError': 'Не вдалося завантажити чати.',
        'chats.subtitle': 'Переписка з орендодавцями та орендарями',
        'chats.startPrompt': 'Зробіть бронювання, щоб почати спілкування з власником.',
        'chats.browseListings': 'Переглянути оголошення',
        'chats.loadChatError': 'Не вдалося завантажити чат.',
        'chats.sendError': 'Не вдалося надіслати повідомлення.',
        'chats.defaultName': 'Чат',
        'chats.connected': 'Підключено',
        'chats.offline': 'Офлайн',
        'chats.emptyChat': 'Поки немає повідомлень. Напишіть першим!',

        // AiPage extra
        'ai.subtitle': 'Опишіть що вам потрібно — AI підбере відповідні речі для оренди',
        'ai.textareaPlaceholder': 'Наприклад: Планую похід у Карпати на 3 дні, що взяти?',
        'ai.example1': 'Що взяти в похід на вихідні?',
        'ai.example2': 'Потрібен інструмент для ремонту квартири',
        'ai.example3': 'Шукаю камеру для фотосесії',
        'ai.thinking': 'Думаю...',
        'ai.submit': '✨ Знайти',
        'ai.responseLabel': 'Відповідь AI',
        'ai.noRecommendations': 'AI не знайшов підходящих речей за вашим запитом.',
        'ai.rephrasePrompt': 'Спробуйте переформулювати або перегляньте каталог.',

        // ProfilePage extra
        'profile.loadError': 'Не вдалося завантажити профіль.',
        'profile.avatarSuccess': 'Аватар успішно оновлено!',
        'profile.avatarUnavailable': 'Завантаження аватара тимчасово недоступне.',
        'profile.avatarError': 'Не вдалося завантажити аватар.',
        'profile.verificationSuccess': 'Запит на верифікацію надіслано!',
        'profile.verificationError': 'Не вдалося надіслати запит на верифікацію.',
        'profile.changeAvatar': 'Змінити',
        'profile.middleName': 'По батькові',
        'profile.birthDate': 'Дата народження',
        'profile.phone': 'Телефон',
        'profile.bio': 'Про себе',
        'profile.bioPlaceholder': 'Розкажіть трохи про себе...',
        'profile.submitVerification': 'Надіслати на верифікацію',

        // AdminPage
        'admin.title': 'Панель адміністратора',
        'admin.tabStats': '📊 Статистика',
        'admin.tabUsers': '👤 Користувачі',
        'admin.tabItems': '📦 Оголошення',
        'admin.tabVerifications': '✅ Верифікація',
        'admin.tabCategories': '🗂 Категорії',
        'admin.tabBookings': '📅 Бронювання',
        
        'admin.statsLoadError': 'Не вдалося завантажити статистику.',
        'admin.statsUsers': 'Користувачів',
        'admin.statsItems': 'Оголошень',
        'admin.statsActiveRents': 'Активних оренд',
        'admin.statsCompletedRents': 'Завершених оренд',
        'admin.statsPopCategories': 'Популярні категорії',
        'admin.statsNoData': 'Немає даних.',
        
        'admin.usersLoadError': 'Не вдалося завантажити користувачів.',
        'admin.usersSearchPlaceholder': 'Пошук за email…',
        'admin.usersSearchBtn': 'Знайти',
        'admin.usersResetBtn': 'Скинути',
        'admin.usersNotFound': 'Користувача не знайдено.',
        'admin.actionFailed': 'Дію не виконано.',
        'admin.usersColId': 'ID',
        'admin.usersColEmail': 'Email',
        'admin.usersColName': 'Ім\'я',
        'admin.usersColRole': 'Роль',
        'admin.usersColAccount': 'Акаунт',
        'admin.usersColVerification': 'Верифікація',
        'admin.usersColActions': 'Дії',
        'admin.usersActive': 'Активний',
        'admin.usersBlocked': 'Заблокований',
        'admin.usersBlockReason': 'Причина: {reason}',
        'admin.usersActivateBtn': 'Активувати',
        'admin.usersBlockBtn': 'Заблокувати',
        'admin.usersDeleteBtn': 'Видалити',
        'admin.usersPromptBlockReason': 'Причина блокування:',
        'admin.usersConfirmDelete': 'Видалити користувача {email}?',
        
        'admin.itemsLoadError': 'Не вдалося завантажити оголошення.',
        'admin.itemsConfirmDelete': 'Видалити це оголошення?',
        'admin.itemsDeleteFailed': 'Не вдалося видалити.',
        'admin.itemsVerifyFailed': 'Не вдалося змінити статус верифікації.',
        'admin.itemsColId': 'ID',
        'admin.itemsColTitle': 'Назва',
        'admin.itemsColOwner': 'Власник',
        'admin.itemsColCity': 'Місто',
        'admin.itemsColStatus': 'Статус',
        'admin.itemsColVerification': 'Верифікація',
        'admin.itemsColPrice': 'Ціна/день',
        'admin.itemsYes': 'Так',
        'admin.itemsNo': 'Ні',
        'admin.itemsRevokeBtn': 'Скасувати',
        'admin.itemsApproveBtn': 'Схвалити',
        
        'admin.verifLoadError': 'Не вдалося завантажити запити.',
        'admin.verifNoRequests': 'Немає запитів на верифікацію.',
        'admin.verifColPhone': 'Телефон',
        'admin.verifColBirth': 'Дата народж.',
        'admin.verifColStatus': 'Статус',
        'admin.verifApproveBtn': 'Схвалити',
        'admin.verifRejectBtn': 'Відхилити',
        
        'admin.catLoadError': 'Не вдалося завантажити категорії.',
        'admin.catEditTitle': 'Редагувати категорію',
        'admin.catCreateTitle': 'Створити категорію',
        'admin.catNameLabel': 'Назва *',
        'admin.catSlugLabel': 'Slug *',
        'admin.catIconLabel': 'URL іконки',
        'admin.catParentLabel': 'Батьківська категорія',
        'admin.catNoParent': 'Немає (верхній рівень)',
        'admin.catSaveBtn': 'Зберегти',
        'admin.catCancelBtn': 'Скасувати',
        'admin.catSaveFailed': 'Не вдалося зберегти.',
        'admin.catConfirmDelete': 'Видалити категорію?',
        'admin.catColSubcats': 'Підкатегорії',
        'admin.catNewBtn': '+ Нова категорія',
        
        'admin.bookLoadError': 'Не вдалося завантажити бронювання.',
        'admin.bookNoData': 'Бронювань поки немає.',
        'admin.bookColItem': 'Оголошення',
        'admin.bookColRenter': 'Орендар',
        'admin.bookColPeriod': 'Період',
        'admin.bookColTotal': 'Сума',
        'admin.bookPromptCancel': 'Причина скасування:',
        'admin.bookStatusFailed': 'Не вдалося змінити статус.',
        
        'admin.catEditBtn': 'Редагувати',
        
        'errors.serverDown': 'Сервер недоступний. Переконайтеся, що бекенд працює.',
        'errors.profileIncomplete': 'Ваш профіль не верифіковано або не заповнено повністю. Будь ласка, заповніть дані та пройдіть верифікацію для створення оголошення.',
        'errors.requiredField': 'Будь ласка, заповніть це поле.',
        'errors.badCredentials': 'Невірний логін або пароль.',
        'errors.userNotFound': 'Користувача не знайдено.',
        'errors.emailTaken': 'Користувач з таким email вже існує.',
        'errors.accessDenied': 'У вас немає прав для виконання цієї дії.',
        'errors.itemNotFound': 'Оголошення не знайдено.',
        'errors.bookingNotFound': 'Бронювання не знайдено.',
        'errors.sessionExpired': 'Сесія закінчилася. Будь ласка, увійдіть знову.',
    },
    en: {
        // Navbar
        'nav.catalog': 'Catalog',
        'nav.aiHelper': '✨ AI Assistant',
        'nav.chats': 'Chats',
        'nav.myBookings': 'My Bookings',
        'nav.addListing': 'Add Listing',
        'nav.profile': 'Profile',
        'nav.admin': 'Admin',
        'nav.logout': 'Logout',
        'nav.login': 'Login',
        'nav.register': 'Register',

        // BrowsePage
        'browse.searchPlaceholder': 'Search by title, description or tags...',
        'browse.cityPlaceholder': 'City...',
        'browse.all': 'All',
        'browse.loadingError': 'Failed to load items.',
        'browse.noItems': 'No items available right now. Check back soon!',
        'browse.notFound': 'Nothing found matching your query.',
        'browse.heroSub': 'Browse thousands of items available for rent near you.',
        'browse.priceUnitDay': '/day',
        'browse.priceUnitWeek': '/week',
        'browse.prev': '← Prev',
        'browse.next': 'Next →',
        'browse.pageInfo': 'Page {page} of {total}',

        // Conditions
        'condition.IDEAL': 'Ideal',
        'condition.GOOD': 'Good',
        'condition.NORM': 'Normal',
        'condition.BAD': 'Fair',
        'condition.NEEDS_REPAIRING': 'Needs Repair',

        // Booking Statuses
        'bookingStatus.PENDING': 'Pending Approval',
        'bookingStatus.APPROVED': 'Approved',
        'bookingStatus.REJECTED': 'Rejected',
        'bookingStatus.PAID': 'Paid',
        'bookingStatus.IN_PROGRESS': 'In Progress',
        'bookingStatus.COMPLETED': 'Completed',
        'bookingStatus.CANCELLED': 'Cancelled',
        'bookingStatus.DISPUTE': 'Dispute',

        // Profile Verification Statuses
        'verificationStatus.UNVERIFIED': 'Unverified',
        'verificationStatus.PENDING': 'Pending Verification',
        'verificationStatus.VERIFIED': 'Verified',
        'verificationStatus.REJECTED': 'Rejected',

        // LoginPage
        'login.title': 'Account Login',
        'login.email': 'Email Address',
        'login.password': 'Password',
        'login.submit': 'Login',
        'login.noAccount': 'Don\'t have an account?',
        'login.registerLink': 'Sign Up',
        'login.error': 'Authentication failed. Please check your credentials.',

        // RegisterPage
        'register.title': 'Registration',
        'register.email': 'Email Address',
        'register.password': 'Password',
        'register.firstName': 'First Name',
        'register.lastName': 'Last Name',
        'register.submit': 'Register',
        'register.hasAccount': 'Already have an account?',
        'register.loginLink': 'Login',
        'register.error': 'Registration failed. This email might already be registered.',

        // Create/Edit Item
        'itemForm.createTitle': 'Create New Listing',
        'itemForm.editTitle': 'Edit Listing',
        'itemForm.title': 'Item Title',
        'itemForm.description': 'Description',
        'itemForm.category': 'Category',
        'itemForm.selectCategory': 'Select Category',
        'itemForm.condition': 'Condition',
        'itemForm.price': 'Price per day (₴)',
        'itemForm.deposit': 'Deposit amount (₴)',
        'itemForm.city': 'City',
        'itemForm.address': 'Address',
        'itemForm.submitCreate': 'Create Listing',
        'itemForm.submitEdit': 'Save Changes',
        'itemForm.successCreate': 'Listing successfully created!',
        'itemForm.successEdit': 'Changes saved successfully!',
        'itemForm.error': 'Failed to save listing. Please check the form.',

        // ItemDetailPage
        'itemDetail.deposit': 'Deposit',
        'itemDetail.condition': 'Condition',
        'itemDetail.price': 'Price',
        'itemDetail.owner': 'Owner',
        'itemDetail.address': 'Address',
        'itemDetail.description': 'Item Description',
        'itemDetail.reviews': 'Reviews',
        'itemDetail.noReviews': 'No reviews for this item yet.',
        'itemDetail.bookTitle': 'Rent this Item',
        'itemDetail.startDate': 'Start Date',
        'itemDetail.endDate': 'End Date',
        'itemDetail.totalPrice': 'Total Price',
        'itemDetail.submitBook': 'Book Now',
        'itemDetail.bookSuccess': 'Booking request sent successfully!',
        'itemDetail.bookError': 'Failed to create booking.',
        'itemDetail.loginToBook': 'Log in to book this item.',
        'itemDetail.editBtn': 'Edit Listing',
        'itemDetail.availability': '📅 Availability',

        // ProfilePage
        'profile.title': 'My Profile',
        'profile.firstName': 'First Name',
        'profile.lastName': 'Last Name',
        'profile.email': 'Email',
        'profile.verification': 'Verification Status',
        'profile.renterScore': 'Renter trust score',
        'profile.ownerScore': 'Owner trust score',
        'profile.rentsCount': 'Successful rents',
        'profile.save': 'Save Changes',
        'profile.success': 'Profile updated successfully!',
        'profile.error': 'Failed to update profile.',

        // MyBookingsPage
        'bookings.title': 'My Bookings',
        'bookings.tabRents': 'My Rents',
        'bookings.tabOffers': 'My Offers',
        'bookings.id': 'Booking #',
        'bookings.item': 'Item',
        'bookings.renter': 'Renter',
        'bookings.owner': 'Owner',
        'bookings.dates': 'Dates',
        'bookings.total': 'Total',
        'bookings.status': 'Status',
        'bookings.actions': 'Actions',
        'bookings.actionPay': 'Pay',
        'bookings.actionCancel': 'Cancel',
        'bookings.actionApprove': 'Approve',
        'bookings.actionReject': 'Reject',
        'bookings.actionComplete': 'Complete',
        'bookings.actionReview': 'Leave Review',
        'bookings.noRents': 'You have not rented anything yet.',
        'bookings.noOffers': 'Nobody has booked your items yet.',
        'bookings.statusSuccess': 'Status updated successfully!',

        // ChatsPage / ChatPage
        'chats.title': 'My Messages',
        'chats.noChats': 'You don\'t have any active chats yet.',
        'chats.placeholder': 'Type a message...',
        'chats.send': 'Send',
        'chats.about': 'Item:',

        // AiPage
        'ai.title': 'AI Rent Assistant',
        'ai.placeholder': 'Ask me: "Where can I find a tent?" or "Suggest a drill for repairs"...',
        'ai.send': 'Ask',
        'ai.recommendations': 'Recommended items for you:',
        'ai.error': 'Error communicating with AI assistant.',

        // ReviewModal
        'review.title': 'Leave a Review',
        'review.rating': 'Rating (1-5)',
        'review.comment': 'Comment',
        'review.submit': 'Submit Review',
        'review.success': 'Review submitted successfully!',
        'review.error': 'Failed to submit review.',
        'review.selectRatingError': 'Please choose a rating (from 1 to 5 stars).',
        'review.rateItem': 'Rate Item',
        'review.rateRenter': 'Rate Renter',
        'review.itemRating': 'Item Rating',
        'review.renterRating': 'Renter Rating',
        'review.commentLabel': 'Your Comment',
        'review.placeholder': 'Share your impressions of the rent...',
        'review.cancel': 'Cancel',
        'review.submitting': 'Sending...',
        'review.close': 'Close',

        // ItemDetailPage extra
        'itemDetail.notFound': 'Item not found.',
        'itemDetail.deleteConfirm': 'Delete this item? This cannot be undone.',
        'itemDetail.deleteError': 'Failed to delete item.',
        'itemDetail.deleteBtn': 'Delete Listing',
        'itemDetail.verified': 'Verified',
        'itemDetail.trust': 'Trust',
        'itemDetail.successfulRents': '{count} successful rents',
        'itemDetail.ownerReviews': 'Owner Reviews',
        'itemDetail.bookAndMessage': 'Book and message owner',
        'itemDetail.selectDatesError': 'Select rent dates in the calendar.',
        'itemDetail.perDay': 'per day',
        'itemDetail.perWeek': 'per week',
        'itemDetail.tags': 'Tags',

        // Calendar
        'cal.available': 'Available',
        'cal.booked': 'Booked',
        'cal.selected': 'Selected',
        'cal.selectStart': 'Select start date',
        'cal.selectEnd': 'Start: {start} — select end date',
        'cal.depositLabel': 'deposit',

        // Create/Edit Item extra
        'itemForm.createSubtitle': 'Fill in the details below to make your item available for rent.',
        'itemForm.editSubtitle': 'Update the details for "{title}".',
        'itemForm.somethingWentWrong': 'Something went wrong. Please try again.',
        'itemForm.titlePlaceholder': 'e.g. Electric Drill',
        'itemForm.descPlaceholder': 'Describe the item, its features, and any usage notes...',
        'itemForm.tagsLabel': 'Tags (comma-separated)',
        'itemForm.tagsPlaceholder': 'tent, tourism, outdoor',
        'itemForm.priceWeek': 'Price per week (₴)',
        'itemForm.cityPlaceholder': 'Kyiv',
        'itemForm.addressPlaceholder': '1 Khreshchatyk St',
        'itemForm.latitude': 'Latitude',
        'itemForm.longitude': 'Longitude',

        // LoginPage extra
        'login.subtitle': 'Log in to rent or list items',
        'login.or': 'or',
        'login.googleSubmit': 'Sign in with Google',

        // RegisterPage extra
        'register.subtitle': 'Create an account and start renting',
        'register.passwordMismatch': 'Passwords do not match.',
        'register.passwordLength': 'Password must be between 8 and 20 characters.',
        'register.passwordPlaceholder': '8–20 characters',
        'register.confirmPassword': 'Confirm Password',
        'register.confirmPasswordPlaceholder': 'repeat password',
        'register.googleSubmit': 'Continue with Google',

        // MyBookingsPage extra
        'bookings.loadError': 'Failed to load bookings.',
        'bookings.subtitle': 'View status and manage your orders',
        'bookings.tabRentsLabel': 'My Rents',
        'bookings.tabOffersLabel': 'Lent Out',
        'bookings.loading': 'Loading bookings...',
        'bookings.noRentsPrompt': 'You don\'t have any bookings yet. Go to the catalog to rent something.',
        'bookings.noOffersPrompt': 'You don\'t have any orders for your items yet.',
        'bookings.goToCatalog': 'Go to Catalog',
        'bookings.itemPlaceholder': 'Item #{id}',
        'bookings.period': 'Period:',
        'bookings.depositLabel': 'Deposit:',
        'bookings.totalUnit': ' total',
        'bookings.itemDetails': 'Item Details',
        'bookings.cancelTitle': 'Cancel Booking',
        'bookings.cancelReasonLabel': 'Please provide a reason for cancellation (optional):',
        'bookings.cancelPlaceholder': 'Reason for cancellation...',
        'bookings.cancelConfirm': 'Confirm Cancellation',
        'bookings.cancelBack': 'Back',
        'bookings.alertApproveSuccess': 'Booking successfully approved!',
        'bookings.alertRejectSuccess': 'Booking rejected!',
        'bookings.alertCancelSuccess': 'Booking successfully cancelled!',
        'bookings.alertCompleteSuccess': 'Booking successfully completed!',
        'bookings.actionError': 'Failed to update booking status.',
        'bookings.subTabAll': 'All',
        'bookings.subTabPending': 'Pending',
        'bookings.subTabActive': 'In Progress',
        'bookings.subTabUpcoming': 'Upcoming',
        'bookings.subTabCompleted': 'Completed',
        'bookings.subTabCancelled': 'Cancelled',
        'bookings.noPendingRentsPrompt': 'You have no bookings pending approval.',
        'bookings.noActiveRentsPrompt': 'You have no active rentals in progress.',
        'bookings.noUpcomingRentsPrompt': 'You have no upcoming rentals planned.',
        'bookings.noCompletedRentsPrompt': 'You have no completed rentals.',
        'bookings.noCancelledRentsPrompt': 'You have no cancelled rentals.',
        'bookings.noPendingOffersPrompt': 'You have no offers pending approval.',
        'bookings.noActiveOffersPrompt': 'You have no active offers in progress.',
        'bookings.noUpcomingOffersPrompt': 'You have no upcoming offers planned.',
        'bookings.noCompletedOffersPrompt': 'You have no completed offers.',
        'bookings.noCancelledOffersPrompt': 'You have no cancelled offers.',

        // ChatsPage extra
        'chats.loadError': 'Failed to load chats.',
        'chats.subtitle': 'Correspondence with landlords and renters',
        'chats.startPrompt': 'Make a booking to start a conversation with the owner.',
        'chats.browseListings': 'Browse Listings',
        'chats.loadChatError': 'Failed to load chat.',
        'chats.sendError': 'Failed to send message.',
        'chats.defaultName': 'Chat',
        'chats.connected': 'Connected',
        'chats.offline': 'Offline',
        'chats.emptyChat': 'No messages yet. Write first!',

        // AiPage extra
        'ai.subtitle': 'Describe what you need — AI will find suitable items to rent',
        'ai.textareaPlaceholder': 'For example: I am planning a 3-day hike in the Carpathians, what should I bring?',
        'ai.example1': 'What to pack for a weekend hike?',
        'ai.example2': 'Need a tool for apartment renovation',
        'ai.example3': 'Looking for a camera for a photoshoot',
        'ai.thinking': 'Thinking...',
        'ai.submit': '✨ Find',
        'ai.responseLabel': 'AI Response',
        'ai.noRecommendations': 'AI could not find suitable items for your request.',
        'ai.rephrasePrompt': 'Try rephrasing or browse the catalog.',

        // ProfilePage extra
        'profile.loadError': 'Failed to load profile.',
        'profile.avatarSuccess': 'Avatar updated successfully!',
        'profile.avatarUnavailable': 'Avatar upload is temporarily unavailable.',
        'profile.avatarError': 'Failed to upload avatar.',
        'profile.verificationSuccess': 'Verification request submitted!',
        'profile.verificationError': 'Failed to submit verification request.',
        'profile.changeAvatar': 'Change',
        'profile.middleName': 'Middle Name',
        'profile.birthDate': 'Date of Birth',
        'profile.phone': 'Phone',
        'profile.bio': 'Bio',
        'profile.bioPlaceholder': 'Tell us a little about yourself...',
        'profile.submitVerification': 'Submit for Verification',

        // AdminPage
        'admin.title': 'Admin Panel',
        'admin.tabStats': '📊 Statistics',
        'admin.tabUsers': '👤 Users',
        'admin.tabItems': '📦 Items',
        'admin.tabVerifications': '✅ Verifications',
        'admin.tabCategories': '🗂 Categories',
        'admin.tabBookings': '📅 Bookings',
        
        'admin.statsLoadError': 'Failed to load statistics.',
        'admin.statsUsers': 'Users',
        'admin.statsItems': 'Items',
        'admin.statsActiveRents': 'Active Rents',
        'admin.statsCompletedRents': 'Completed Rents',
        'admin.statsPopCategories': 'Popular Categories',
        'admin.statsNoData': 'No data.',
        
        'admin.usersLoadError': 'Failed to load users.',
        'admin.usersSearchPlaceholder': 'Search by email...',
        'admin.usersSearchBtn': 'Search',
        'admin.usersResetBtn': 'Reset',
        'admin.usersNotFound': 'User not found.',
        'admin.actionFailed': 'Action failed.',
        'admin.usersColId': 'ID',
        'admin.usersColEmail': 'Email',
        'admin.usersColName': 'Name',
        'admin.usersColRole': 'Role',
        'admin.usersColAccount': 'Account',
        'admin.usersColVerification': 'Verification',
        'admin.usersColActions': 'Actions',
        'admin.usersActive': 'Active',
        'admin.usersBlocked': 'Blocked',
        'admin.usersBlockReason': 'Reason: {reason}',
        'admin.usersActivateBtn': 'Activate',
        'admin.usersBlockBtn': 'Block',
        'admin.usersDeleteBtn': 'Delete',
        'admin.usersPromptBlockReason': 'Reason for blocking:',
        'admin.usersConfirmDelete': 'Delete user {email}?',
        
        'admin.itemsLoadError': 'Failed to load items.',
        'admin.itemsConfirmDelete': 'Delete this item?',
        'admin.itemsDeleteFailed': 'Failed to delete.',
        'admin.itemsVerifyFailed': 'Failed to change verification status.',
        'admin.itemsColId': 'ID',
        'admin.itemsColTitle': 'Title',
        'admin.itemsColOwner': 'Owner',
        'admin.itemsColCity': 'City',
        'admin.itemsColStatus': 'Status',
        'admin.itemsColVerification': 'Verification',
        'admin.itemsColPrice': 'Price/day',
        'admin.itemsYes': 'Yes',
        'admin.itemsNo': 'No',
        'admin.itemsRevokeBtn': 'Revoke',
        'admin.itemsApproveBtn': 'Approve',
        
        'admin.verifLoadError': 'Failed to load requests.',
        'admin.verifNoRequests': 'No verification requests.',
        'admin.verifColPhone': 'Phone',
        'admin.verifColBirth': 'Birth Date',
        'admin.verifColStatus': 'Status',
        'admin.verifApproveBtn': 'Approve',
        'admin.verifRejectBtn': 'Reject',
        
        'admin.catLoadError': 'Failed to load categories.',
        'admin.catEditTitle': 'Edit category',
        'admin.catCreateTitle': 'Create category',
        'admin.catNameLabel': 'Name *',
        'admin.catSlugLabel': 'Slug *',
        'admin.catIconLabel': 'Icon URL',
        'admin.catParentLabel': 'Parent Category',
        'admin.catNoParent': 'None (top level)',
        'admin.catSaveBtn': 'Save',
        'admin.catCancelBtn': 'Cancel',
        'admin.catSaveFailed': 'Failed to save.',
        'admin.catConfirmDelete': 'Delete category?',
        'admin.catColSubcats': 'Subcategories',
        'admin.catNewBtn': '+ New category',
        
        'admin.bookLoadError': 'Failed to load bookings.',
        'admin.bookNoData': 'No bookings yet.',
        'admin.bookColItem': 'Item',
        'admin.bookColRenter': 'Renter',
        'admin.bookColPeriod': 'Period',
        'admin.bookColTotal': 'Amount',
        'admin.bookPromptCancel': 'Cancellation reason:',
        'admin.bookStatusFailed': 'Failed to change status.',
        
        'admin.catEditBtn': 'Edit',
        
        'errors.serverDown': 'Server is unavailable. Please ensure the backend is running.',
        'errors.profileIncomplete': 'Your profile is incomplete or unverified. Please fill in your profile and get verified to create an item.',
        'errors.requiredField': 'Please fill out this field.',
        'errors.badCredentials': 'Invalid email or password.',
        'errors.userNotFound': 'User not found.',
        'errors.emailTaken': 'User with this email already exists.',
        'errors.accessDenied': 'You do not have permission to perform this action.',
        'errors.itemNotFound': 'Item not found.',
        'errors.bookingNotFound': 'Booking not found.',
        'errors.sessionExpired': 'Session expired. Please log in again.',
    }
};

interface LanguageContextValue {
    language: Language;
    setLanguage: (lang: Language) => void;
    t: (key: string, replacements?: Record<string, string | number>) => string;
}

const LanguageContext = createContext<LanguageContextValue | null>(null);

const STORAGE_KEY = 'rentgo-language';

export const LanguageProvider = ({ children }: { children: ReactNode }) => {
    const [language, setLanguageState] = useState<Language>(() => {
        const stored = localStorage.getItem(STORAGE_KEY);
        if (stored === 'ua' || stored === 'en') {
            return stored;
        }
        return 'ua';
    });

    const setLanguage = (lang: Language) => {
        setLanguageState(lang);
        localStorage.setItem(STORAGE_KEY, lang);
    };

    const t = (key: string, replacements?: Record<string, string | number>): string => {
        const dictionary = translations[language];
        let text = dictionary[key] || translations['ua'][key] || key;

        if (replacements) {
            Object.entries(replacements).forEach(([k, v]) => {
                text = text.replace(`{${k}}`, String(v));
            });
        }

        return text;
    };

    useEffect(() => {
        const handleInvalid = (e: Event) => {
            const target = e.target as HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement;
            if (target && target.validity && target.validity.valueMissing) {
                target.setCustomValidity(t('errors.requiredField'));
            }
        };
        const handleInput = (e: Event) => {
            const target = e.target as HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement;
            if (target && target.setCustomValidity) {
                target.setCustomValidity('');
            }
        };

        document.addEventListener('invalid', handleInvalid, true);
        document.addEventListener('input', handleInput, true);

        return () => {
            document.removeEventListener('invalid', handleInvalid, true);
            document.removeEventListener('input', handleInput, true);
        };
    }, [language]);

    return (
        <LanguageContext.Provider value={{ language, setLanguage, t }}>
            {children}
        </LanguageContext.Provider>
    );
};

export const useLanguage = () => {
    const ctx = useContext(LanguageContext);
    if (!ctx) throw new Error('useLanguage must be used within LanguageProvider');
    return ctx;
};
