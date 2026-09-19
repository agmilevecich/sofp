package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormaPagoTest {

    @Test
    void deberiaDefinirLasFormasDePagoPrevistas() {

        assertEquals(
                5,
                FormaPago.values().length
        );

        assertArrayEquals(
                new FormaPago[]{
                        FormaPago.EFECTIVO,
                        FormaPago.TRANSFERENCIA,
                        FormaPago.TARJETA_DEBITO,
                        FormaPago.TARJETA_CREDITO,
                        FormaPago.QR
                },
                FormaPago.values()
        );
    }

    @Test
    void deberiaRepresentarCorrectamenteLasFormasDePago() {

        assertEquals("EFECTIVO", FormaPago.EFECTIVO.name());
        assertEquals("TRANSFERENCIA", FormaPago.TRANSFERENCIA.name());
        assertEquals("TARJETA_DEBITO", FormaPago.TARJETA_DEBITO.name());
        assertEquals("TARJETA_CREDITO", FormaPago.TARJETA_CREDITO.name());
        assertEquals("QR", FormaPago.QR.name());
    }
}
