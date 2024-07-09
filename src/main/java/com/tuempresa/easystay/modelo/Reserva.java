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
@Table(name = "reserva")
@Views({
    @View(name = "default", members = "reservaID; cliente; habitacion; fechaInicio; fechaFin; estado"),
    @View(name = "simple", members = "reservaID; fechaInicio; fechaFin; estado")
})
public class Reserva {

    @Id
    @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "reservaID", length = 32)
    String reservaID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clienteID", referencedColumnName = "clienteID")
    @ReferenceView("simple")
    @NoCreate  
    @NoModify  
    Cliente cliente;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habitacionID", referencedColumnName = "habitacionID")
    @NoCreate  
    @NoModify  
    Habitacion habitacion;

    @Column(name = "fechaInicio")
    @Temporal(TemporalType.DATE)
    @Required
    Date fechaInicio;

    @Column(name = "fechaFin")
    @Temporal(TemporalType.DATE)
    @Required
    Date fechaFin;

    @Column(name = "estado", length = 20)
    @Required
    EstadoReserva estado; 

    public enum EstadoReserva {
        CONFIRMADA, PENDIENTE, CANCELADA
    }

    @PrePersist
    @PreUpdate
    private void validarReserva() throws Exception {
        validarFechas();
        validarDisponibilidadHabitacion();
    }

    private void validarFechas() throws Exception {
        if (fechaInicio != null && fechaFin != null) {
            if (!fechaInicio.before(fechaFin)) {
                throw new ValidationException("La fecha de inicio debe ser anterior a la fecha de fin.");
            }
        }
    }

    private void validarDisponibilidadHabitacion() throws Exception {
        if (habitacion != null && fechaInicio != null && fechaFin != null) {
            String jpql = "SELECT COUNT(r) FROM Reserva r WHERE r.habitacion.habitacionID = :habitacionID AND r.estado != :estadoCancelada AND " +
                          "((:fechaInicio BETWEEN r.fechaInicio AND r.fechaFin) OR " +
                          "(:fechaFin BETWEEN r.fechaInicio AND r.fechaFin) OR " +
                          "(r.fechaInicio BETWEEN :fechaInicio AND :fechaFin) OR " +
                          "(r.fechaFin BETWEEN :fechaInicio AND :fechaFin))";

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
            EntityManager em = emf.createEntityManager();
            Long count = em.createQuery(jpql, Long.class)
                           .setParameter("habitacionID", habitacion.getHabitacionID())
                           .setParameter("estadoCancelada", EstadoReserva.CANCELADA)
                           .setParameter("fechaInicio", fechaInicio)
                           .setParameter("fechaFin", fechaFin)
                           .getSingleResult();
            em.close();
            emf.close();

            if (count > 0) {
                throw new ValidationException("La habitación ya está reservada en las fechas seleccionadas.");
            }
        }
    }
}