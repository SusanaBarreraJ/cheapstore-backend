package com.cheapstore.backend.service;

import com.cheapstore.backend.model.TokenType;
import com.cheapstore.backend.model.Usuario;
import com.cheapstore.backend.model.VerificationToken;
import com.cheapstore.backend.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificationTokenService {

    @Autowired
    private VerificationTokenRepository repo;

    public VerificationToken createToken(Usuario user, TokenType tipo) {

        String token = UUID.randomUUID().toString();

        VerificationToken vt = new VerificationToken();
        vt.setToken(token);
        vt.setUsuario(user);
        vt.setTipo(tipo);
        vt.setExpiryDate(LocalDateTime.now().plusHours(1));

        return repo.save(vt);
    }

    public VerificationToken validateToken(String token) {

        VerificationToken vt = repo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (vt.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        return vt;
    }

    public void deleteToken(VerificationToken vt) {
        repo.delete(vt);
    }

    public void deleteAllByUsuario(Usuario usuario) {
        repo.deleteByUsuario(usuario);
    }
}