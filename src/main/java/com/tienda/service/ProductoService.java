package com.tienda.service;

import com.tienda.FirebaseStorageService;
import com.tienda.domain.Producto;
import com.tienda.repository.ProductoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    // Repository used to access producto data from the database.
    private final ProductoRepository productoRepository;

    private final FirebaseStorageService firebaseStorageService;

    public ProductoService(ProductoRepository productoRepository, FirebaseStorageService firebaseStorageService) {
        this.productoRepository = productoRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    // Returns all categories or only active categories, depending on the activo parameter.
    @Transactional(readOnly =true)
    public List<Producto> getProductos(boolean activo){
        if (activo){
            return productoRepository.findByActivoTrue();

        }
        return productoRepository.findAll();
    }

    // recupera 1 reguistro de producto
    @Transactional(readOnly =true)
    public Optional<Producto> getProducto(Integer idProducto){
        return productoRepository.findById(idProducto);
    }

    // si  producto tra un id producto se atualia el regstro, sino se crea
    @Transactional
    public void save(Producto producto, MultipartFile imagenFile){ //Multipartfoile es el tipo de dato de la iamgen
        //se salva la producto
        productoRepository.save(producto); //o actualiza o inserta, hace al eccon en el objeto, si tiene id o si no tiene id
        if (!imagenFile.isEmpty()){ // nsi no esta vaia el archivo, quiere decir que tra imagen para guardar
            try {
                String ruta = firebaseStorageService.uploadImage(
                        imagenFile,
                        "producto",
                        producto.getIdProducto()
                        );
                producto.setRutaImagen(ruta);
                productoRepository.save(producto); //esto guarda la imagen
            } catch (IOException e) {

            }
        }

    }

    // si  id producto existe se elimina si no tiene productos asociados
    @Transactional
    public void delete(Integer idProducto){
//se valida que la producto exista
        if (!productoRepository.existsById(idProducto)){ //sino existe
            //ppenemos en multiusuarios, otra persona peude estar viendo lo mismo que otra persona y borrar lo mismo por eso se lanza la excpion
            throw new IllegalArgumentException("La producto no existe"); //trato de borrar algo que ya no existe

        }
        try {
            productoRepository.deleteById(idProducto); //aqui busca el id y borra la producto
        } catch (DataIntegrityViolationException e){ //esto es por si la producto a eliminar tiene productos
            throw new IllegalStateException("La producto no se puee elimiar, tiene productos asociados");

        }

    }

}
