package com.cheapstore.backend.controller;

import com.cheapstore.backend.model.Usuario;
import com.cheapstore.backend.repository.UsuarioRepository;
import com.cheapstore.backend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Usuario user) {

        Usuario dbUser = repo.findByEmail(user.getEmail())
                .orElse(null);

        Map<String, String> response = new HashMap<>();

        if (dbUser == null) {
            response.put("error", "Usuario no existe");
            return ResponseEntity.status(401).body(response);
        }

        if (!dbUser.getEstado()) {
            response.put("error", "Usuario desactivado");
            return ResponseEntity.status(403).body(response);
        }

        if (!dbUser.getVerified()) {
            response.put("error", "Cuenta no verificada");
            return ResponseEntity.status(403).body(response);
        }

        if (!passwordEncoder.matches(user.getPasswordHash(), dbUser.getPasswordHash())) {
            response.put("error", "Password incorrecto");
            return ResponseEntity.status(401).body(response);
        }

        String token = jwtService.generateToken(dbUser.getEmail());

        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Usuario user) {

        Map<String, String> response = new HashMap<>();

        if (repo.findByEmail(user.getEmail()).isPresent()) {
            response.put("error", "Email ya registrado");
            return ResponseEntity.status(400).body(response);
        }

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        user.setEstado(true);
        user.setVerified(false);

        Usuario saved = repo.save(user);

        response.put("message", "Usuario creado. Verifica tu cuenta.");
        return ResponseEntity.ok(response);
    }
}