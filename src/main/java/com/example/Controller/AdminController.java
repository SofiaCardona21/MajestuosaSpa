package com.example.Controller;

import com.example.Dto.CambioEstadoRequest;
import com.example.Dto.CitaResponse;
import com.example.Dto.LlegadaRequest;
import com.example.Dto.TurnoResponse;
import com.example.Service.AgendaService;
import com.example.Service.CitaService;
import com.example.Service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController // es para q se cree al arrancar, espere las peticiones
// y q lo q devuelvan sus métodos se conviertan en JSON
@RequestMapping("/admin")
public class AdminController {
    private final TurnoService turnoService;
    private final CitaService citaService;
    private final AgendaService agendaService;

    public AdminController(TurnoService turnoService, CitaService citaService, AgendaService agendaService) {
        this.turnoService = turnoService;
        this.citaService = citaService;
        this.agendaService = agendaService;
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

    // el @patch se usa para modificar una parte de algo que ya existe,
    //como acá la cita ya está guardada y solo cambia su estado ent se usa
    @PatchMapping("/citas/{idCita}/estado")
    public CitaResponse cambiarEstado(@PathVariable Long idCita,
                                      @Valid @RequestBody CambioEstadoRequest datosEstado) {
        return citaService.cambiarEstado(idCita, datosEstado);
    }

    @GetMapping("/citas") //para listar las citas del dia
    public List<CitaResponse> citasDelDia(@RequestParam(required = false) LocalDate fecha) {
        return agendaService.citasDelDia(fecha);
    }
}