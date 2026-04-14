package com.cheapstore.backend.service;

import com.cheapstore.backend.model.Usuario;
import com.cheapstore.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public Usuario guardar(Usuario u) {
        u.setPasswordHash(passwordEncoder.encode(u.getPasswordHash()));
        return repo.save(u);
    }

    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}