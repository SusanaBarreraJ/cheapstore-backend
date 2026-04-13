package com.cheapstore.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
public class TestDBController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/test-db")
    public String testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            return "Conectado a la BD";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}