package com.example.Repository;

import com.example.Model.Cita;
import com.example.Model.Personal;
import com.example.Model.Rol;
import org.springframework.data.jpa.repository.JpaRepository; //Hereda todos los métodos de acá, es un traductor entre

import java.time.LocalDate;
import java.util.List;
//el service y la BD

public interface CitaRepository extends JpaRepository <Cita, Long> {

    //BUSQUEDAS EN LA TABLA CITA, todos los metodos

    //CONSULTAS

    List<Cita> findByManicuristaIdPersonalAndFecha(String idPersonal, LocalDate fecha);
    //Busca en la tabla CITAS las citas del manicurista idPersonal en el día fecha (nombre necesario)

    List <Cita> findByClienteNumeroTel(String numeroTel); //Las citas de una clienta

    List <Cita> findByFecha(LocalDate fecha);

}

