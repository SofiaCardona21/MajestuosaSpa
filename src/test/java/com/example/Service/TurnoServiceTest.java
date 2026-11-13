package com.example.Service;

import com.example.Dto.LlegadaRequest;
import com.example.Dto.TurnoResponse;
import com.example.Errores.LogicaNegocioException;
import com.example.Model.OrdenLlegadaManicurista;
import com.example.Model.Personal;
import com.example.Model.Rol;
import com.example.Repository.OrdenLlegadaManicuristaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TurnoServiceTest {

    private OrdenLlegadaManicuristaRepository repository;
    private TablaUsuariosService tablaUsuarios;

    private ColaTurnosManicuristasService colaTurnos;
    private TurnoService turnoService;

    private Personal angelica;
    private Personal jesus;
    private Personal administrador;

    @BeforeEach
    void preparar() {
        // Objetos de mentira: no tocan MySQL ni la tabla hash real
        repository = mock(OrdenLlegadaManicuristaRepository.class);
        tablaUsuarios = mock(TablaUsuariosService.class);

        // La cola sí es real, porque es la estructura que queremos probar
        colaTurnos = new ColaTurnosManicuristasService();
        turnoService = new TurnoService(repository, colaTurnos, tablaUsuarios);

        angelica = crearPersonal("0000000027", "Angélica", Rol.MANICURISTA);
        jesus = crearPersonal("0000000019", "Jesús", Rol.MANICURISTA);
        administrador = crearPersonal("0000000001", "Administrador", Rol.ADMIN);

        // La tabla hash de mentira responde con estas personas
        when(tablaUsuarios.buscarPersonal("0000000027")).thenReturn(angelica);
        when(tablaUsuarios.buscarPersonal("0000000019")).thenReturn(jesus);
        when(tablaUsuarios.buscarPersonal("0000000001")).thenReturn(administrador);
    }

    // La primera que llega queda en la posición 1 y se guarda en MySQL con posición 1
    @Test
    void primeraLlegadaQuedaEnPosicionUno() {
        List<TurnoResponse> orden = turnoService.registrarLlegada(new LlegadaRequest("0000000027"));

        assertEquals(1, orden.size());
        assertEquals(1, orden.get(0).posicion());
        assertEquals("Angélica", orden.get(0).nombre());

        ArgumentCaptor<OrdenLlegadaManicurista> guardada = ArgumentCaptor.forClass(OrdenLlegadaManicurista.class);
        verify(repository).save(guardada.capture());
        assertEquals(1, guardada.getValue().getPosicion());
        assertEquals(LocalDate.now(), guardada.getValue().getFecha());
    }

    // Dos llegadas quedan en el orden en que llegaron
    @Test
    void dosLlegadasQuedanEnOrden() {
        turnoService.registrarLlegada(new LlegadaRequest("0000000027"));
        List<TurnoResponse> orden = turnoService.registrarLlegada(new LlegadaRequest("0000000019"));

        assertEquals(2, orden.size());
        assertEquals("Angélica", orden.get(0).nombre());
        assertEquals(1, orden.get(0).posicion());
        assertEquals("Jesús", orden.get(1).nombre());
        assertEquals(2, orden.get(1).posicion());
    }

    // Un ID que no existe se rechaza y no se guarda nada
    @Test
    void manicuristaQueNoExisteSeRechaza() {
        assertThrows(LogicaNegocioException.class,
                () -> turnoService.registrarLlegada(new LlegadaRequest("9999999999")));

        verify(repository, never()).save(any());
        assertTrue(colaTurnos.estaVacia());
    }

    // El administrador no puede registrarse como llegada
    @Test
    void administradorSeRechaza() {
        assertThrows(LogicaNegocioException.class,
                () -> turnoService.registrarLlegada(new LlegadaRequest("0000000001")));

        verify(repository, never()).save(any());
        assertTrue(colaTurnos.estaVacia());
    }

    // Si ya llegó hoy, se rechaza y la cola no cambia
    @Test
    void llegadaRepetidaSeRechaza() {
        when(repository.existsByManicuristaIdPersonalAndFecha(anyString(), any())).thenReturn(true);

        assertThrows(LogicaNegocioException.class,
                () -> turnoService.registrarLlegada(new LlegadaRequest("0000000027")));

        verify(repository, never()).save(any());
        assertTrue(colaTurnos.estaVacia());
    }

    // Después de un reinicio, la cola se restaura desde MySQL en el mismo orden
    @Test
    void verOrdenRestauraLaColaDesdeMySQL() {
        OrdenLlegadaManicurista primera = crearLlegada(jesus, 1);
        OrdenLlegadaManicurista segunda = crearLlegada(angelica, 2);
        when(repository.findByFechaOrderByPosicionAsc(LocalDate.now())).thenReturn(List.of(primera, segunda));

        List<TurnoResponse> orden = turnoService.verOrden();

        assertEquals(2, orden.size());
        assertEquals("Jesús", orden.get(0).nombre());
        assertEquals("Angélica", orden.get(1).nombre());
    }

    // Si nadie ha llegado hoy, el orden está vacío
    @Test
    void verOrdenSinLlegadasEstaVacio() {
        List<TurnoResponse> orden = turnoService.verOrden();

        assertTrue(orden.isEmpty());
    }

    // ── Ayudantes para crear datos de prueba ──

    private Personal crearPersonal(String id, String nombre, Rol rol) {
        Personal personal = new Personal();
        personal.setIdPersonal(id);
        personal.setNombrePersonal(nombre);
        personal.setRol(rol);
        return personal;
    }

    private OrdenLlegadaManicurista crearLlegada(Personal manicurista, int posicion) {
        OrdenLlegadaManicurista llegada = new OrdenLlegadaManicurista();
        llegada.setManicurista(manicurista);
        llegada.setFecha(LocalDate.now());
        llegada.setPosicion(posicion);
        return llegada;
    }
}