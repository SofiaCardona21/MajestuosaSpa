package com.example.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record InicioSesionRequest(@Pattern(regexp = "\\d{10}", message = "El usuario debe tener 10 dígitos")
                                  String usuario, @NotBlank(message = "Se debe poner una contraseña") String contrasena)
{ }
