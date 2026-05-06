package wisp.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
//import java.util.List;

import wisp.models.TGrafo;
import wisp.models.User;
import wisp.dtos.ActivityDTO;
import wisp.models.Activity;
import wisp.models.Category;
import wisp.models.GraphNode;
import wisp.models.TNo;

@Service
public class GraphService {

    private TGrafo graph;

    @PostConstruct
    public void startServer() { // INICIALIZA SERVIDOR E CARREGA O GRAFO
        System.out.println("\nCarregando dados (200 vértices) de Data para o Grafo...");
        
        this.graph = new TGrafo(0);
        DataLoader loader = new DataLoader();   // Carrega dados no grafo
        loader.loadAll(this.graph, 200); // Passa o limite de 200 vértices
        
        System.out.println("Grafo carregado com sucesso! (200 vértices)\n");
    }

    public java.util.List<String> getAllCategoriesAPI() { // PEGA TODAS AS CATEGORIAS EXISTENTES PARA LISTÁ-LAS NO QUIZ
        java.util.List<String> categories = new java.util.ArrayList<>();
        for (int i = 0; i < this.graph.getN(); i++) {
            wisp.models.GraphNode node = this.graph.getNodeByIndex(i);
            if (node instanceof wisp.models.Category) {
                categories.add(node.getName());
            }
        }
        return categories;
    }

    public User registerUserAPI(String nome, String email, List<String> preferences) { // CADASTRA UM NOVO USUÁRIO E SUAS CATEGORIAS PREFERIDAS NO GRAFO
        User newUser = new User(nome, email);
        this.graph.insereV(newUser);

        if (preferences != null && !preferences.isEmpty()) { // Se as categorias do usuário estiverem especificadas, adicionamos elas ao grafo (arestas U <-> C)
            for (String categoryName : preferences) {        // Para cada nome de categoria especificado nas preferencias, ...
                for (int i = 0; i < this.graph.getN(); i++) {
                    GraphNode node = this.graph.getNodeByIndex(i);
                    if (node instanceof Category && node.getName().equalsIgnoreCase(categoryName)) { // ...verifica se ele existe
                        this.graph.insereA(newUser, node, 1); // Se sim, liga (U <-> C) com peso 1
                        break;                               // Achou essa categoria, vai procurar próxima
                    }
                }
            }
        }
        
        System.out.println("Novo usuário cadastrado: " + nome + " (ID: " + newUser.getId() + ")");
        return newUser;
    }

    public String loginAPI(String email) { // REALIZA LOGIN DE UM USUÁRIO JÁ CADASTRADO/EXISTENTE
        for (int i = 0; i < this.graph.getN(); i++) {
            wisp.models.GraphNode node = this.graph.getNodeByIndex(i);
            if (node instanceof User) {
                User u = (User) node;
                if (u.getEmail() != null && u.getEmail().equals(email)) {
                    return u.getId();
                }
            }
        }
        return null;                       // Se o usuário não existir, retorna null
    }

    public List<wisp.dtos.ActivityDTO> getRecommendationsAPI(String userId) { // PEGA RECOMENDAÇÕES PARA ESSE USUÁRIO
        User targetUser = null;

        // Procura o NodeID desse usuário pelo seu Index
        for (int i = 0; i < this.graph.getN(); i++) {
            GraphNode node = this.graph.getNodeByIndex(i);
            if (node instanceof User && node.getId().equals(userId)) {
                targetUser = (User) node;
                break;
            }
        }
        if (targetUser == null) { // Se não achar o usuário, retorna lista vazia
            return new java.util.ArrayList<>();
        }

        // =-=-=-=-=-=-= CHAMDADA DO SISTEMA DE RECOMENDAÇÃO =-=-=-=-=-=-=
        List<wisp.models.Activity> rawActivities = initialRecommendation(targetUser, this.graph);

        // Cria DTO para armazenar os dados das atividades e enviá-los para o React depois
        List<wisp.dtos.ActivityDTO> translatedList = new java.util.ArrayList<>();
        for (wisp.models.Activity act : rawActivities) {
            String localName = (act.getEstablishment() != null) ? act.getEstablishment().getName() : "Placeholder"; // Tratamento de segurança caso a atividade não tenha estabelecimento/local ainda
            String actId = act.getId();
            String titulo = act.getName();
            String data = (act.getDate() != null && !act.getDate().isEmpty()) ? act.getDate() : "Placeholder";
            String valor = (act.getValue() != null && !act.getValue().isEmpty()) ? act.getValue() : "Placeholder";
            String imagem = (act.getImage() != null && !act.getImage().isEmpty()) ? act.getImage() : "";

            ActivityDTO dto = new ActivityDTO(actId, titulo, localName, data, valor, imagem);
            translatedList.add(dto);
        }

        System.out.println("Enviando " + translatedList.size() + " recomendações para o usuário " + targetUser.getName());
        return translatedList;
    }

    // O método getter para o Controller acessar o Grafo quando precisar
    //public TGrafo getGraph() {
    //    return this.graph;
    //}

    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    //                       SISTEMA DE RECOMENDAÇÃO
    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    
    private double normalizedDistanceCalculation(User u, Activity a) {     // TODO Placeholder para o método auxiliar de cálculo de Distância de Haversine
        return 0.0;
    }

    private List<Activity> initialRecommendation(User user, TGrafo graph) { // CONTENT-BASED SCORING

        // --------------------------- Inicialização e variáveis auxiliares ---------------------------

        Integer userIndex = graph.getIndexByNodeId(user.getId());   // NodeID -> Index do usuário no grafo
        
        List<Activity> activities = new ArrayList<>();              // Lista de atividades possíveis de serem recomedadas
        List<Double> activitiesScore = new ArrayList<>();           // Score dessas atividades (listas alinhadas por index)

        // -------------------------- PRIMEIRO SALTO BFS (User -> Categoria) --------------------------
        TNo neighboringCategory = graph.getAdj(userIndex);          // SALTO 1:Adquire a lista de adjacências do usuário, ou seja, seus vizinhos, ou seja, as suas categorias preferidas
        
        while (neighboringCategory != null) {                       // Percorre lista de adjacências (todos os vizinhos do usuário)
        int catIndex = neighboringCategory.w;                     // Index de um dos vizinhos / categorias preferidas do usuário
        GraphNode nodeCat = graph.getNodeByIndex(catIndex);       // Converte Index -> NodeID para ver suas propriedades
        
        if (nodeCat instanceof Category) {                        // Garante de que é uma categoria mesmo (U -> C), e não, por exemplo, uma atividade (U -> A)

            double weightUC = (neighboringCategory.peso != null) ? neighboringCategory.peso : 1.0; // Peso da aresta U <-> C (Por segurança, se for null, é settado como 1.0)
            if (weightUC <= 0) weightUC = 1.0;                      // Segurança para evitar divisão por zero na fórmula do Scoring
            
            // -------------------------- SEGUNDO SALTO BFS (Categoria -> Atividade) --------------------------
            TNo neighboringActivity = graph.getAdj(catIndex);       // SALTO 2: Adquire a lista de adjacências da categoria, ou seja, seus vizinhos, ou seja, as suas atividades        
            
            while (neighboringActivity != null) {                   // Percorre lista de adjacências (todos os vizinhos da categoria)
            GraphNode nodeAct = graph.getNodeByIndex(neighboringActivity.w); // Converte Index -> NodeID de um dos vizinhos / atividades da categoria
            
            if (nodeAct instanceof Activity) {                    // Garante de que é uma atividade mesmo (C -> A), e não, por exemplo, um usuário (C -> U)
                Activity act = (Activity) nodeAct;                  // Sabendo que é uma atividade, especificamos de que o tipo desse GraphNode é Activity (mais específico)
                
                // -------------------------- CÁLCULO DO SCORING P/ ESSA ATIVIDADE --------------------------

                // FÓRMULA COMPLETA É: Score = ( Dnorm * 0.5 ) + ( 1 / Peso(U->C) * 0.3 ) + ( 1 / Cliques(A)+1 * 0.2 )
                //  -> Quanto menor o Score, melhor a recomendação.
                //    -> ( Dnorm ) é a Distância normalizada - Quanto maior ela for, mais ela "atrapalha" a recomendar essa atividade
                //    -> ( 1 / Peso(U->C) ) é o inverso do peso do interesse do usuário por aquela categoria - Quanto maior o peso (gosto), menor o número, melhor a recomendação
                //    -> ( 1 / Cliques(A)+1 ) é o inverso da popularidade da atividade - Quanto mais clicada, menor o número, melhor a recomendação. O "+1" é para evitar divisão por zero no caso de uma atividade ter 0 cliques.
                //        -> As multiplicações feitas logo após representam o quanto esses fatores afetam o cálculo final
                //            -> ( Dnorm * 0.5 )
                //            -> ( 1 / Peso(U->C) * 0.3 )
                //            -> ( 1 / Cliques(A)+1 * 0.2 )

                double dNorm = normalizedDistanceCalculation(user, act); // Chama o método auxiliar para calcular a distância normalizada entre o usuário e a atividade utilizando a fórmula de Haversine
                double pref = 1.0 / weightUC;
                double pop = 1.0 / (act.getClickCount() + 1.0);

                double currentScore = (dNorm * 0.5) + (pref * 0.3) + (pop * 0.2); // SCORE CALCULADO FINAL
                
                // ----------------------------------- Adiciona atividade nova -----------------------------------
                int index = activities.indexOf(act);             // Verifica se a atividade já está na lista de atividades possíveis a serem recomendadas (indexOf retorna -1 quando não estiver)
                if (index == -1) {                               // Se a atividade não existe ainda na lista (primeira vez que é encontrada),
                activities.add(act);                                  // simplesmente adiciona a atividade na lista de possíveis recomendações
                activitiesScore.add(currentScore);                    // e o score dela na lista de scores
                } else {                                                // Se ela JÁ EXISTE na lista (veio de outra categoria analisada no passado),
                // --------- AJUSTE FINAL NO SCORE NO CASO DE ATIVIDADE COM MÚLTIPLAS CATEGORIAS PREFERIDAS ---------
                double bestScore = Math.min(currentScore, activitiesScore.get(index)); // Comparar o score atual com o score vindo da outra categoria e pegar o melhor (menor) dos dois
                double bonus = 0.15;                                  // Define um bônus extra porque a atividade está ligada à mais de uma categoria preferida do usuário simultaneamente
                activitiesScore.set(index, bestScore - bonus); // Atualizamos o score para ser o melhor score encontrado dentre os dois E também possuir o bônus de match extra
                }
            }
            neighboringActivity = neighboringActivity.prox;           // Continua percorrendo a lista de adjacências da categoria e pegando a próxima ativdade
            }
        }
        neighboringCategory = neighboringCategory.prox;               // Continua percorrendo a lista de adjacências do usuário e pegando a próxima categoria
        }

        // --------------------- ORDENA ATIVIDADES DE RECOMENDAÇÃO COM BUBBLE SORT ---------------------

        for (int i = 0; i < activities.size() - 1; i++) {               // Esse loop controla quantas passadas completas faremos na lista.
        for (int j = 0; j < activities.size() - i - 1; j++) {         // Esse loop compara os vizinhos. O "- i" serve para ignorar os últimos elementos que já foram ordenados anteriormente
            if (activitiesScore.get(j) > activitiesScore.get(j + 1)) {  // Se o score da atividade atual for MAIOR (pior) que a nota do próximo item, eles estão na ordem errada.
            Activity tempAct = activities.get(j);                     // Realiza o Swap das ATIVVIDADES
            activities.set(j, activities.get(j + 1));
            activities.set(j + 1, tempAct);
            
            Double tempScore = activitiesScore.get(j);                // Realiza o Swap dos SCORES DESSAS ATIVIDADES (mantendo index sincronizado)
            activitiesScore.set(j, activitiesScore.get(j + 1));
            activitiesScore.set(j + 1, tempScore);
            }
        }
        }

        // --------------- RETORNA LISTA FINAL DA ORDEM DE RECOMENDAÇÃO DAS ATIVIDADES ---------------
        return activities;
    }

}