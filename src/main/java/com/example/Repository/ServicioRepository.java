package com.example.Repository;

import com.example.Model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioRepository extends JpaRepository <Servicio, String> {
}
