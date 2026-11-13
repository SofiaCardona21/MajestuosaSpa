package com.example.Dto;

import jakarta.validation.constraints.NotBlank;

public record LlegadaRequest(@NotBlank String idManicurista) {
}
