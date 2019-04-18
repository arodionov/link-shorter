package util.exception;

public class BeanNotFoundException extends RuntimeException {
    public BeanNotFoundException(String message) {
        super(message);
    }
}
