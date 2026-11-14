package com.example.Dto;

import java.time.LocalDate;
import java.util.List;

public record AgendaResponse(String idManicurista,
                             String nombreManicurista,
                             LocalDate fecha,
                             List<CitaResponse> citas,
                             CitaResponse siguienteCita) {
}
