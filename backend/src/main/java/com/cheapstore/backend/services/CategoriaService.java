package com.cheapstore.backend.service;

import com.cheapstore.backend.model.Categoria;
import com.cheapstore.backend.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository repo;

    public List<Categoria> listar() {
        return repo.findAll();
    }

    public Categoria guardar(Categoria c) {
        return repo.save(c);
    }

    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}