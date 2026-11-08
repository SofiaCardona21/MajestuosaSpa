package com.example.Repository;

import com.example.Model.Personal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalRepository extends JpaRepository <Personal, String> {
}
