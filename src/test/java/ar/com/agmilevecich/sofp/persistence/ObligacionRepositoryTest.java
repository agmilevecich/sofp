package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.CargoFinanciero;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.Financiacion;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCambio;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObligacionRepositoryTest {

    @Test
    void deberiaListarObligacionesDeUnaCuentaYUnCierreDeCiclo() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            Datos datos = crearDatos();
            Obligacion obligacionObjetivo = crearObligacion(datos.cuenta(), datos.categoria(), LocalDateTime.of(2026, 9, 10, 10, 0), "Consumo objetivo");
            Obligacion otraDelMismoCiclo = crearObligacion(datos.cuenta(), datos.categoria(), LocalDateTime.of(2026, 9, 12, 10, 0), "Otro consumo");
            Obligacion otroCiclo = crearObligacion(datos.cuenta(), datos.categoria(), LocalDateTime.of(2026, 9, 20, 10, 0), "Consumo siguiente ciclo");
            Cuenta otraCuenta = crearCuenta(datos.perfil(), datos.banco(), datos.moneda(), "Otra tarjeta");
            Obligacion otraCuentaMismoCierre = crearObligacion(otraCuenta, datos.categoria(), LocalDateTime.of(2026, 9, 10, 10, 0), "Consumo otra cuenta");
            ObligacionRepository repository = new ObligacionRepository(em);
            em.getTransaction().begin();
            persistirDatosBase(em, datos);
            em.persist(otraCuenta);
            em.persist(obligacionObjetivo.getMovimientoOrigen());
            em.persist(otraDelMismoCiclo.getMovimientoOrigen());
            em.persist(otroCiclo.getMovimientoOrigen());
            em.persist(otraCuentaMismoCierre.getMovimientoOrigen());
            repository.guardar(obligacionObjetivo); repository.guardar(otraDelMismoCiclo); repository.guardar(otroCiclo); repository.guardar(otraCuentaMismoCierre);
            em.getTransaction().commit();
            List<Obligacion> resultado = repository.listarPorCuentaYCierreCiclo(datos.cuenta().getId(), LocalDate.of(2026, 9, 15));
            assertEquals(2, resultado.size()); assertEquals(obligacionObjetivo.getId(), resultado.get(0).getId()); assertEquals(otraDelMismoCiclo.getId(), resultado.get(1).getId());
            assertTrue(resultado.stream().allMatch(o -> o.getMovimientoOrigen().getCuenta().getId().equals(datos.cuenta().getId())));
        } finally { JpaTestManager.close(); }
    }

    @Test
    void noDeberiaListarUnaObligacionFinanciadaSiLaCuotaDelCierreYaEstaPagada() {
        JpaTestManager.close(); EntityManager em = JpaTestManager.createEntityManager();
        try {
            Datos datos = crearDatos(); Obligacion obligacion = crearObligacion(datos.cuenta(), datos.categoria(), LocalDateTime.of(2026, 9, 10, 10, 0), "Consumo financiado"); obligacion.generarCuotas(2); obligacion.getCuotas().get(0).registrarPago(new BigDecimal("50.00"));
            ObligacionRepository repository = new ObligacionRepository(em); em.getTransaction().begin(); persistirDatosBase(em, datos); em.persist(obligacion.getMovimientoOrigen()); repository.guardar(obligacion); em.getTransaction().commit();
            List<Obligacion> resultado = repository.listarPorCuentaYCierreCiclo(datos.cuenta().getId(), LocalDate.of(2026, 9, 15)); assertTrue(resultado.isEmpty());
            resultado = repository.listarPorCuentaYCierreCiclo(datos.cuenta().getId(), obligacion.getCuotas().get(1).getFechaCierreCiclo()); assertEquals(1, resultado.size()); assertEquals(obligacion.getId(), resultado.get(0).getId());
        } finally { JpaTestManager.close(); }
    }

    @Test
    void deberiaPersistirYRecuperarLasFinanciacionesDeUnaObligacion() {
        JpaTestManager.close(); EntityManager em = JpaTestManager.createEntityManager();
        try {
            Datos datos = crearDatos(); Obligacion obligacion = crearObligacion(datos.cuenta(), datos.categoria(), LocalDateTime.of(2026, 9, 10, 10, 0), "Consumo financiado"); Financiacion financiacion = new Financiacion(obligacion, LocalDate.of(2026, 9, 26), new BigDecimal("240.00")); obligacion.agregarFinanciacion(financiacion);
            ObligacionRepository repository = new ObligacionRepository(em); em.getTransaction().begin(); persistirDatosBase(em, datos); em.persist(obligacion.getMovimientoOrigen()); repository.guardar(obligacion); em.getTransaction().commit(); em.clear();
            Obligacion recuperada = repository.buscarPorId(obligacion.getId()).orElseThrow(); assertEquals(1, recuperada.getFinanciaciones().size()); Financiacion rf = recuperada.getFinanciaciones().get(0); assertEquals(new BigDecimal("240.00"), rf.getCapitalOriginal()); assertEquals(new BigDecimal("240.00"), rf.getSaldoCapital()); assertEquals(LocalDate.of(2026, 9, 26), rf.getFechaInicio()); assertEquals(obligacion.getId(), rf.getObligacion().getId());
        } finally { JpaTestManager.close(); }
    }

    @Test
    void deberiaDevolverListaVaciaSiNoHayObligacionesParaElCierre() {
        JpaTestManager.close(); EntityManager em = JpaTestManager.createEntityManager();
        try { Datos datos = crearDatos(); ObligacionRepository repository = new ObligacionRepository(em); em.getTransaction().begin(); persistirDatosBase(em, datos); em.getTransaction().commit(); assertTrue(repository.listarPorCuentaYCierreCiclo(datos.cuenta().getId(), LocalDate.of(2026, 9, 15)).isEmpty()); }
        finally { JpaTestManager.close(); }
    }

    @Test void deberiaRechazarCuentaNulaAlListarPorCierre() { JpaTestManager.close(); EntityManager em = JpaTestManager.createEntityManager(); try { ObligacionRepository r = new ObligacionRepository(em); assertThrows(NullPointerException.class, () -> r.listarPorCuentaYCierreCiclo(null, LocalDate.of(2026, 9, 15))); } finally { JpaTestManager.close(); } }
    @Test void deberiaRechazarFechaDeCierreNulaAlListarPorCierre() { JpaTestManager.close(); EntityManager em = JpaTestManager.createEntityManager(); try { ObligacionRepository r = new ObligacionRepository(em); assertThrows(NullPointerException.class, () -> r.listarPorCuentaYCierreCiclo(1L, null)); } finally { JpaTestManager.close(); } }

    @Test
    void deberiaContarValorizacionCompletaDeObligacionMultidivisaEnCreditoUtilizado() {
        JpaTestManager.close(); EntityManager em = JpaTestManager.createEntityManager();
        try { Datos d=crearDatos(); Moneda usd=crearDolares(); Obligacion o=crearObligacionMultidivisa(d.cuenta(),d.categoria(),usd); TipoCambio tc=new TipoCambio(usd,d.moneda(),new BigDecimal("1500.00"),LocalDateTime.of(2026,9,15,23,59),"TEST"); o.valorarCierre(tc); ObligacionRepository r=new ObligacionRepository(em); em.getTransaction().begin(); persistirDatosBase(em,d); em.persist(usd); em.persist(tc); em.persist(o.getMovimientoOrigen()); r.guardar(o); em.getTransaction().commit(); assertEquals(0,new BigDecimal("150000.00").compareTo(r.sumarCreditoUtilizadoPorCuenta(d.cuenta().getId(),d.cuenta().getMoneda()))); } finally { JpaTestManager.close(); }
    }

    @Test
    void deberiaReducirCreditoUtilizadoProporcionalmenteAlPagoParcialEnMonedaOriginal() {
        JpaTestManager.close(); EntityManager em=JpaTestManager.createEntityManager();
        try { Datos d=crearDatos(); Moneda usd=crearDolares(); Obligacion o=crearObligacionMultidivisa(d.cuenta(),d.categoria(),usd); TipoCambio tc=new TipoCambio(usd,d.moneda(),new BigDecimal("1500.00"),LocalDateTime.of(2026,9,15,23,59),"TEST"); o.valorarCierre(tc); o.registrarPago(new BigDecimal("40.00")); ObligacionRepository r=new ObligacionRepository(em); em.getTransaction().begin(); persistirDatosBase(em,d); em.persist(usd); em.persist(tc); em.persist(o.getMovimientoOrigen()); r.guardar(o); em.getTransaction().commit(); assertEquals(0,new BigDecimal("90000.00").compareTo(r.sumarCreditoUtilizadoPorCuenta(d.cuenta().getId(),d.cuenta().getMoneda()))); } finally { JpaTestManager.close(); }
    }

    @Test
    void deberiaEliminarCreditoUtilizadoAlPagarCompletaLaObligacionEnMonedaOriginal() {
        JpaTestManager.close(); EntityManager em=JpaTestManager.createEntityManager();
        try { Datos d=crearDatos(); Moneda usd=crearDolares(); Obligacion o=crearObligacionMultidivisa(d.cuenta(),d.categoria(),usd); TipoCambio tc=new TipoCambio(usd,d.moneda(),new BigDecimal("1500.00"),LocalDateTime.of(2026,9,15,23,59),"TEST"); o.valorarCierre(tc); o.registrarPago(new BigDecimal("100.00")); ObligacionRepository r=new ObligacionRepository(em); em.getTransaction().begin(); persistirDatosBase(em,d); em.persist(usd); em.persist(tc); em.persist(o.getMovimientoOrigen()); r.guardar(o); em.getTransaction().commit(); assertEquals(0,BigDecimal.ZERO.compareTo(r.sumarCreditoUtilizadoPorCuenta(d.cuenta().getId(),d.cuenta().getMoneda()))); } finally { JpaTestManager.close(); }
    }

    @Test
    void deberiaReducirCreditoUtilizadoAlRegistrarPagoParcialEnMonedaOriginal() {
        JpaTestManager.close(); EntityManager em=JpaTestManager.createEntityManager();
        try { Datos d=crearDatos(); Obligacion o=crearObligacion(d.cuenta(),d.categoria(),LocalDateTime.of(2026,9,10,10,0),"Consumo ARS"); o.registrarPago(new BigDecimal("40.00")); ObligacionRepository r=new ObligacionRepository(em); em.getTransaction().begin(); persistirDatosBase(em,d); em.persist(o.getMovimientoOrigen()); r.guardar(o); em.getTransaction().commit(); assertEquals(0,new BigDecimal("60.00").compareTo(r.sumarCreditoUtilizadoPorCuenta(d.cuenta().getId(),d.cuenta().getMoneda()))); } finally { JpaTestManager.close(); }
    }

    @Test
    void deberiaConservarCapitalYAgregarCargosPendientesDeUnaFinanciacion() {
        JpaTestManager.close(); EntityManager em=JpaTestManager.createEntityManager();
        try {
            Datos d=crearDatos(); Movimiento movimiento=new Movimiento(d.cuenta(),d.categoria(),TipoMovimiento.EGRESO,new BigDecimal("240.00"),LocalDateTime.of(2026,9,10,10,0),"Consumo financiado",FormaPago.TARJETA_CREDITO); Obligacion o=new Obligacion(movimiento); Financiacion f=o.crearFinanciacion(LocalDate.of(2026,9,26),new BigDecimal("240.00")); f.registrarInteres(new BigDecimal("12.00"),LocalDate.of(2026,9,27),new BigDecimal("240.00"),new BigDecimal("18.0000"),1); ObligacionRepository r=new ObligacionRepository(em);
            em.getTransaction().begin(); persistirDatosBase(em,d); em.persist(o.getMovimientoOrigen()); r.guardar(o); em.getTransaction().commit();
            assertEquals(1L, em.createQuery("SELECT COUNT(f) FROM Financiacion f WHERE f.obligacion.id = :obligacionId", Long.class).setParameter("obligacionId", o.getId()).getSingleResult());
            assertEquals(1L, em.createQuery("SELECT COUNT(c) FROM CargoFinanciero c WHERE c.financiacion.obligacion.id = :obligacionId", Long.class).setParameter("obligacionId", o.getId()).getSingleResult());
            em.clear();
            Obligacion recuperada=r.buscarPorId(o.getId()).orElseThrow(); assertEquals(1,recuperada.getFinanciaciones().size()); assertEquals(1,recuperada.getFinanciaciones().get(0).getCargos().size()); CargoFinanciero cargo=recuperada.getFinanciaciones().get(0).getCargos().get(0); assertEquals(new BigDecimal("12.00"),cargo.getSaldoPendiente());
            BigDecimal resultado=r.sumarCreditoUtilizadoPorCuenta(d.cuenta().getId(),d.cuenta().getMoneda()); assertEquals(0,new BigDecimal("252.00").compareTo(resultado));
        } finally { JpaTestManager.close(); }
    }

    private Datos crearDatos() {
        Usuario usuario=new Usuario("Ariel","Milevecich","obligacion.repository."+System.nanoTime()+"@test.com","hash"); PerfilFinanciero perfil=new PerfilFinanciero("Personal",usuario); InstitucionFinanciera banco=new InstitucionFinanciera("Banco Santander",TipoInstitucionFinanciera.BANCO); Moneda moneda=new Moneda("ARS","Peso Argentino",2,TipoMoneda.FIAT); Cuenta cuenta=crearCuenta(perfil,banco,moneda,"Tarjeta principal"); Categoria categoria=new Categoria("Compra",perfil); return new Datos(usuario,perfil,banco,moneda,cuenta,categoria);
    }
    private Moneda crearDolares(){ return new Moneda("USD","Dólar Estadounidense",2,TipoMoneda.FIAT); }
    private Cuenta crearCuenta(PerfilFinanciero perfil,InstitucionFinanciera banco,Moneda moneda,String nombre){ return new Cuenta(nombre,perfil,banco,moneda,new BigDecimal("500000.00"),15,10); }
    private Obligacion crearObligacion(Cuenta cuenta,Categoria categoria,LocalDateTime fechaHora,String descripcion){ Movimiento m=new Movimiento(cuenta,categoria,TipoMovimiento.EGRESO,new BigDecimal("100.00"),fechaHora,descripcion,FormaPago.TARJETA_CREDITO); return new Obligacion(m); }
    private Obligacion crearObligacionMultidivisa(Cuenta cuenta,Categoria categoria,Moneda monedaOriginal){ Movimiento m=new Movimiento(cuenta,categoria,monedaOriginal,TipoMovimiento.EGRESO,new BigDecimal("100.00"),LocalDateTime.of(2026,9,10,10,0),"Consumo USD",FormaPago.TARJETA_CREDITO); return new Obligacion(m); }
    private void persistirDatosBase(EntityManager em,Datos d){ em.persist(d.usuario()); em.persist(d.perfil()); em.persist(d.banco()); em.persist(d.moneda()); em.persist(d.cuenta()); em.persist(d.categoria()); }
    private record Datos(Usuario usuario,PerfilFinanciero perfil,InstitucionFinanciera banco,Moneda moneda,Cuenta cuenta,Categoria categoria) {}
}
