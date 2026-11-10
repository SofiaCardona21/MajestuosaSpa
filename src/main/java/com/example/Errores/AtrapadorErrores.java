package com.example.Errores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class AtrapadorErrores {
    private static final Logger log = LoggerFactory.getLogger(AtrapadorErrores.class);

    // Errores de las reglas del spa: responde 400 con el mensaje de la regla
    @ExceptionHandler(LogicaNegocioException.class)
    public ResponseEntity<Map<String, String>> reglaNegocio(LogicaNegocioException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
    // Datos que no cumplen las validaciones de los DTO: responde 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacion(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(Map.of("error", "Hay datos inválidos en el formulario"));
    }

    // Cualquier otro error: lo registra en consola y responde 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> inesperado(Exception e) {
        log.error("Error inesperado", e);
        return ResponseEntity.internalServerError().body(Map.of("error", "Ocurrió un error inesperado"));
    }
}
