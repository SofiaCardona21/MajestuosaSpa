package com.example.Controller;

import com.example.Dto.AgendaResponse;
import com.example.Service.AgendaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class ManicuristaController {

    private final AgendaService agendaService;

    public ManicuristaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    // Agenda de una manicurista
    @GetMapping("/manicuristas/{idManicurista}/agenda")
    public AgendaResponse verAgenda(@PathVariable String idManicurista,
                                    @RequestParam(required = false) LocalDate fecha) {
        return agendaService.agendaManicurista(idManicurista, fecha);
    }
}
