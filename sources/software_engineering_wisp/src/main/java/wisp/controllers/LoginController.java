package wisp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import wisp.services.GraphService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // PERMITE QUE O REACT (NA PORTA 5173) FAÇA REQUISIÇÕES PARA ESSE CONTROLLER (NA PORTA 8080)
public class LoginController {

    @Autowired
    private GraphService graphService;

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> body) {
        String userId = graphService.loginAPI(body.get("email")); // Chama Service (API) para verificar se usuário com esse email existe (business logic)
        return (userId != null) ? userId : "ERRO"; // Se existe, retorna o ID. Se não, informa ERRO
    }
}