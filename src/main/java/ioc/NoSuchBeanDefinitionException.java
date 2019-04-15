package ioc;

public class NoSuchBeanDefinitionException extends RuntimeException {

    public NoSuchBeanDefinitionException(Throwable cause) {
        super(cause);
    }
}
