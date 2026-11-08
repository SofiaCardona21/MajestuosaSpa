package com.example.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Cita {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY) //para asignar automáticamente a cada cita(contador de la tabla)
    private Long idCita;

    @ManyToOne //Clave Foránea
    private Cliente cliente;

    @ManyToOne
    private Personal manicurista;

    @ManyToMany (fetch = FetchType.EAGER) //consultar todos los servicios cuando el sistema busque un turenp
    private List<Servicio> servicios;

    private LocalDate fecha;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    @Enumerated (EnumType.STRING)
    private EstadoCita estado;

    private double valorTotal;

    private boolean bonoAplicado;






}
