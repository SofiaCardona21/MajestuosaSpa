package com.example.Service;

import com.example.Dto.ClienteResponse;
import com.example.Errores.LogicaNegocioException;
import com.example.Model.Cliente;
import com.example.Repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FidelidadServiceTest {

    private static final String CELULAR = "3001234567";

    private ClienteRepository clienteRepository;
    private TablaUsuariosService tablaUsuarios;
    private FidelidadService fidelidadService;
    private Cliente cliente;

    @BeforeEach
    void preparar() {
        // Objetos de mentira: no tocan MySQL ni la tabla hash real
        clienteRepository = mock(ClienteRepository.class);
        tablaUsuarios = mock(TablaUsuariosService.class);
        fidelidadService = new FidelidadService(clienteRepository, tablaUsuarios);

        // Una clienta sin puntos ni bono, que la tabla hash de mentira devuelve
        cliente = new Cliente();
        cliente.setNumeroTel(CELULAR);
        cliente.setNombre("Laura");
        cliente.setPuntos(0);
        cliente.setBonosDisponibles(false);
        when(tablaUsuarios.buscarCliente(CELULAR)).thenReturn(cliente);
    }

    // 5 puntos + 2 servicios = 7 → bono y queda 1 punto (la regla de la guía)
    @Test
    void cincoMasDosQuedaEnUnoConBono() {
        cliente.setPuntos(5);

        fidelidadService.sumarPuntos(CELULAR, 2);

        assertEquals(1, cliente.getPuntos());
        assertTrue(cliente.isBonosDisponibles());
        verify(clienteRepository).save(cliente);
    }

    // Exactamente 6 → bono y 0 puntos
    @Test
    void seisExactosDanUnBono() {
        cliente.setPuntos(4);

        fidelidadService.sumarPuntos(CELULAR, 2);

        assertEquals(0, cliente.getPuntos());
        assertTrue(cliente.isBonosDisponibles());
    }

    // Menos de 6 → solo suma puntos, sin bono
    @Test
    void menosDeSeisNoDanBono() {
        fidelidadService.sumarPuntos(CELULAR, 3);

        assertEquals(3, cliente.getPuntos());
        assertFalse(cliente.isBonosDisponibles());
    }

    // Solo se tiene un bono a la vez: 5 + 8 = 13 → un bono y quedan 7 puntos guardados
    @Test
    void trecePuntosDanUnSoloBonoYConservanElResto() {
        cliente.setPuntos(5);

        fidelidadService.sumarPuntos(CELULAR, 8);

        assertEquals(7, cliente.getPuntos());
        assertTrue(cliente.isBonosDisponibles());
    }

    // Si ya tenía un bono sin usar, los puntos se acumulan y NO se convierten en otro bono
    @Test
    void conBonoSinUsarLosPuntosSoloSeAcumulan() {
        cliente.setPuntos(5);
        cliente.setBonosDisponibles(true);

        fidelidadService.sumarPuntos(CELULAR, 1);

        assertEquals(6, cliente.getPuntos());
        assertTrue(cliente.isBonosDisponibles());
    }

    // Una clienta que no existe se rechaza y no se guarda nada
    @Test
    void sumarPuntosAClientaQueNoExisteSeRechaza() {
        assertThrows(LogicaNegocioException.class,
                () -> fidelidadService.sumarPuntos("3009999999", 2));

        verify(clienteRepository, never()).save(any());
    }

    // Con bono: se gasta y responde true
    @Test
    void usarBonoConBonoLoGasta() {
        cliente.setBonosDisponibles(true);

        boolean usado = fidelidadService.usarBono(cliente);

        assertTrue(usado);
        assertFalse(cliente.isBonosDisponibles());
        verify(clienteRepository).save(cliente);
    }

    // Sin bono: responde false y no guarda nada
    @Test
    void usarBonoSinBonoNoHaceNada() {
        boolean usado = fidelidadService.usarBono(cliente);

        assertFalse(usado);
        assertFalse(cliente.isBonosDisponibles());
        verify(clienteRepository, never()).save(any());
    }

    // El descuento nunca deja el valor por debajo de 0
    @Test
    void valorConBonoNoBajaDeCero() {
        assertEquals(25000.0, fidelidadService.valorConBono(55000));
        assertEquals(2000.0, fidelidadService.valorConBono(32000));
        assertEquals(0.0, fidelidadService.valorConBono(20000));
    }

    // Consultar una clienta que existe devuelve sus datos de fidelidad
    @Test
    void consultarClienteDevuelveSusDatos() {
        cliente.setPuntos(4);
        cliente.setBonosDisponibles(true);

        ClienteResponse respuesta = fidelidadService.consultarCliente(CELULAR);

        assertEquals(CELULAR, respuesta.numeroTel());
        assertEquals("Laura", respuesta.nombre());
        assertEquals(4, respuesta.puntos());
        assertEquals(2, respuesta.puntosParaBono());   // le faltan 2 para el siguiente bono
        assertEquals(1, respuesta.bonosDisponibles()); // tiene un bono sin usar
    }

    // Sin bono, el perfil muestra 0 bonos
    @Test
    void consultarClienteSinBonoMuestraCeroBonos() {
        cliente.setPuntos(3);

        ClienteResponse respuesta = fidelidadService.consultarCliente(CELULAR);

        assertEquals(3, respuesta.puntosParaBono());
        assertEquals(0, respuesta.bonosDisponibles());
    }

    // Consultar una clienta que no existe se rechaza
    @Test
    void consultarClienteQueNoExisteSeRechaza() {
        assertThrows(LogicaNegocioException.class,
                () -> fidelidadService.consultarCliente("3009999999"));
    }
}