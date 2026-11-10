package com.example.Config;
//Precargar usuarios


import com.example.Dto.ServicioResponse;
import com.example.Model.Cliente;
import com.example.Model.Personal;
import com.example.Model.Rol;
import com.example.Model.Servicio;
import com.example.Repository.ClienteRepository;
import com.example.Repository.PersonalRepository;
import com.example.Repository.ServicioRepository;
import com.example.Service.TablaUsuariosService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component//Crea un objeto de esa clase
public class DatosIniciales implements CommandLineRunner { //Busca un metodo run para ejecutarlo al final del arranque

    private final PersonalRepository personalRepository;
    private final ServicioRepository servicioRepository;
    private final BCryptPasswordEncoder encoder ;
    private final ClienteRepository clienteRepository; // Para poder leer los clientes de MySQL
    private final TablaUsuariosService tablasUsuarios; // Para poder usar los metodos de agregar y poder poner los datos precargados en el hashmap

    public DatosIniciales(PersonalRepository personalRepository, ServicioRepository servicioRepository,
                          BCryptPasswordEncoder encoder, ClienteRepository clienteRepository,
                          TablaUsuariosService tablasUsuarios) {

        this.personalRepository = personalRepository;
        this.servicioRepository = servicioRepository;
        this.encoder = encoder;
        this.clienteRepository = clienteRepository;
        this.tablasUsuarios = tablasUsuarios;
    }

    @Override
    public void run(String... args) {//Métedo que busca el commandLine

        crearPersonal("0000000001", "Administrador", "adminmajestuosa123", Rol.ADMIN, LocalTime.of(10, 0), LocalTime.of(20, 0) );
        crearPersonal("0000000027", "Angélica", "angie1234", Rol.MANICURISTA, LocalTime.of(10, 0), LocalTime.of(20, 0) );
        crearPersonal("0000000067", "Sofia", "sofialapro67", Rol.MANICURISTA, LocalTime.of(10, 0), LocalTime.of(20, 0) );
        crearPersonal("0000000019", "Jesús", "jesusmajestuosa", Rol.MANICURISTA, LocalTime.of(10, 0), LocalTime.of(20, 0) );
        crearPersonal("0000000047", "Verónica", "vero0489", Rol.MANICURISTA, LocalTime.of(10, 0), LocalTime.of(20, 0) );
        crearPersonal("0000000084", "Paulina", "pau2456", Rol.MANICURISTA, LocalTime.of(10, 0), LocalTime.of(20, 0) );

        crearServicio("SRV01", "Manicure tradicional", 60, 32000);
        crearServicio("SRV02", "Pedicure tradicional", 60, 35000);
        crearServicio("SRV03", "Manicure semipermanente Mujer", 90, 55000);
        crearServicio("SRV04", "Pedicure semipermanente Mujer", 90, 60000);
        crearServicio("SRV05", "Manicure/ Pedicure semipermanente Hombre", 120, 35000);
        crearServicio("SRV06", "Acrílicas ", 120, 150000);
        crearServicio("SRV07", "Press On", 120, 150000);

        for (Personal personalPrecargado : personalRepository.findAll()) {  //recorremos todos los datos precargados y los añadimos a la tabla
            tablasUsuarios.agregarPersonal(personalPrecargado);
        }

        for (Cliente clienteYaRegistrado : clienteRepository.findAll()) {  //este for es pq cuando se registran clientes quedan en la BD
            tablasUsuarios.agregarCliente(clienteYaRegistrado);            //Pero los datos en el hash map se van borrando ent siempre se necesitan cargarlos al inciar el programa
        }

    }


    private void crearPersonal (String idPersonal, String nombrePersonal, String contrasenaPersonal, Rol rol, LocalTime horaEntrada,  LocalTime horaSalida ){

        if (personalRepository.existsById(idPersonal)){
            return;
        } else {
            Personal P = new Personal();
            P.setIdPersonal(idPersonal);
            P.setNombrePersonal(nombrePersonal);
            P.setContrasenaPersonal(encoder.encode(contrasenaPersonal)); //Para cifrar
            P.setRol(rol);
            P.setHoraEntrada(horaEntrada);
            P.setHoraSalida(horaSalida);

            personalRepository.save(P); //Lo lleva a Personal de la BD
        }
    }

    private void crearServicio (String idServicio, String nombreServicio, int duracionMinutos, double precio){
        if (servicioRepository.existsById(idServicio)) {
            return;
        }
        Servicio s = new Servicio();

        s.setIdServicio(idServicio);
        s.setNombreServicio(nombreServicio);
        s.setDuracionMinutos(duracionMinutos);
        s.setPrecio(precio);

        servicioRepository.save(s);
    }
}
