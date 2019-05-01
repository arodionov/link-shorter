package ioc;

import ioc.anotation.MyTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Optional.ofNullable;

@Configuration
@RequiredArgsConstructor
public class MyTransactionBeanPostProcessor implements BeanPostProcessor {

    private static final ThreadLocal<EntityManager> entityManagerHolder = new ThreadLocal<>();
    private final EntityManagerFactory entityManagerFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (bean.getClass().isAnnotationPresent(MyTransaction.class)) {
            Arrays.stream(bean.getClass().getDeclaredFields())
                    .filter(field -> field.getType().equals(EntityManager.class))
                    .findAny()
                    .ifPresent(field -> {
                        try {
                            field.setAccessible(true);
                            field.set(bean, createEntityManagerProxy());
                        } catch (IllegalAccessException ignored) {
                        }
                    });
            return createBeanProxy(bean);
        } else {
            return bean;
        }
    }

    private EntityManager createEntityManagerProxy() {
        return (EntityManager) Proxy.newProxyInstance(
                EntityManager.class.getClassLoader(),
                new Class[]{EntityManager.class},
                ((proxy, method, args) -> method.invoke(entityManagerHolder.get(), args)));
    }

    private Object createBeanProxy(Object bean) {
        return Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(),
                ((proxy, method, args) -> {
                    AtomicBoolean isEntityManagerCreator = new AtomicBoolean();
                    EntityManager entityManager = ofNullable(entityManagerHolder.get())
                            .orElseGet(() -> {
                                EntityManager newEntityManagerFactory = entityManagerFactory.createEntityManager();
                                entityManagerHolder.set(newEntityManagerFactory);
                                isEntityManagerCreator.set(true);
                                newEntityManagerFactory.getTransaction().begin();
                                return newEntityManagerFactory;
                            });

                    Object returnValuer = method.invoke(bean, args);

                    if (isEntityManagerCreator.get()) {
                        entityManager.getTransaction().commit();
                        entityManagerHolder.remove();
                    }

                    return returnValuer;
                }));
    }

}
