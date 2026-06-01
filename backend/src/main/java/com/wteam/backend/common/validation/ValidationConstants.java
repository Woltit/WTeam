package com.wteam.backend.common.validation;

/**
 * Глобальний клас констант для валідації вхідних даних додатка.
 * <p>
 * Містить централізовані обмеження розмірів полів (constraints), тексти помилок (messages)
 * та регулярні вирази (regex), які використовуються як у DTO (для Jakarta Validation),
 * так і в JPA-сутностях для конфігурації схеми бази даних.
 * </p>
 */
public final class ValidationConstants {

    /**
     * Приватний конструктор для запобігання створенню екземплярів утилітарного класу.
     *
     * @throws UnsupportedOperationException якщо виконується спроба викликати конструктор через рефлексію.
     */
    private ValidationConstants() {
        throw new UnsupportedOperationException("ValidationConstants cannot be instantiated - utility class");
    }

    /**
     * Константи валідації, що стосуються облікових даних користувача (акаунта).
     */
    public static final class User {

        // --- Constraints ---

        /**
         * Максимально дозволена довжина електронної пошти.
         */
        public static final int EMAIL_MAX_LENGTH = 255;

        /**
         * Максимально дозволена довжина пароля користувача.
         */
        public static final int PASSWORD_MAX_LENGTH = 100;

        /**
         * Мінімально дозволена довжина пароля користувача для забезпечення безпеки.
         */
        public static final int PASSWORD_MIN_LENGTH = 8;

        // --- Messages ---

        /**
         * Повідомлення, якщо поле email порожнє.
         */
        public static final String EMAIL_BLANK_MSG = "Email cannot be blank";

        /**
         * Повідомлення, якщо формат email не відповідає стандарту.
         */
        public static final String EMAIL_INVALID_FORMAT = "Invalid email format";

        /**
         * Повідомлення, якщо поле пароля порожнє.
         */
        public static final String PASSWORD_BLANK_MSG = "Password cannot be blank";

        /**
         * Повідомлення, якщо довжина пароля не входить у дозволені ліміти.
         */
        public static final String PASSWORD_INVALID_FORMAT = "Password must be between " + PASSWORD_MIN_LENGTH + " and " + PASSWORD_MAX_LENGTH;
    }

    /**
     * Константи валідації, що стосуються персональних даних профілю користувача.
     */
    public static final class UserProfile {

        // --- Constraints ---

        /**
         * Максимально дозволена довжина для імені, прізвища та по батькові.
         */
        public static final int NAME_MAX_LENGTH = 100;

        /**
         * Максимальна довжина номера телефону за стандартом E.164 (включаючи знак +).
         */
        public static final int PHONE_NUMBER_LENGTH = 20;

        // --- Messages ---

        /**
         * Повідомлення, якщо прізвище порожнє.
         */
        public static final String LAST_NAME_BLANK_MSG = "Last name cannot be blank";

        /**
         * Повідомлення, якщо ім'я порожнє.
         */
        public static final String FIRST_NAME_BLANK_MSG = "First name cannot be blank";

        /**
         * Повідомлення, якщо дата народження не вказана під час верифікації профілю.
         */
        public static final String BIRTH_DATE_NULL_MSG = "Birth date cannot be null";

        /**
         * Повідомлення, якщо номер телефону порожній.
         */
        public static final String PHONE_NUMBER_BLANK_MSG = "Phone number cannot be blank";

        /**
         * Повідомлення, якщо номер телефону не відповідає міжнародному стандарту.
         */
        public static final String PHONE_REGEX_INVALID_MSG = "Invalid format of phone number";

        // --- Regex ---

        /**
         * Регулярний вираз для валідації номерів телефонів у міжнародному форматі E.164.
         * <p>
         * Приклад валідного номера: {@code +380971234567}.
         * Вимагає обов'язкову наявність знаку {@code +}, коду країни та від 10 до 14 цифр після нього.
         * </p>
         */
        public static final String PHONE_REGEX = "^\\+[1-9]\\d{10,14}$";
    }

    /**
     * The type Category.
     */
    public static final class Category {
        /**
         * The constant NAME_MAX_LENGTH.
         */
        public static final int NAME_MAX_LENGTH = 255;
        /**
         * The constant SLUG_MAX_LENGTH.
         */
        public static final int SLUG_MAX_LENGTH = 100;
    }

    /**
     * The type Refresh token.
     */
    public static final class RefreshToken {
        /**
         * The constant TOKEN_HASH_MAX_LENGTH.
         */
        public static final int TOKEN_HASH_MAX_LENGTH = 255;
    }

    /**
     * The type Item.
     */
    public static final class Item {
        /**
         * The constant TITLE_MAX_LENGTH.
         */
        public static final int TITLE_MAX_LENGTH = 100;
        /**
         * The constant PRICE_PRECISION.
         */
        public static final int PRICE_PRECISION = 10;
        /**
         * The constant PRICE_SCALE.
         */
        public static final int PRICE_SCALE = 2;
        /**
         * The constant PLACE_PRECISION.
         */
        public static final int PLACE_PRECISION = 9;
        /**
         * The constant PLACE_SCALE.
         */
        public static final int PLACE_SCALE = 6;
    }

    /**
     * The type Booking.
     */
    public static final class Booking {
        /**
         * The constant PRICE_PRECISION.
         */
        public static final int PRICE_PRECISION = 10;
        /**
         * The constant PRICE_SCALE.
         */
        public static final int PRICE_SCALE = 2;
    }



    public static final class Constraints {
        public static final int PASSWORD_MIN_LENGTH = 8;
        public static final int PASSWORD_MAX_LENGTH = 20;

        public static final String PASSWORD_INVALID_MSG = "Password must be between " + PASSWORD_MIN_LENGTH + " and " + PASSWORD_MAX_LENGTH;
    }
}
