package com.tuempresa.easystay.modelo;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

import lombok.*;

@Entity
@Getter @Setter
@Table(name = "cliente")
@Views({
    @View(name = "default", members = "clienteID; nombre, apellido; email; telefono; direccion"),
    @View(name = "simple", members = "clienteID; nombre; apellido; email")
})
public class Cliente {

    @Id
    @Hidden // La propiedad no se muestra al usuario. Es un identificador interno
    @GeneratedValue(generator="system-uuid") // Identificador Universal Único (1)
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "clienteID", length = 32)
    String clienteID;

    @Column(name = "nombre", length = 50)
    @Required
    String nombre;

    @Column(name = "apellido", length = 50)
    @Required
    String apellido;

    @Column(name = "email", length = 50)
    String email;

    @Column(name = "telefono", length = 15)
    String telefono;

    @Column(name = "direccion", length = 100)
    String direccion;


  

}