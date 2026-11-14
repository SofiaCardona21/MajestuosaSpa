package com.example.Service;

import com.example.Model.Cita;
import com.example.Model.EstadoCita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AgendaServiceTest {

    private AgendaService agendaService;

    @BeforeEach
    void preparar() {
        // Estas pruebas no usan MySQL ni la tabla hash
        agendaService = new AgendaService(null, null, null);
    }

    // Citas que llegan desordenadas salen en orden de hora
    @Test
    void ordenaCitasDesordenadas() {
        List<Cita> citas = List.of(
                crearCita(15, 0, EstadoCita.PENDIENTE),
                crearCita(10, 0, EstadoCita.PENDIENTE),
                crearCita(12, 30, EstadoCita.PENDIENTE));

        List<Cita> ordenadas = agendaService.ordenarPorHora(citas);

        assertEquals(LocalTime.of(10, 0), ordenadas.get(0).getHoraInicio());
        assertEquals(LocalTime.of(12, 30), ordenadas.get(1).getHoraInicio());
        assertEquals(LocalTime.of(15, 0), ordenadas.get(2).getHoraInicio());
    }

    // Muchas citas en desorden: cada una queda antes o a la misma hora que la siguiente
    @Test
    void ordenaMuchasCitas() {
        List<Cita> citas = new ArrayList<>();
        int[] horas = {18, 11, 20, 10, 14, 13, 19, 16, 12, 15};
        for (int hora : horas) {
            citas.add(crearCita(hora, 0, EstadoCita.PENDIENTE));
        }

        List<Cita> ordenadas = agendaService.ordenarPorHora(citas);

        assertEquals(10, ordenadas.size());
        for (int i = 0; i < ordenadas.size() - 1; i++) {
            assertFalse(ordenadas.get(i).getHoraInicio().isAfter(ordenadas.get(i + 1).getHoraInicio()));
        }
    }

    // Dos citas a la misma hora (manicuristas distintas): salen las dos
    @Test
    void citasALaMismaHoraSalenLasDos() {
        List<Cita> citas = List.of(
                crearCita(10, 0, EstadoCita.PENDIENTE),
                crearCita(9, 0, EstadoCita.PENDIENTE),
                crearCita(10, 0, EstadoCita.PENDIENTE));

        List<Cita> ordenadas = agendaService.ordenarPorHora(citas);

        assertEquals(3, ordenadas.size());
        assertEquals(LocalTime.of(9, 0), ordenadas.get(0).getHoraInicio());
    }

    // Un día sin citas da una lista vacía
    @Test
    void sinCitasQuedaVacia() {
        assertTrue(agendaService.ordenarPorHora(List.of()).isEmpty());
    }

    // La siguiente es la primera PENDIENTE: se saltan la pagada y la que está en proceso
    @Test
    void siguienteEsLaPrimeraPendiente() {
        List<Cita> ordenadas = List.of(
                crearCita(10, 0, EstadoCita.PAGADA),
                crearCita(12, 0, EstadoCita.EN_PROCESO),
                crearCita(15, 0, EstadoCita.PENDIENTE),
                crearCita(17, 0, EstadoCita.PENDIENTE));

        Cita siguiente = agendaService.buscarSiguiente(ordenadas);

        assertEquals(LocalTime.of(15, 0), siguiente.getHoraInicio());
    }

    // Si todas están pagadas, no hay siguiente
    @Test
    void sinPendientesNoHaySiguiente() {
        List<Cita> ordenadas = List.of(
                crearCita(10, 0, EstadoCita.PAGADA),
                crearCita(12, 0, EstadoCita.PAGADA));

        assertNull(agendaService.buscarSiguiente(ordenadas));
    }

    // Juntas: desordenadas → se ordenan → la siguiente es la PENDIENTE más temprana
    @Test
    void ordenarYBuscarSiguienteJuntos() {
        List<Cita> citas = List.of(
                crearCita(17, 0, EstadoCita.PENDIENTE),
                crearCita(11, 0, EstadoCita.PAGADA),
                crearCita(14, 0, EstadoCita.PENDIENTE));

        Cita siguiente = agendaService.buscarSiguiente(agendaService.ordenarPorHora(citas));

        assertEquals(LocalTime.of(14, 0), siguiente.getHoraInicio());
    }

    // ── Ayudante: una cita con hora de inicio y estado ──
    private Cita crearCita(int hora, int minuto, EstadoCita estado) {
        Cita cita = new Cita();
        cita.setHoraInicio(LocalTime.of(hora, minuto));
        cita.setHoraFin(LocalTime.of(hora, minuto).plusMinutes(60));
        cita.setEstado(estado);
        return cita;
    }
}