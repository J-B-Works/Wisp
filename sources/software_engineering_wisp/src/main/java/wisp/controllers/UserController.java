package wisp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import wisp.services.GraphService;
import wisp.models.User;
import wisp.dtos.UserRegisterDTO;

@RestController
@RequestMapping("/api/usuarios")
//@CrossOrigin(origins = "http://localhost:5173") // PERMITE QUE O REACT (NA PORTA 5173) FAÇA REQUISIÇÕES PARA ESSE CONTROLLER (NA PORTA 8080)
@CrossOrigin(origins = "*") // PERMITE QUE >QUALQUER< IP FAÇA REQUISIÇÕES PARA ESSE CONTROLLER
public class UserController {

    @Autowired
    private GraphService graphService;

    @PostMapping("/cadastrar")
    public String registerUser(@RequestBody UserRegisterDTO reactData) {
        User createdUser = graphService.registerUserAPI(reactData.nome, reactData.email, reactData.interesses); // Chama Service (API) para cadastrar o usuário (business logic)
        return createdUser.getId(); // Retorna ID gerado pelo Service (API) (business logic)
    }
}