package vn.yenthan.taskmanager.util;

public final class MessageKeys {
    private MessageKeys() {}

    // --- SYSTEM
    public static final String SYSTEM_INTERNAL_ERROR = "system.internal.error";
    public static final String SYSTEM_DATABASE_ERROR = "system.database.error";
    public static final String SYSTEM_VALIDATION_FAILED = "system.validation.failed";
    public static final String SYSTEM_METHOD_NOT_ALLOWED = "system.method.not.allowed";

    // --- AUTH / SECURITY
    public static final String AUTH_UNAUTHENTICATED = "auth.unauthenticated";
    public static final String AUTH_UNAUTHORIZED = "auth.unauthorized";
    public static final String AUTH_INVALID_TOKEN = "auth.invalid.token";
    public static final String AUTH_INVALID_DEVICE_ID = "auth.invalid.device.id";
    public static final String AUTH_INVALID_CREDENTIALS = "auth.invalid.credentials";
    public static final String AUTH_ACCOUNT_LOCKED = "auth.account.locked";
    public static final String AUTH_ACCOUNT_UNVERIFIED = "auth.account.unverified";
    public static final String AUTH_LOGIN_SUCCESS = "auth.login.success";
    public static final String AUTH_LOGOUT_SUCCESS = "auth.logout.success";
    public static final String AUTH_REGISTER_SUCCESS = "auth.register.success";
    public static final String AUTH_REFRESH_SUCCESS = "auth.refresh.success";

    // --- USER
    public static final String USER_NOT_FOUND = "user.not.found";

    // --- VALIDATION
    public static final String USERNAME_REQUIRED = "username.required";
    public static final String USERNAME_SIZE = "username.size";
    public static final String USERNAME_PATTERN = "username.pattern";
    public static final String USERNAME_ALREADY_EXISTS = "username.already.exists";
    public static final String EMAIL_ALREADY_EXISTS = "email.already.exists";
    public static final String EMAIL_INVALID = "email.invalid";
    public static final String PASSWORD_INVALID = "password.invalid";
    public static final String PASSWORD_REQUIRED = "password.required";
    public static final String PASSWORD_NO_SPACE = "password.no.space";
}
