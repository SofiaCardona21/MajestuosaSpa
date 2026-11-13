package com.example.Controller;

import com.example.Dto.LlegadaRequest;
import com.example.Dto.TurnoResponse;
import com.example.Service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // es para q se cree al arrancar, vaya escuchando peticiones
// y q lo q devuelvan sus métodos se conviertan en JSON
@RequestMapping("/admin")
public class AdminController {
    private final TurnoService turnoService;

    public AdminController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @PostMapping("/llegadas")
    public ResponseEntity<List<TurnoResponse>> registrarLlegada(@Valid @RequestBody LlegadaRequest datosLlegada) {
        List<TurnoResponse> orden = turnoService.registrarLlegada(datosLlegada);
        return ResponseEntity.status(HttpStatus.CREATED).body(orden);
    }

    @GetMapping("/llegadas")
    public List<TurnoResponse> verOrden() {
        return turnoService.verOrden();
    }
}
