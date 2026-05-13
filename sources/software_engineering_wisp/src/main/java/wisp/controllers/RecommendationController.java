package wisp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import wisp.services.GraphService;
import wisp.dtos.ActivityDTO;

@RestController
@RequestMapping("/api/recomendacoes")
//@CrossOrigin(origins = "http://localhost:5173") // PERMITE QUE O REACT (NA PORTA 5173) FAÇA REQUISIÇÕES PARA ESSE CONTROLLER (NA PORTA 8080)
@CrossOrigin(origins = "*") // PERMITE QUE >QUALQUER< IP FAÇA REQUISIÇÕES PARA ESSE CONTROLLER
public class RecommendationController {

    @Autowired
    private GraphService graphService;

    @GetMapping("/{userId}")
    public List<ActivityDTO> getRecommendations(@PathVariable String userId) {
        System.out.println("React solicitou feed para o usuário ID: " + userId); // TODO print de teste temporária
        List<ActivityDTO> feed = graphService.getRecommendationsAPI(userId); // Chama Service (API) para calcular as recomendações do usuário (business logic)
        return feed; // Retorna lista gerada pelo Service (API) (business logic)
    }
}