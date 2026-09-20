package ar.com.agmilevecich.sofp.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class JpaTestManager {

    private static final ThreadLocal<EntityManagerFactory> ENTITY_MANAGER_FACTORY = new ThreadLocal<>();
    private static final ThreadLocal<EntityManager> ENTITY_MANAGER = new ThreadLocal<>();

    private JpaTestManager() {
    }

    public static synchronized EntityManager createEntityManager() {

        EntityManager entityManagerActual = ENTITY_MANAGER.get();
        EntityManagerFactory entityManagerFactory = ENTITY_MANAGER_FACTORY.get();

        if (entityManagerActual == null || !entityManagerActual.isOpen()) {

            if (entityManagerActual != null) {
                entityManagerActual.close();
            }
            ENTITY_MANAGER.remove();

            if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
                entityManagerFactory.close();
            }

            entityManagerFactory =
                    Persistence.createEntityManagerFactory(
                            "sofp-persistence-unit-test",
                            propiedadesTest()
                    );
            ENTITY_MANAGER_FACTORY.set(entityManagerFactory);
        }

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        ENTITY_MANAGER.set(entityManager);
        return entityManager;
    }

    public static synchronized void close() {

        EntityManager entityManager = ENTITY_MANAGER.get();
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        ENTITY_MANAGER.remove();

        EntityManagerFactory entityManagerFactory = ENTITY_MANAGER_FACTORY.get();

        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }

        ENTITY_MANAGER_FACTORY.remove();
    }

    private static Map<String, Object> propiedadesTest() {

        Map<String, Object> propiedades = new HashMap<>();

        propiedades.put(
                "jakarta.persistence.jdbc.driver",
                "org.h2.Driver"
        );

        propiedades.put(
                "jakarta.persistence.jdbc.url",
                "jdbc:h2:mem:sofp_test_"
                        + UUID.randomUUID()
                        + ";DB_CLOSE_DELAY=0"
        );

        propiedades.put(
                "jakarta.persistence.jdbc.user",
                "sa"
        );

        propiedades.put(
                "jakarta.persistence.jdbc.password",
                ""
        );

        propiedades.put(
                "hibernate.hbm2ddl.auto",
                "create-drop"
        );

        propiedades.put(
                "hibernate.show_sql",
                "true"
        );

        propiedades.put(
                "hibernate.format_sql",
                "true"
        );

        return propiedades;
    }
}
