package com.example.Config;
//Precargar usuarios


import com.example.Model.Personal;
import com.example.Model.Rol;
import com.example.Model.Servicio;
import com.example.Repository.PersonalRepository;
import com.example.Repository.ServicioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component//Crea un objeto de esa clase
public class DatosIniciales implements CommandLineRunner { //Busca un metodo run para ejecutarlo al final del arranque

    private final PersonalRepository personalRepository;
    private final ServicioRepository servicioRepository;
    private final BCryptPasswordEncoder encoder ;

    public DatosIniciales(PersonalRepository personalRepository, ServicioRepository servicioRepository, BCryptPasswordEncoder encoder) {
        this.personalRepository = personalRepository;
        this.servicioRepository = servicioRepository;
        this.encoder = encoder;
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
        crearServicio("SRV04", "Manicure/ Pedicure semipermanente Hombre", 120, 35000);
        crearServicio("SRV05", "Acrílicas ", 120, 150000);
        crearServicio("SRV05", "Press On", 120, 150000);





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
