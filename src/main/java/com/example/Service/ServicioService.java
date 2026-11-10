package com.example.Service;

import com.example.Dto.ServicioResponse;
import com.example.Model.Servicio;
import com.example.Repository.ServicioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service //Es para indicar que hace parte de la logica de negocio
public class ServicioService {

    private final ServicioRepository servicioRepository;

    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    public List<ServicioResponse> listarServicios() {

        List<Servicio> serviciosBD = servicioRepository.findAll(); //creamos la lista con todos los servicios q precargamos en la BD

        List<ServicioResponse> listaRespuesta = new ArrayList<>();  //creamos una lista para mostrar los servicios

        for (Servicio s : serviciosBD) {

            ServicioResponse servicioAListar = new ServicioResponse(     //Los recorremos y los vamos agg a la lista de respuesta
                    s.getIdServicio(),
                    s.getNombreServicio(),
                    s.getDuracionMinutos(),
                    s.getPrecio()
            );
            listaRespuesta.add(servicioAListar);
        }

        return listaRespuesta;
    }
}
