package com.cheapstore.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    @Column(unique = true)
    private String dni;

    private String telefono;

    private String email;

    private String direccion;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Integer getId() { return id;}
    public void setId(Integer id) { this.id = id;}

    public String getNombre() { return nombre;}
    public void setNombre(String nombre) { this.nombre = nombre;}

    public String getDni() { return dni;}
    public void setDni(String dni) { this.dni = dni;}

    public String getTelefono() { return telefono;}
    public void setTelefono(String telefono) { this.telefono = telefono;}

    public String getEmail() { return email;}
    public void setEmail(String email) { this.email = email;}

    public String getDireccion() { return direccion;}
    public void setDireccion(String direccion) { this.direccion = direccion;}

    public LocalDateTime getCreatedAt() { return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt;}
}