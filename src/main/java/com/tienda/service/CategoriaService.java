package com.tienda.service;

import com.tienda.FirebaseStorageService;
import com.tienda.domain.Categoria;
import com.tienda.repository.CategoriaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {
    // Repository used to access categoria data from the database.
    private final CategoriaRepository categoriaRepository;

    private final FirebaseStorageService firebaseStorageService;

    public CategoriaService(CategoriaRepository categoriaRepository, FirebaseStorageService firebaseStorageService) {
        this.categoriaRepository = categoriaRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    // Returns all categories or only active categories, depending on the activo parameter.
    @Transactional(readOnly =true)
    public List<Categoria> getCategorias(boolean activo){
        if (activo){
            return categoriaRepository.findByActivoTrue();

        }
        return categoriaRepository.findAll();
    }

    // recupera 1 reguistro de categoria
    @Transactional(readOnly =true)
    public Optional<Categoria> getCategoria(Integer idCategoria){
        return categoriaRepository.findById(idCategoria);
    }

    // si  categoria tra un id categoria se atualia el regstro, sino se crea
    @Transactional
    public void save(Categoria categoria, MultipartFile imagenFile){ //Multipartfoile es el tipo de dato de la iamgen
        //se salva la categoria
        categoriaRepository.save(categoria); //o actualiza o inserta, hace al eccon en el objeto, si tiene id o si no tiene id
        if (imagenFile.isEmpty()){ // nsi no esta vaia el archivo, quiere decir que tra imagen para guardar
            try {
                String ruta = firebaseStorageService.uploadImage(
                        imagenFile,
                        "categoria",
                        categoria.getIdCategoria()
                        );
                categoria.setRutaImagen(ruta);
                categoriaRepository.save(categoria); //esto guarda la imagen
            } catch (IOException e) {

            }
        }

    }

    // si  id categoria existe se elimina si no tiene productos asociados
    @Transactional
    public void delete(Integer idCategoria){
//se valida que la categoria exista
        if (!categoriaRepository.existsById(idCategoria)){ //sino existe
            //ppenemos en multiusuarios, otra persona peude estar viendo lo mismo que otra persona y borrar lo mismo por eso se lanza la excpion
            throw new IllegalArgumentException("La categoria no existe"); //trato de borrar algo que ya no existe

        }
        try {
            categoriaRepository.deleteById(idCategoria); //aqui busca el id y borra la categoria
        } catch (DataIntegrityViolationException e){ //esto es por si la categoria a eliminar tiene productos
            throw new IllegalArgumentException("La categoria no se puee elimiar, tiene productos asociados");

        }

    }

}
