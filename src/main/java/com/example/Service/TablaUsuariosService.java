package com.example.Service;

import com.example.Model.Cliente;
import com.example.Model.Personal;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component  // nos sirve para instanciar automáticamente cuando se arranca el programa
public class TablaUsuariosService {

    private final Map<String, Cliente> mapaConsultarClientes = new HashMap<>();
    private final Map<String, Personal> mapaConsultarPersonal = new HashMap<>();
    //    Clave, valor

    public boolean existeCliente (String numeroCelCliente){
        return mapaConsultarClientes.containsKey(numeroCelCliente);
    }

    public void agregarCliente (Cliente clienteNuevo){
        mapaConsultarClientes.put(clienteNuevo.getNumeroTel(), clienteNuevo);
    }

    public Cliente buscarCliente (String numeroCelCliente){
        return mapaConsultarClientes.get(numeroCelCliente);
    }

    public void agregarPersonal (Personal personalNuevo){
        mapaConsultarPersonal.put(personalNuevo.getIdPersonal(), personalNuevo);
    }

    public Personal buscarPersonal (String idPersonal){
        return mapaConsultarPersonal.get(idPersonal);
    }

}
