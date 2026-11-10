package com.example.Controller;

import com.example.Dto.ServicioResponse;
import com.example.Service.ServicioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping ("/servicios")
public class ServicioController {
    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping
    public ResponseEntity <List<ServicioResponse>> listar() {
        return ResponseEntity.ok(servicioService.listarServicios());
    }
}
