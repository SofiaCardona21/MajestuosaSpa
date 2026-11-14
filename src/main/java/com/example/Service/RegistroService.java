package com.example.Service;

import com.example.Dto.ClienteResponse;
import com.example.Dto.RegistroRequest;
import com.example.Errores.LogicaNegocioException;
import com.example.Model.Cliente;
import com.example.Repository.ClienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j // para generar e instanciar automaticamente el log y poder hacer log.info, log.warn y asi...
@Service
public class RegistroService {

    private final ClienteRepository clienteRepository;
    private final TablaUsuariosService mapaConsultarUsuarios;
    private final BCryptPasswordEncoder encoder;

    public RegistroService(ClienteRepository clienteRepository, TablaUsuariosService mapaConsultarClientes, BCryptPasswordEncoder encoder) {
        this.clienteRepository = clienteRepository;
        this.mapaConsultarUsuarios = mapaConsultarClientes;
        this.encoder = encoder;
    }

    public ClienteResponse registrarCliente (RegistroRequest entradaRegistro) {

        if(mapaConsultarUsuarios.existeCliente(entradaRegistro.numeroTelCliente())){

            log.warn("Registro rechazado: el celular " + entradaRegistro.numeroTelCliente() + " ya existe");// log para informar q ya existe
            throw new LogicaNegocioException("Este celular ya está registrado"); //aca se manda el error al atrapador de errores (los errores de la lógica de negocio)
                                                                                 //y se sale del metodo y ya el se encarga de mostrarle ese mensaje al usuario
        }

        Cliente clienteARegistrar = new Cliente(entradaRegistro.numeroTelCliente(), entradaRegistro.nombreCliente(), encoder.encode(entradaRegistro.contrasena()),0,false);

        clienteRepository.save(clienteARegistrar);

        mapaConsultarUsuarios.agregarCliente(clienteARegistrar);

        log.info("Registro exitoso: se ha registrado un cliente con id: " + clienteARegistrar.getNumeroTel());// log para informar q ya se resgitro

        return new ClienteResponse(clienteARegistrar.getNumeroTel(), clienteARegistrar.getNombre(), 0, FidelidadService.PUNTOS_PARA_BONO, 0);    }
}