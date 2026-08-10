package ar.com.agmilevecich.sofp.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class JpaTestManager {

    private static EntityManagerFactory entityManagerFactory;

    private JpaTestManager() {
    }

    public static synchronized EntityManager createEntityManager() {

        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {

            entityManagerFactory =
                    Persistence.createEntityManagerFactory(
                            "sofp-persistence-unit-test",
                            propiedadesTest()
                    );
        }

        return entityManagerFactory.createEntityManager();
    }

    public static synchronized void close() {

        if (entityManagerFactory != null
                && entityManagerFactory.isOpen()) {

            entityManagerFactory.close();
            entityManagerFactory = null;
        }
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