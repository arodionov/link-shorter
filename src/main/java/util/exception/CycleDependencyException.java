package util.exception;

public class CycleDependencyException extends RuntimeException {
    public CycleDependencyException(String message) {
        super(message);
    }
}
