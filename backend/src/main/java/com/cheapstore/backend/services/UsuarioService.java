package com.cheapstore.backend.service;

import com.cheapstore.backend.model.Usuario;
import com.cheapstore.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repo;

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public Usuario guardar(Usuario u) {
        return repo.save(u);
    }

    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}