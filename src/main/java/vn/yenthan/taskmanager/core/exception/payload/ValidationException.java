package vn.yenthan.taskmanager.core.exception.payload;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
