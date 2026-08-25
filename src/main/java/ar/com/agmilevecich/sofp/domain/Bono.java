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
    public Bono(String nombre, Moneda moneda) {
        super(nombre, moneda);
    }
}
