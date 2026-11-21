package com.smartpoweron.gestion_materiales.security;

import com.smartpoweron.gestion_materiales.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String hashCorrecto = passwordEncoder.encode("123456");
        usuarioRepository.findAll().forEach(u -> {
            if (!passwordEncoder.matches("123456", u.getPasswordHash())) {
                u.setPasswordHash(hashCorrecto);
                usuarioRepository.save(u);
            }
        });
    }
}
