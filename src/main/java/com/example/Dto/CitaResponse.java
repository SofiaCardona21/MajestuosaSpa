package com.example.Dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CitaResponse (Long idCita,
                            LocalDate fecha,
                            LocalTime horaInicio,
                            LocalTime horaFin,
                            List<String> nombresServicios,
                            String nombreCliente,
                            String nombreManicurista,
                            double valorTotal,
                            String estado){
}
