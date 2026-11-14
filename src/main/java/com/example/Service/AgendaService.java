package com.example.Service;

import com.example.Dto.AgendaResponse;
import com.example.Dto.CitaResponse;
import com.example.Errores.LogicaNegocioException;
import com.example.Model.Cita;
import com.example.Model.EstadoCita;
import com.example.Model.Personal;
import com.example.Model.Rol;
import com.example.Repository.CitaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

@Service
@Slf4j
public class AgendaService {

    private final CitaRepository citaRepository;
    private final TablaUsuariosService tablaUsuarios;
    private final CitaService citaService;

    public AgendaService(CitaRepository citaRepository, TablaUsuariosService tablaUsuarios, CitaService citaService) {
        this.citaRepository = citaRepository;
        this.tablaUsuarios = tablaUsuarios;
        this.citaService = citaService;
    }

    // Ordena las citas por hora de inicio con la PriorityQueue (usa el compareTo de Cita)
    public List<Cita> ordenarPorHora(List<Cita> citas) {

        //Meter todas las citas en la PriorityQueue: ordenada por  la más temprana
        PriorityQueue<Cita> colaCitas = new PriorityQueue<>(citas);

        List<Cita> citasOrdenadas = new ArrayList<>();

        while (!colaCitas.isEmpty()) {
            citasOrdenadas.add(colaCitas.poll());
        }

        return citasOrdenadas;
    }

    // La siguiente cita que tiene que atender: la primera PENDIENTE en orden de hora (null si no le queda ninguna)
    public Cita buscarSiguiente(List<Cita> citasOrdenadas) {

        Cita siguiente = null;

        for (Cita cita : citasOrdenadas) {
            if (siguiente == null && cita.getEstado() == EstadoCita.PENDIENTE) {
                siguiente = cita;
            }
        }

        return siguiente;
    }

    // Citas de todas las manicuristas en un día, ordenadas por hora, para el administrador
    public List<CitaResponse> citasDelDia(LocalDate fecha) {

        long tiempoInicio = System.nanoTime();

        LocalDate dia = fecha;
        if (dia == null) {
            dia = LocalDate.now();
        }

        List<Cita> citasOrdenadas = ordenarPorHora(citaRepository.findByFecha(dia));

        List<CitaResponse> respuesta = new ArrayList<>();
        for (Cita cita : citasOrdenadas) {
            respuesta.add(citaService.convertirAResponse(cita));
        }

        long tiempoFinal = System.nanoTime();
        long tiempoProceso = tiempoFinal - tiempoInicio;
        log.info("El proceso tardó: " + tiempoProceso);

        return respuesta;
    }

    // Agenda de una manicurista en un día: sus citas en orden de hora y la siguiente que tiene que atender
    public AgendaResponse agendaManicurista(String idManicurista, LocalDate fecha) {

        Personal manicurista = tablaUsuarios.buscarPersonal(idManicurista);

        if (manicurista == null || manicurista.getRol() != Rol.MANICURISTA) {
            log.warn("Agenda rechazada: la manicurista no existe");
            throw new LogicaNegocioException("La manicurista no existe");
        }

        LocalDate dia = fecha;
        if (dia == null) {
            dia = LocalDate.now();
        }

        List<Cita> citasOrdenadas = ordenarPorHora(
                citaRepository.findByManicuristaIdPersonalAndFecha(idManicurista, dia));

        List<CitaResponse> citas = new ArrayList<>();
        for (Cita cita : citasOrdenadas) {
            citas.add(citaService.convertirAResponse(cita));
        }

        Cita siguiente = buscarSiguiente(citasOrdenadas);
        CitaResponse siguienteCita = null;
        if (siguiente != null) {
            siguienteCita = citaService.convertirAResponse(siguiente);
        }

        log.info("Agenda hecha");

        return new AgendaResponse(manicurista.getIdPersonal(), manicurista.getNombrePersonal(), dia, citas, siguienteCita);
    }
}