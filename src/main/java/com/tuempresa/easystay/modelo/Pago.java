package com.tuempresa.easystay.modelo;

import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.*;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

import lombok.*;

@Entity
@Getter @Setter
@Table(name = "pago")
@View(members = "pagoID; factura; fecha; monto; metodo")
public class Pago {

    @Id
    @Hidden // La propiedad no se muestra al usuario. Es un identificador interno
    @GeneratedValue(generator="system-uuid") // Identificador Universal Único (1)
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "pagoID", length = 32)
    String pagoID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facturaID", referencedColumnName = "facturaID")
    @ReferenceView("simple")
    @NoCreate  // Asegura que no se puedan crear nuevas facturas desde la entidad Pago
    @NoModify  // Se deshabilita la modificación del pago desde aquí
    Factura factura;

    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    @Required
    Date fecha;

    @Column(name = "monto")
    @Required
    @Money
    double monto;

    @Column(name = "metodo", length = 50)
    @Required
    String metodo; // Ej: tarjeta de crédito, efectivo, transferencia

    @PrePersist
    @PreUpdate
    private void validarMonto() throws Exception {
        if (factura != null) {
            double totalPagado = monto;
            EntityManager em = Persistence.createEntityManagerFactory("default").createEntityManager();
            try {
                // Suma todos los pagos existentes para la misma factura
                String jpql = "SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.factura.facturaID = :facturaID AND p.pagoID != :pagoID";
                Double pagosPrevios = em.createQuery(jpql, Double.class)
                                        .setParameter("facturaID", factura.getFacturaID())
                                        .setParameter("pagoID", pagoID != null ? pagoID : "")
                                        .getSingleResult();
                totalPagado += pagosPrevios;
            } finally {
                em.close();
            }

            if (totalPagado > factura.getPrecioFinal()) {
                throw new ValidationException("El monto total de los pagos no puede exceder el precio final de la factura.");
            }

            // Cambia el estado de la factura a "PAGADA" si el monto total de los pagos es igual al precio final de la factura
            if (totalPagado == factura.getPrecioFinal()) {
                factura.setEstado(Factura.EstadoFactura.PAGADA);
            }
        }
    }
}
