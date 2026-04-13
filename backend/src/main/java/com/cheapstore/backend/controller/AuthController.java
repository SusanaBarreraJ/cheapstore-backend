package com.cheapstore.backend.controller;

import com.cheapstore.backend.model.Usuario;
import com.cheapstore.backend.repository.UsuarioRepository;
import com.cheapstore.backend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Usuario user) {

        Usuario dbUser = repo.findAll().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .findFirst()
                .orElse(null);

        Map<String, String> response = new HashMap<>();

        if (dbUser == null) {
            response.put("error", "Usuario no existe");
            return response;
        }

        if (!dbUser.getPasswordHash().equals(user.getPasswordHash())) {
            response.put("error", "Password incorrecto");
            return response;
        }

        String token = jwtService.generateToken(dbUser.getEmail());

        response.put("token", token);
        return response;
    }
}