package wisp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import wisp.dtos.ActivityDTO;
import wisp.dtos.InteractionDTO;
import wisp.models.services.GraphService;

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

    @PostMapping("/interaction") // TODO ajeitar
    public ResponseEntity<String> registerInteraction(@RequestBody InteractionDTO request) {
        try {
            graphService.registerInteractionAPI(
                request.getUserId(), 
                request.getActivityId(), 
                request.isFavorite()
            );
            return ResponseEntity.ok("Interação registrada e pesos do grafo atualizados com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao processar a interação: " + e.getMessage());
        }
    }

    @GetMapping("/favoritos/{userId}")
    public ResponseEntity<List<ActivityDTO>> getUserFavorites(@PathVariable String userId) {
        List<ActivityDTO> favoritos = graphService.getFavoritesAPI(userId);
        return ResponseEntity.ok(favoritos);
    }

    @GetMapping("/visitante")
    public ResponseEntity<List<ActivityDTO>> getPopularActivities() {
        List<ActivityDTO> populares = graphService.getPopularActivitiesAPI();
        return ResponseEntity.ok(populares);
    }

    @GetMapping("/busca")
    public ResponseEntity<List<wisp.dtos.ActivityDTO>> searchActivities(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String userId) {
        
        try {
            List<wisp.dtos.ActivityDTO> resultados = graphService.searchActivitiesAPI(nome, categoria, userId);
            return ResponseEntity.ok(resultados);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}