package com.tienda.repository;

import com.tienda.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer>{

    // Derived query method. This will be explained in more detail in week 8.
    public List<Producto> findByActivoTrue();

}
