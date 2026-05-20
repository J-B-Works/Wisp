package wisp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import wisp.dtos.UserRegisterDTO;
import wisp.models.entities.User;
import wisp.models.services.GraphService;
import wisp.models.services.MapboxService;

@RestController
@RequestMapping("/api/usuarios")
//@CrossOrigin(origins = "http://localhost:5173") // PERMITE QUE O REACT (NA PORTA 5173) FAÇA REQUISIÇÕES PARA ESSE CONTROLLER (NA PORTA 8080)
@CrossOrigin(origins = "*") // PERMITE QUE >QUALQUER< IP FAÇA REQUISIÇÕES PARA ESSE CONTROLLER
public class UserController {

    @Autowired
    private MapboxService mapboxService;

    @Autowired
    private GraphService graphService;

    @PostMapping("/cadastrar")
    public String registerUser(@RequestBody UserRegisterDTO reactData) {
        User createdUser = graphService.registerUserAPI(reactData.nome, reactData.email, reactData.idade, reactData.interesses); // Chama Service (API) para cadastrar o usuário (business logic)
        
        // Busca coordenadas no MAPBOX usando o CEP do DTO
        double[] coords = mapboxService.getCoordinatesFromCep(reactData.cep);
        // Atualizar User com seu CEP, latitude e longitude completos
        createdUser.setCep(reactData.cep);
        createdUser.setLatitude(coords[0]);
        createdUser.setLongitude(coords[1]);

        // TODO teste
        System.out.println("DEBUG GEOLOC -> CEP: " + reactData.cep + " | Lat: " + coords[0] + " | Lon: " + coords[1]);
        
        return createdUser.getId(); // Retorna ID gerado pelo Service (API) (business logic)
    }

    @GetMapping("/{id}")
    public org.springframework.http.ResponseEntity<java.util.Map<String, Object>> getUserProfile(@PathVariable String id) {
        java.util.Map<String, Object> perfil = graphService.getUserProfileAPI(id);
        if (perfil != null) {
            return org.springframework.http.ResponseEntity.ok(perfil);
        }
        return org.springframework.http.ResponseEntity.notFound().build();
    }
}