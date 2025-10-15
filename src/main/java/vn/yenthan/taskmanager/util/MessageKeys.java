package vn.yenthan.taskmanager.util;

public final class MessageKeys {
    private MessageKeys() {}

    // --- SYSTEM
    public static final String SYSTEM_INTERNAL_ERROR = "system.internal.error";
    public static final String SYSTEM_DATABASE_ERROR = "system.database.error";
    public static final String SYSTEM_VALIDATION_FAILED = "system.validation.failed";
    public static final String SYSTEM_METHOD_NOT_ALLOWED = "system.method.not.allowed";
    public static final String SYSTEM_NOT_FOUND = "system.not.found";

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

    // --- SCRUMBOARD
    public static final String BOARD_GET_SUCCESS = "board.get.success";
    public static final String BOARD_CREATE_SUCCESS = "board.create.success";
    public static final String BOARD_UPDATE_SUCCESS = "board.update.success";
    public static final String BOARD_DELETE_SUCCESS = "board.delete.success";
    public static final String BOARD_NOT_FOUND = "board.not.found";
    public static final String BOARD_NAME_EXISTS = "board.name.exists";
    
    public static final String LIST_GET_SUCCESS = "list.get.success";
    public static final String LIST_CREATE_SUCCESS = "list.create.success";
    public static final String LIST_UPDATE_SUCCESS = "list.update.success";
    public static final String LIST_DELETE_SUCCESS = "list.delete.success";
    public static final String LIST_NOT_FOUND = "list.not.found";
    
    public static final String CARD_GET_SUCCESS = "card.get.success";
    public static final String CARD_CREATE_SUCCESS = "card.create.success";
    public static final String CARD_UPDATE_SUCCESS = "card.update.success";
    public static final String CARD_DELETE_SUCCESS = "card.delete.success";
    public static final String CARD_NOT_FOUND = "card.not.found";
    public static final String CARD_MOVE_SUCCESS = "card.move.success";
    
    public static final String MEMBER_GET_SUCCESS = "member.get.success";
    public static final String MEMBER_ADD_SUCCESS = "member.add.success";
    public static final String MEMBER_REMOVE_SUCCESS = "member.remove.success";
    public static final String MEMBER_UPDATE_ROLE_SUCCESS = "member.update.role.success";
    public static final String MEMBER_NOT_FOUND = "member.not.found";
    
    public static final String NOTIFICATION_GET_SUCCESS = "notification.get.success";
    public static final String NOTIFICATION_MARK_READ_SUCCESS = "notification.mark.read.success";
    public static final String NOTIFICATION_MARK_ALL_READ_SUCCESS = "notification.mark.all.read.success";
    public static final String NOTIFICATION_DELETE_SUCCESS = "notification.delete.success";
}
