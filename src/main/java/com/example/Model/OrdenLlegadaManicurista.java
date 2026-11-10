package com.example.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenLlegadaManicurista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //para asignar automáticamente a cada cita(contador de la tabla)
    private Long idLlegada;

    private LocalDate fecha;

    @ManyToOne
    private Personal manicurista;

    private int posicion;



}
