package ar.com.agmilevecich.sofp.app;

import ar.com.agmilevecich.sofp.config.JpaManager;
import ar.com.agmilevecich.sofp.config.JpaTestManager;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

class TestConexion {

    @Test
    void debeConectarYFinalizarCorrectamente() {
        EntityManager entityManager = null;

        try {
            entityManager = JpaTestManager.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.getTransaction().commit();

            System.out.println("Conexión Exitosa!!");
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            JpaTestManager.close();
        }
    }
}
