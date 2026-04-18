package com.cheapstore.backend.service;

import com.cheapstore.backend.model.Rol;
import com.cheapstore.backend.model.TokenType;
import com.cheapstore.backend.model.Usuario;
import com.cheapstore.backend.model.VerificationToken;

import com.cheapstore.backend.repository.RolRepository;
import com.cheapstore.backend.repository.UsuarioRepository;

import com.cheapstore.backend.security.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    @Value("${app.base-url}")
    private String baseUrl;

    public ResponseEntity<Map<String, String>> login(Usuario user) {
        Usuario dbUser = repo.findByEmail(user.getEmail()).orElseThrow(() -> new RuntimeException("Usuario no existe"));;
        if (!dbUser.getEstado()) {
            return error("Usuario desactivado", 403);
        }
        if (!dbUser.getVerified()) {
            return error("Cuenta no verificada", 403);
        }
        if (!passwordEncoder.matches(user.getPasswordHash(), dbUser.getPasswordHash())) {
            return error("Password incorrecto", 401);
        }
        String token = jwtService.generateToken(dbUser);
        return success("token", token);
    }

    public ResponseEntity<Map<String, String>> register(Usuario user) {
        if (repo.findByEmail(user.getEmail()).isPresent()) {
            return error("Email ya registrado", 400);
        }
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setEstado(true);
        user.setVerified(false);
        if (user.getRol() == null || user.getRol().getId() == null) {
            return error("Debe enviar rol", 400);
        }
        Rol rol = rolRepository.findById(user.getRol().getId()).orElseThrow(() -> new RuntimeException("Rol no existe"));
        user.setRol(rol);
        Usuario saved = repo.save(user);
        sendVerification(saved);
        return success("message", "Usuario creado. Verifica tu cuenta.");
    }

    public ResponseEntity<Map<String, String>> verify(String token) {
        VerificationToken vt = tokenService.validateToken(token);
        Usuario user = vt.getUsuario();
        if (user.getVerified()) {
            tokenService.deleteToken(vt);
            return error("La cuenta ya está verificada", 400);
        }
        user.setVerified(true);
        repo.save(user);
        tokenService.deleteToken(vt);
        return success("message", "Cuenta verificada correctamente");
    }

    public ResponseEntity<Map<String, String>> forgotPassword(String email) {
        Usuario user = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no registrado"));
        tokenService.deleteAllByUsuario(user);
        VerificationToken vt = tokenService.createToken(user, TokenType.RESET_PASSWORD);
        String link = buildUrl("/auth/reset-password", vt.getToken());
        emailService.sendResetPasswordEmail(user.getEmail(), link);
        return success("message", "Correo enviado para recuperar contraseña");
    }

    public ResponseEntity<Map<String, String>> resetPassword(String token, String newPassword) {
        VerificationToken vt = tokenService.validateToken(token);
        if (vt.getTipo() != TokenType.RESET_PASSWORD) {
            return error("Token inválido", 400);
        }
        Usuario user = vt.getUsuario();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordUpdatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        repo.save(user);
        tokenService.deleteToken(vt);
        return success("message", "Contraseña actualizada correctamente");
    }

    public ResponseEntity<Map<String, String>> logout() {
        return success("message", "Sesión cerrada correctamente");
    }

    public ResponseEntity<Map<String, String>> resendVerification(String email) {
        Usuario user = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (user.getVerified()) {
            return error("El usuario ya está verificado", 400);
        }
        sendVerification(user);
        return success("message", "Correo de verificación reenviado");
    }

    private void sendVerification(Usuario user) {
        tokenService.deleteAllByUsuario(user);
        VerificationToken vt = tokenService.createToken(user, TokenType.ACTIVACION);
        String link = buildUrl("/auth/verify", vt.getToken());
        emailService.sendVerificationEmail(user.getEmail(), link);
    }

    private String buildUrl(String path, String token) {
        return baseUrl + path + "?token=" + token;
    }

    private ResponseEntity<Map<String, String>> error(String message, int status) {
        Map<String, String> res = new HashMap<>();
        res.put("error", message);
        return ResponseEntity.status(status).body(res);
    }

    private ResponseEntity<Map<String, String>> success(String key, String value) {
        Map<String, String> res = new HashMap<>();
        res.put(key, value);
        return ResponseEntity.ok(res);
    }
}