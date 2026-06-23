package com.tienda.service;

import com.tienda.domain.Categoria;
import com.tienda.repository.CategoriaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    // Repository used to access categoria data from the database.
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // Returns all categories or only active categories, depending on the activo parameter.
    @Transactional(readOnly =true)
    public List<Categoria> getCategorias(boolean activo){
        if (activo){
            return categoriaRepository.findByActivoTrue();

        }
        return categoriaRepository.findAll();
    }
}
