package com.cheapstore.backend.controller;

import com.cheapstore.backend.model.Usuario;
import com.cheapstore.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Usuario user) {
        return authService.login(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return authService.logout();
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Usuario user) {
        return authService.register(user);
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verify(@RequestParam("token") String token) {
        return authService.verify(token);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerification(@RequestParam String email) {
        return authService.resendVerification(email);
    }

    //http://localhost:8080/auth/forgot-password?email=susanabarrerajara@gmail.com
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam String email) {
        return authService.forgotPassword(email);
    }

    //http://localhost:8080/auth/reset-password?token=fe29e319-1b17-4cc1-8c2b-bae6db9e5757&newPassword=999999
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {

        return authService.resetPassword(token, newPassword);
    }
}