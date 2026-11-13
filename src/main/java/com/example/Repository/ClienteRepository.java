package com.example.Repository;

import com.example.Model.Cliente;
import com.example.Model.Personal;
import com.example.Model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository <Cliente, String> {


}
