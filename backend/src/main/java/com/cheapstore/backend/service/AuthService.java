package com.cheapstore.backend.service;

import com.cheapstore.backend.model.Rol;
import com.cheapstore.backend.model.TokenType;
import com.cheapstore.backend.model.Usuario;

import com.cheapstore.backend.repository.UsuarioRepository;
import com.cheapstore.backend.repository.RolRepository;

import com.cheapstore.backend.security.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.cheapstore.backend.model.VerificationToken;

@Service
public class AuthService {
    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private VerificationTokenService tokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<Map<String, String>> login(Usuario user) {

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

    public ResponseEntity<Map<String, String>> register(Usuario user) {

        Map<String, String> response = new HashMap<>();

        if (repo.findByEmail(user.getEmail()).isPresent()) {
            response.put("error", "Email ya registrado");
            return ResponseEntity.status(400).body(response);
        }

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        user.setEstado(true);
        user.setVerified(false);

        if (user.getRol() == null || user.getRol().getId() == null) {
            response.put("error", "Debe enviar rol");
            return ResponseEntity.status(400).body(response);
        }

        Rol rol = rolRepository.findById(user.getRol().getId())
                .orElseThrow(() -> new RuntimeException("Rol no existe"));

        user.setRol(rol);

        Usuario saved = repo.save(user);

        VerificationToken vt = tokenService.createToken(saved, TokenType.ACTIVACION);

        String link = "http://localhost:8080/auth/verify?token=" + vt.getToken();

        emailService.sendVerificationEmail(saved.getEmail(), link);

        response.put("message", "Usuario creado. Verifica tu cuenta.");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<String> verify(String token) {

        VerificationToken vt = tokenService.validateToken(token);

        Usuario user = vt.getUsuario();

        if (user.getVerified()) {
            tokenService.deleteToken(vt);
            return ResponseEntity.badRequest().body("La cuenta ya está verificada");
        }

        user.setVerified(true);
        repo.save(user);

        tokenService.deleteToken(vt);

        return ResponseEntity.ok("Cuenta verificada correctamente");
    }

    public ResponseEntity<String> forgotPassword(String email) {

        Optional<Usuario> optionalUser = repo.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("Usuario no registrado");
        }

        Usuario user = optionalUser.get();

        VerificationToken vt =
                tokenService.createToken(user, TokenType.RESET_PASSWORD);

        String link = "http://localhost:8080/auth/reset-password?token=" + vt.getToken();

        emailService.sendResetPasswordEmail(user.getEmail(), link);

        return ResponseEntity.ok("Correo enviado para recuperar contraseña");
    }

    public ResponseEntity<String> resetPassword(String token, String newPassword) {

        VerificationToken vt = tokenService.validateToken(token);

        if (vt.getTipo() != TokenType.RESET_PASSWORD) {
            return ResponseEntity.badRequest().body("Token inválido");
        }

        Usuario user = vt.getUsuario();

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordUpdatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        repo.save(user);

        tokenService.deleteToken(vt);

        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    public ResponseEntity<String> logout() {

        return ResponseEntity.ok("Sesión cerrada correctamente");
    }

    public ResponseEntity<String> resendVerification(String email) {

        Usuario user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getVerified()) {
            return ResponseEntity.badRequest().body("El usuario ya está verificado");
        }

        VerificationToken vt = tokenService.createToken(user, TokenType.ACTIVACION);

        String link = "http://localhost:8080/auth/verify?token=" + vt.getToken();

        emailService.sendVerificationEmail(user.getEmail(), link);
        return ResponseEntity.ok("Correo de verificación reenviado");
    }

}
