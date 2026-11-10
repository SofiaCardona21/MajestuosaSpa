package com.example.Service;

import com.example.Dto.InicioSesionRequest;
import com.example.Dto.InicioSesionResponse;
import com.example.Errores.LogicaNegocioException;
import com.example.Model.Cliente;
import com.example.Model.Personal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InicioSesionServices {

    private final TablaUsuariosService mapaConsultarUsuarios;
    private final BCryptPasswordEncoder encoder;

    public InicioSesionServices(TablaUsuariosService mapaConsultarUsuarios, BCryptPasswordEncoder encoder) {
        this.mapaConsultarUsuarios = mapaConsultarUsuarios;
        this.encoder = encoder;
    }


    public InicioSesionResponse iniciarSesion (InicioSesionRequest entradaInicioSesion) {

        Cliente clienteEncontrado = mapaConsultarUsuarios.buscarCliente(entradaInicioSesion.usuario());
        Personal personalEncontrado = mapaConsultarUsuarios.buscarPersonal(entradaInicioSesion.usuario());

        if ((clienteEncontrado != null) && (encoder.matches(entradaInicioSesion.contrasena(), clienteEncontrado.getContrasenaCliente())) ){
            log.info("Cliente ha ingresado correctamente");
            return new InicioSesionResponse(clienteEncontrado.getNumeroTel(), clienteEncontrado.getNombre(), "CLIENTE");
        }
        else if ((personalEncontrado != null) && (encoder.matches(entradaInicioSesion.contrasena(), personalEncontrado.getContrasenaPersonal()))) {
            log.info("Se ha ingresado correctamente como: " + personalEncontrado.getRol().name()); // el .name convierte el valor de un enum en texto
            return new InicioSesionResponse(personalEncontrado.getIdPersonal(), personalEncontrado.getNombrePersonal(), personalEncontrado.getRol().name());
        } else {
            log.warn("No se pudo iniciar sesión");
            throw new LogicaNegocioException("Usuario o contraseña incorrectos, no es posible iniciar sesión");

        }
    }
}
