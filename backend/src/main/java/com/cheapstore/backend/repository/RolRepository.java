package com.cheapstore.backend.repository;

import com.cheapstore.backend.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Integer> {
}