package com.cheapstore.backend.service;

import com.cheapstore.backend.model.Cliente;
import com.cheapstore.backend.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repo;

    public List<Cliente> listar() {
        return repo.findAll();
    }

    public Cliente guardar(Cliente c) {
        return repo.save(c);
    }

    public void eliminar(Integer id) {
        repo.deleteById(id);
    }

    public Cliente buscarPorId(Integer id) {
        return repo.findById(id).orElse(null);
    }
}