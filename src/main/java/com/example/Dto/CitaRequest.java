package com.example.Dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CitaRequest(@NotBlank(message = "El celular del cliente es obligatorio")
                          String celularCliente,

                          @NotNull(message = "La fecha es obligatoria")
                          @FutureOrPresent(message = "No se pueden agendar citas en fechas pasadas")
                          LocalDate fecha,

                          @NotNull(message = "La hora es obligatoria")
                          LocalTime horaInicio,

                          @NotEmpty(message = "Debe elegir al menos un servicio")
                          List<String> idsServicios,

                          String idManicurista) {


}
