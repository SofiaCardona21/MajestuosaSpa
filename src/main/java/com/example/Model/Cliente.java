package com.example.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    @Id
    private String numeroTel;

    private String nombre;

    private String contrasenaCliente;

    private int puntos;

    private boolean bonosDisponibles;





}
