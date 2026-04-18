package com.cheapstore.backend.repository;

import com.cheapstore.backend.model.Usuario;
import com.cheapstore.backend.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByUsuario(Usuario usuario);

}