package com.example.Controller;

import com.example.Dto.ClienteResponse;
import com.example.Service.FidelidadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClienteController {

    private final FidelidadService fidelidadService;

    public ClienteController(FidelidadService fidelidadService) {
        this.fidelidadService = fidelidadService;
    }

    // Puntos y bonos de una clienta: GET /clientes/3001234567
    @GetMapping("/clientes/{celular}")
    public ClienteResponse consultarCliente(@PathVariable String celular) {
        return fidelidadService.consultarCliente(celular);
    }
}