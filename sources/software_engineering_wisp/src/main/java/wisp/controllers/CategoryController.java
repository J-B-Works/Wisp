package wisp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import wisp.services.GraphService;

@RestController
@RequestMapping("/api/categorias")
//@CrossOrigin(origins = "http://localhost:5173") // PERMITE QUE O REACT (NA PORTA 5173) FAÇA REQUISIÇÕES PARA ESSE CONTROLLER (NA PORTA 8080)
@CrossOrigin(origins = "*") // PERMITE QUE >QUALQUER< IP FAÇA REQUISIÇÕES PARA ESSE CONTROLLER
public class CategoryController {

    @Autowired
    private GraphService graphService;

    @GetMapping
    public List<String> getCategories() {
        return graphService.getAllCategoriesAPI(); // Retorna lista de categorias pelo Service (API) (business logic)
    }
}