package com.example.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration //avisar que hay que leer una configuración

public class SeguridadContrasena {
    @Bean //guardar lo q retorna y enviarlo
    public BCryptPasswordEncoder encriptar (){
        return new BCryptPasswordEncoder();

    }
}
