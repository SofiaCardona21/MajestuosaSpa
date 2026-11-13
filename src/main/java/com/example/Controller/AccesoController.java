package com.example.Controller;

import com.example.Dto.ClienteResponse;
import com.example.Dto.InicioSesionRequest;
import com.example.Dto.InicioSesionResponse;
import com.example.Dto.RegistroRequest;
import com.example.Service.InicioSesionService;
import com.example.Service.RegistroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//DOCUMENTACIÓN IA: Claude opus 5.5, quisimos investigar que respuestas podria devolver ResponseEntity para entender mejor y son estas:
// 200 OK
// 201 Created
// 204 No Content
// 302 Found
// 400 Bad Request
// 401 Unauthorized
// 404 Not Found
// 500 Internal Server Error
// 502 Bad Gateway

@RestController // Se usa este y no el controller pq necesitamos devolver datos al usuario en JSON
@RequestMapping("/acceso") // es como la ruta url para cuando se invoquen los métodos de este controller
public class AccesoController {

    //acá se ponen los services que son usados en el controller
    private final RegistroService registroService;
    private final InicioSesionService inicioSesionService;

    public AccesoController(RegistroService registroService, InicioSesionService inicioSesionService) {
        this.registroService = registroService;
        this.inicioSesionService = inicioSesionService;
    }

    @PostMapping("/registro") // se usa para enviar datos nuevos al servidor o procesar datos que recibe de usuario
    public ResponseEntity<ClienteResponse> registrar(@Valid @RequestBody RegistroRequest entradaRegistro) {  // ResponseEntity es lo que va a retornar
                                                                                                             // osea una la respuesta http
        ClienteResponse clienteCreado = registroService.registrarCliente(entradaRegistro);
        return new ResponseEntity<>(clienteCreado, HttpStatus.CREATED); //retorna la respuesta completa HTTP el cliente (JSON) y el estado http
    }

    @PostMapping("/Inicio_Sesión")
    public ResponseEntity<InicioSesionResponse> iniciarSesion(@Valid @RequestBody InicioSesionRequest entradaInicioSesion) {
        InicioSesionResponse usuarioIngresado = inicioSesionService.iniciarSesion(entradaInicioSesion);
        return ResponseEntity.ok(usuarioIngresado);
    }
}
