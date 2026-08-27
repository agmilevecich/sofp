package ar.com.agmilevecich.sofp.domain;

import jakarta.persistence.Entity;

@Entity
public class Bono extends Activo {

    /**
     * Constructor requerido por JPA.
     */
    protected Bono() {
    }

    /**
     * Constructor principal del dominio.
     */
    public Bono(String nombre, String simbolo, Moneda moneda) {
        super(nombre, simbolo, moneda);
    }
}
