package com.example.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Personal {

    @Id
    private String idPersonal;

    private String nombrePersonal;

    private String contrasenaPersonal;

    @Enumerated(EnumType.STRING) //Para que se guarde en la BD como un String(Rol)
    private Rol rol;

    private LocalTime horaEntrada;

    private LocalTime horaSalida;

}
