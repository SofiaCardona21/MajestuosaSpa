package com.example.Dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record HorarioDisponibleResponse(LocalTime hora, List<ManicuristaResponse> manicuristasDisponibles) {
}
