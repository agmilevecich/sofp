package ar.com.agmilevecich.sofp.config;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class JpaManagerTest {

    @Test
    void deberiaCrearEntityManager() {

        EntityManager entityManager =
                JpaTestManager.createEntityManager();

        assertNotNull(entityManager);

        entityManager.close();
    }
}
