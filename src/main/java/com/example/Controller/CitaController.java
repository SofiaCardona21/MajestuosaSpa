package com.example.Controller;

import com.example.Dto.CitaRequest;
import com.example.Dto.CitaResponse;
import com.example.Dto.HorarioDisponibleResponse;
import com.example.Service.CitaService;
import com.example.Service.DisponibilidadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class CitaController {

    private final DisponibilidadService disponibilidadService;
    private final CitaService citaService;

    public CitaController(DisponibilidadService disponibilidadService, CitaService citaService) {
        this.disponibilidadService = disponibilidadService;
        this.citaService = citaService;
    }

    @GetMapping("/citas/horarios")
    public List<HorarioDisponibleResponse> consultarHorarios(@RequestParam LocalDate fecha,
                                                             @RequestParam List<String> servicios) {
        // @RequestParam es para leer lo que el cliente eligio en fecha y servicios que va en la URL despues del ?
        return disponibilidadService.consultarHorarios(fecha, servicios);
    }

    @PostMapping("/citas")
    public ResponseEntity<CitaResponse> agendar(@Valid @RequestBody CitaRequest datosCita) {
        CitaResponse cita = citaService.agendar(datosCita);
        return ResponseEntity.status(HttpStatus.CREATED).body(cita);
    }

    @GetMapping("/clientes/{celular}/citas")
    public List<CitaResponse> listaCitasCliente(@PathVariable String celular) {
        return citaService.listaCitasCliente(celular);
    }    //@PathVariable es para que se lea el dato celular de la URL
}
