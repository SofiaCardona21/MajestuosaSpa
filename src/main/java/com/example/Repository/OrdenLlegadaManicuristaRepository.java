package com.example.Repository;

import com.example.Model.OrdenLlegadaManicurista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrdenLlegadaManicuristaRepository extends JpaRepository <OrdenLlegadaManicurista, Long> {

    //Todos los métodos buscan en la tabla de la entidad "OrdenLlegadaManicuristaRepository"

    boolean existsByManicuristaIdPersonalAndFecha(String idPersonal, LocalDate fecha); //verificar si ya llego la manicurista

    List<OrdenLlegadaManicurista> findByFechaOrderByPosicionAsc(LocalDate fecha);  //busca la fecha para ordenar de menor a mayor las manicuristas de ese dia

}
