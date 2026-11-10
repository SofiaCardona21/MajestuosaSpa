package com.example.Service;

import com.example.Model.Cliente;
import com.example.Model.Personal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TablaUsuariosServiceTest {
    private TablaUsuariosService tablas;

    @BeforeEach
    void prepararTabla() {
        tablas = new TablaUsuariosService();   // cada prueba empieza con una tabla vacía
    }

    // Crea un Personal de prueba (para no repetir los setters en cada prueba)
    private Personal crearPersonal(String id, String nombre) {
        Personal p = new Personal();
        p.setIdPersonal(id);
        p.setNombrePersonal(nombre);
        return p;
    }

    // 1. Caso normal: agregar un cliente y encontrarlo por su celular
    @Test
    void agregarYBuscarCliente() {
        Cliente laura = new Cliente("3001234567", "Laura", "clave", 0, false);

        tablas.agregarCliente(laura);
        Cliente encontrado = tablas.buscarCliente("3001234567");

        assertNotNull(encontrado);
        assertEquals("Laura", encontrado.getNombre());
    }

    // 2. Caso normal: un cliente agregado sí existe
    @Test
    void existeClienteAgregado() {
        Cliente laura = new Cliente("3001234567", "Laura", "clave", 0, false);

        tablas.agregarCliente(laura);

        assertTrue(tablas.existeCliente("3001234567"));
    }

    // 3. Caso límite: buscar en una tabla vacía devuelve null
    @Test
    void buscarClienteQueNoExiste() {
        Cliente encontrado = tablas.buscarCliente("3009999999");

        assertNull(encontrado);
    }

    // 4. Caso límite: un celular que no se agregó no existe
    @Test
    void existeClienteNoAgregado() {
        Cliente laura = new Cliente("3001234567", "Laura", "clave", 0, false);
        tablas.agregarCliente(laura);

        assertFalse(tablas.existeCliente("3007654321"));
    }

    // 5. Caso límite: la clave es única; agregar el mismo celular reemplaza al anterior
    @Test
    void agregarMismoCelularReemplaza() {
        Cliente primero = new Cliente("3001234567", "Laura", "clave", 0, false);
        Cliente segundo = new Cliente("3001234567", "Laura Gómez", "clave", 3, false);

        tablas.agregarCliente(primero);
        tablas.agregarCliente(segundo);

        assertEquals("Laura Gómez", tablas.buscarCliente("3001234567").getNombre());
    }

    // 6. Caso normal: agregar a alguien del personal y encontrarlo por su ID
    @Test
    void agregarYBuscarPersonal() {
        Personal sofia = crearPersonal("0000000010", "Sofía");

        tablas.agregarPersonal(sofia);
        Personal encontrada = tablas.buscarPersonal("0000000010");

        assertNotNull(encontrada);
        assertEquals("Sofía", encontrada.getNombrePersonal());
    }

    // 7. Caso límite: buscar un ID que no existe devuelve null
    @Test
    void buscarPersonalQueNoExiste() {
        assertNull(tablas.buscarPersonal("0000000099"));
    }

    // 8. Las dos tablas son independientes: un cliente no aparece en el personal
    @Test
    void clienteNoApareceEnPersonal() {
        Cliente laura = new Cliente("3001234567", "Laura", "clave", 0, false);
        tablas.agregarCliente(laura);

        assertNull(tablas.buscarPersonal("3001234567"));
    }

}