package com.example.Dto;

import com.example.Model.EstadoCita;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(
        @NotNull EstadoCita estado
) { }