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
public class Servicio {

    @Id
    private String idServicio;

    private String nombreServicio;

    private int duracionMinutos;

    private double precio;

}
