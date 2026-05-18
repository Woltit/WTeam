package com.wteam.backend.common.constants;

public final class ValidationConstants {

    private ValidationConstants() {
        throw new UnsupportedOperationException("ValidationConstants class cannot be instantiated");
    }

    public static final class User {
        // Constraints
        public static final int EMAIL_MAX_LENGTH = 255;
        public static final int PASSWORD_MAX_LENGTH = 255;
        public static final int PASSWORD_MIN_LENGTH = 8;

        // Messages
        public static final String EMAIL_BLANK_MSG = "Email cannot be blank";
        public static final String EMAIL_INVALID_FORMAT = "Invalid email format";
        public static final String PASSWORD_BLANK_MSG = "Password cannot be blank";
        public static final String PASSWORD_INVALID_FORMAT = "Password must be between " + PASSWORD_MIN_LENGTH + " and " + PASSWORD_MAX_LENGTH;
    }

    public static final class UserProfile {
        // Constraints
        public static final int NAME_MAX_LENGTH = 100;
        public static final int PHONE_NUMBER_LENGTH = 15;

        // Messages
        public static final String LAST_NAME_BLANK_MSG = "Last name cannot be blank";
        public static final String FIRST_NAME_BLANK_MSG = "First name cannot be blank";
        public static final String BIRTH_DATE_NULL_MSG = "Birth date cannot be null";
        public static final String PHONE_NUMBER_BLANK_MSG = "Phone number cannot be blank";
        public static final String PHONE_REGEX_INVALID_MSG = "Invalid format of phoneNumber number";

        // Regex
        public static final String PHONE_REGEX = "^\\+[1-9]\\d{10,14}$";
    }
}
