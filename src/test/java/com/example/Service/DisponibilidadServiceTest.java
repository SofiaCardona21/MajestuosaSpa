package com.example.Service;

import com.example.Model.Cita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DisponibilidadServiceTest {

    private DisponibilidadService servicio;

    private final LocalTime ENTRADA = LocalTime.of(10, 0);
    private final LocalTime SALIDA = LocalTime.of(21, 0);

    @BeforeEach
    void preparar() {
        // Cuando DisponibilidadService reciba repositories en el constructor (paso 5),
        // cambiar por: new DisponibilidadService(null, null, null)
        servicio = new DisponibilidadService(null, null, null, null);
    }

    // Crea una cita de prueba solo con hora de inicio y de fin
    private Cita crearCita(int horaInicio, int minInicio, int horaFin, int minFin) {
        Cita cita = new Cita();
        cita.setHoraInicio(LocalTime.of(horaInicio, minInicio));
        cita.setHoraFin(LocalTime.of(horaFin, minFin));
        return cita;
    }

    // Las horas esperadas del ejemplo de Yari (pruebas 1 y 4)
    private List<LocalTime> horasEjemploYari() {
        return List.of(
                LocalTime.of(13, 0),
                LocalTime.of(16, 30), LocalTime.of(17, 0), LocalTime.of(17, 30),
                LocalTime.of(18, 0), LocalTime.of(18, 30), LocalTime.of(19, 0)
        );
    }

    // 1. Caso normal: dos citas en orden, servicio de 2 horas
    @Test
    void ejemploConDosCitas() {
        List<Cita> citas = List.of(
                crearCita(11, 0, 13, 0),
                crearCita(15, 0, 16, 30)
        );

        List<LocalTime> horas = servicio.calcularHorasDisponible(citas, ENTRADA, SALIDA, 120);

        assertEquals(horasEjemploYari(), horas);
    }

    // 2. Sin citas: se ofrece el día completo, de 10:00 a 19:00
    @Test
    void diaSinCitas() {
        List<LocalTime> horas = servicio.calcularHorasDisponible(List.of(), ENTRADA, SALIDA, 120);

        assertEquals(19, horas.size());
        assertEquals(LocalTime.of(10, 0), horas.get(0));
        assertEquals(LocalTime.of(19, 0), horas.get(horas.size() - 1));
    }

    // 3. Caso límite: un servicio puede terminar justo a la hora de cierre, pero no pasarse
    @Test
    void terminaJustoAlCierre() {
        List<LocalTime> horas = servicio.calcularHorasDisponible(List.of(), ENTRADA, SALIDA, 120);

        assertTrue(horas.contains(LocalTime.of(19, 0)));    // 19:00 + 2 h = 21:00
        assertFalse(horas.contains(LocalTime.of(19, 30)));  // 19:30 + 2 h = 21:30
    }

    // 4. PriorityQueue: aunque las citas lleguen desordenadas, el resultado es el mismo
    @Test
    void citasDesordenadas() {
        List<Cita> citas = List.of(
                crearCita(15, 0, 16, 30),   // entra primero la de la tarde
                crearCita(11, 0, 13, 0)
        );

        List<LocalTime> horas = servicio.calcularHorasDisponible(citas, ENTRADA, SALIDA, 120);

        assertEquals(horasEjemploYari(), horas);
    }

    // 5. Caso límite: un hueco de 30 minutos no alcanza para un servicio de 1 hora
    @Test
    void espacioMuyPequeno() {
        List<Cita> citas = List.of(crearCita(10, 30, 21, 0));

        List<LocalTime> horas = servicio.calcularHorasDisponible(citas, ENTRADA, SALIDA, 60);

        assertTrue(horas.isEmpty());
    }

    // 6. Caso límite: citas pegadas no dejan huecos
    @Test
    void citasPegadas() {
        List<Cita> citas = List.of(
                crearCita(10, 0, 13, 0),
                crearCita(13, 0, 21, 0)
        );

        List<LocalTime> horas = servicio.calcularHorasDisponible(citas, ENTRADA, SALIDA, 60);

        assertTrue(horas.isEmpty());
    }

    // 7. Caso límite: día lleno
    @Test
    void diaLleno() {
        List<Cita> citas = List.of(crearCita(10, 0, 21, 0));

        List<LocalTime> horas = servicio.calcularHorasDisponible(citas, ENTRADA, SALIDA, 60);

        assertTrue(horas.isEmpty());
    }

    // 8. Una cita que termina a una hora "rara" (13:07): se ofrece desde la siguiente media hora
    @Test
    void horaDeFinRara() {
        List<Cita> citas = List.of(crearCita(10, 0, 13, 7));

        List<LocalTime> horas = servicio.calcularHorasDisponible(citas, ENTRADA, SALIDA, 60);

        assertEquals(LocalTime.of(13, 30), horas.get(0));
        assertFalse(horas.contains(LocalTime.of(13, 0)));
        assertEquals(LocalTime.of(20, 0), horas.get(horas.size() - 1));   // 20:00 + 1 h = 21:00
    }

    // 9. Servicio de 90 minutos: horas que no son exactas en horas completas
    @Test
    void servicioDeNoventaMinutos() {
        List<Cita> citas = List.of(crearCita(12, 0, 13, 30));

        List<LocalTime> horas = servicio.calcularHorasDisponible(citas, ENTRADA, SALIDA, 90);

        assertTrue(horas.contains(LocalTime.of(10, 0)));
        assertTrue(horas.contains(LocalTime.of(10, 30)));    // 10:30 + 1:30 = 12:00 justo
        assertFalse(horas.contains(LocalTime.of(11, 0)));    // terminaría 12:30, se cruza
        assertEquals(LocalTime.of(19, 30), horas.get(horas.size() - 1));   // 19:30 + 1:30 = 21:00
    }
}