package ar.com.agmilevecich.sofp.config;

import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.persistence.MonedaRepository;
import jakarta.persistence.EntityManager;

import java.util.Objects;

public final class DatosInicialesSistema {

    private static final String MONEDA_ARS = "ARS";
    private static final String MONEDA_USD = "USD";

    private DatosInicialesSistema() {
    }

    public static void crearSiNoExisten(EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "El EntityManager es obligatorio");

        MonedaRepository monedaRepository = new MonedaRepository(entityManager);

        entityManager.getTransaction().begin();
        try {
            if (monedaRepository.buscarPorCodigo(MONEDA_ARS).isEmpty()) {
                monedaRepository.guardar(
                        new Moneda(
                                MONEDA_ARS,
                                "Peso argentino",
                                2,
                                TipoMoneda.FIAT
                        )
                );
            }

            if (monedaRepository.buscarPorCodigo(MONEDA_USD).isEmpty()) {
                monedaRepository.guardar(
                        new Moneda(
                                MONEDA_USD,
                                "Dólar estadounidense",
                                2,
                                TipoMoneda.FIAT
                        )
                );
            }

            entityManager.getTransaction().commit();
        } catch (RuntimeException e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }
}
