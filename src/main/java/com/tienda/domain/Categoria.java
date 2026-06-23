package com.tienda.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data // Generates getters, setters, toString, equals, and hashCode methods.
@Entity // Indicates that this class is a JPA entity mapped to a database table.
@Table(name = "categoria") // Defines the database table associated with this entity.

public class Categoria implements Serializable {
    private static final long serialVersionUID = 1L;

    // Indicates that the database generates the ID automatically.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    // Image path associated with the category. It can have a maximum length of 1024 characters.
    @Column(name = "descripcion", unique = true, nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    private String descripcion;

    @Column(name = "ruta_imagen", length = 1024)
    @Size(max = 1024)
    private String rutaImagen;

    // Indicates whether the category is active or inactive.
    private boolean activo;

}
