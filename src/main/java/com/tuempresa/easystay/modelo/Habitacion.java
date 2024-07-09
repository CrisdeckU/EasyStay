package com.tuempresa.easystay.modelo;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

import lombok.*;

@Entity
@Getter @Setter
@Table(name = "habitacion")
@View(members = "habitacionID; tipo; precioPorNoche; disponibilidad")
public class Habitacion {

    @Id
    @Hidden // La propiedad no se muestra al usuario. Es un identificador interno
    @GeneratedValue(generator="system-uuid") // Identificador Universal Único (1)
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "habitacionID", length = 32)
    String habitacionID;

    @Column(name = "tipo", length = 30)
    @Required
    String tipo; // Ej: individual, doble, suite, etc.

    @Column(name = "precioPorNoche")
    @Required
    @Money
    double precioPorNoche;
    
    @Column (name = "foto", length = 32)
    @Files
    String foto;

    @Column(name = "disponibilidad", length = 20)
    @Required
    DisponibilidadHab disponibilidad; // Ej: disponible, ocupada, en mantenimiento

    public enum DisponibilidadHab {
        DISPONIBLE, EN_MANTENIMIENTO
    }




}