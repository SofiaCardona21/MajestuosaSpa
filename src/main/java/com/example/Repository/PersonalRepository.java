package com.example.Repository;

import com.example.Model.Personal;
import com.example.Model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalRepository extends JpaRepository <Personal, String> {

    List<Personal> findByRol (Rol rol); //Consulta las manicuristas

}
