package com.tienda.controller;

import org.springframework.ui.Model;
import com.tienda.service.CategoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // Defines this class as a Spring MVC controller.
@RequestMapping("/categoria") // Maps all requests that start with /categoria to this controller.
public class CategoriaController {
    // Service used to access category-related business logic.
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listado") //ete es el metodo que responde a la llamada
    public String listado(Model model){
        // Retrieves the list of categories from the database.
        var categorias= categoriaService.getCategorias(false);

        // Adds the category list to the model so it can be used in the view.
        model.addAttribute("categorias", categorias);

        // Adds the total number of categories to the model.
        model.addAttribute("totalCategorias", categorias.size());

        // Returns the categoria/listado.html view.
        return "/categoria/listado";
    }
}
