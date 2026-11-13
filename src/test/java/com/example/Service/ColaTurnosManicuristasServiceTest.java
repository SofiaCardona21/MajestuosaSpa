package com.example.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ColaTurnosManicuristasServiceTest {

    private static final String LAURA = "0000000010";
    private static final String MARCELA = "0000000020";
    private static final String DANIELA = "0000000030";

    private ColaTurnosManicuristasService cola;

    @BeforeEach
    void preparar() {
        cola = new ColaTurnosManicuristasService();
    }

    // Llegan tres: deben quedar en el orden en que llegaron
    @Test
    void registrarLlegadasRespetaElOrden() {
        cola.registrarLlegada(LAURA);
        cola.registrarLlegada(MARCELA);
        cola.registrarLlegada(DANIELA);

        assertEquals(List.of(LAURA, MARCELA, DANIELA), cola.verOrden());
    }

    // Una cola nueva está vacía; con una llegada ya no
    @Test
    void estaVaciaAntesYDespuesDeUnaLlegada() {
        assertTrue(cola.estaVacia());

        cola.registrarLlegada(LAURA);

        assertFalse(cola.estaVacia());
    }

    // Todas libres: se asigna la primera y pasa al final
    @Test
    void asignacionNormal() {
        cola.restaurarCola(List.of(LAURA, MARCELA, DANIELA));

        String asignada = cola.asignarPrimeraLibre(List.of(LAURA, MARCELA, DANIELA));

        assertEquals(LAURA, asignada);
        assertEquals(List.of(MARCELA, DANIELA, LAURA), cola.verOrden());
    }

    // La primera está ocupada: se salta, conserva su lugar y se asigna la siguiente libre
    @Test
    void saltaALaOcupadaYConservaSuLugar() {
        cola.restaurarCola(List.of(MARCELA, LAURA, DANIELA));

        String asignada = cola.asignarPrimeraLibre(List.of(LAURA, DANIELA));

        assertEquals(LAURA, asignada);
        assertEquals(List.of(MARCELA, DANIELA, LAURA), cola.verOrden());
    }

    // Ninguna libre: no se asigna nadie y la cola no cambia
    @Test
    void nadieLibreNoCambiaLaCola() {
        cola.restaurarCola(List.of(LAURA, MARCELA, DANIELA));

        String asignada = cola.asignarPrimeraLibre(List.of());

        assertEquals("", asignada);
        assertEquals(List.of(LAURA, MARCELA, DANIELA), cola.verOrden());
    }

    // Cola vacía (nadie ha llegado): no se asigna nadie
    @Test
    void colaVaciaNoAsignaANadie() {
        String asignada = cola.asignarPrimeraLibre(List.of(LAURA));

        assertEquals("", asignada);
        assertTrue(cola.estaVacia());
    }

    // Una manicurista libre que NO ha llegado no se puede asignar
    @Test
    void libreQueNoHaLlegadoNoSeAsigna() {
        cola.restaurarCola(List.of(LAURA, MARCELA));

        String asignada = cola.asignarPrimeraLibre(List.of(DANIELA));

        assertEquals("", asignada);
        assertEquals(List.of(LAURA, MARCELA), cola.verOrden());
    }

    // Varias clientas seguidas con todas libres: el turno rota en orden
    @Test
    void variasAsignacionesSeguidasRotanElTurno() {
        cola.restaurarCola(List.of(LAURA, MARCELA, DANIELA));
        List<String> todas = List.of(LAURA, MARCELA, DANIELA);

        assertEquals(LAURA, cola.asignarPrimeraLibre(todas));
        assertEquals(MARCELA, cola.asignarPrimeraLibre(todas));
        assertEquals(DANIELA, cola.asignarPrimeraLibre(todas));
        assertEquals(LAURA, cola.asignarPrimeraLibre(todas));
    }

    // restaurarCola borra lo que había y deja solo la lista nueva
    @Test
    void restaurarColaReemplazaLoAnterior() {
        cola.registrarLlegada(DANIELA);

        cola.restaurarCola(List.of(LAURA, MARCELA));

        assertEquals(List.of(LAURA, MARCELA), cola.verOrden());
    }

}