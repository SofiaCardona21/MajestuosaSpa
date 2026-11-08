package com.example.Repository;

import com.example.Model.Cita;
import org.springframework.data.jpa.repository.JpaRepository; //Hereda todos los métodos de acá, es un traductor entre
//el service y la BD

public interface CitaRepository extends JpaRepository <Cita, Long> {
}
