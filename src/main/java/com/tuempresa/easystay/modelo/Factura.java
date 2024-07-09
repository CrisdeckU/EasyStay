package com.tuempresa.easystay.modelo;

import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

import lombok.*;

@Entity
@Getter @Setter
@Table(name = "factura")
@Views({
    @View(name = "default", members = "facturaID; cliente; reserva; fecha; montoTotal; descuento; precioFinal; estado"),
    @View(name = "simple", members = "facturaID; fecha; montoTotal; descuento; precioFinal; estado")
})
public class Factura {

    @Id
    @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "facturaID", length = 32)
    String facturaID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clienteID", referencedColumnName = "clienteID")
    @ReferenceView("simple")
    @NoCreate  
    @NoModify  
    Cliente cliente;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservaID", referencedColumnName = "reservaID")
    @ReferenceView("simple")
    @NoCreate  
    @NoModify  
    Reserva reserva;

    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    @Required
    Date fecha;

    @Column(name = "montoTotal")
    @Required
    @Money
    double montoTotal;

    @Column(name = "descuento")
    @Stereotype("PERCENT")
    double descuento;

    @Column(name = "precioFinal")
    @Money
    @ReadOnly
    double precioFinal;

    @Column(name = "estado", length = 20)
    @Required
    EstadoFactura estado;

    public enum EstadoFactura {
        PAGADA, PENDIENTE
    }

    @PrePersist
    @PreUpdate
    private void calcularMontos() throws Exception {
        if (reserva != null) {
            precioFinal = montoTotal - montoTotal*descuento/100;
        }
    }
}
