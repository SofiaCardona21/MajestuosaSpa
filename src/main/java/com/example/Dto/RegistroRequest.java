package com.example.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequest( @NotBlank(message = "El nombre es obligatorio") String nombreCliente,
                               @Pattern(regexp = "3\\d{9}", message = "El celular debe empezar por 3 y tener 10 dígitos") String numeroTelCliente,
                               @NotBlank(message = "Debes de ingresar una contraseña obligatoriamente") String contrasena)
        // el @Pattern es para que se cumpla formato, en este caso que empiece por 3 y tenga 10 digitos
{ }

