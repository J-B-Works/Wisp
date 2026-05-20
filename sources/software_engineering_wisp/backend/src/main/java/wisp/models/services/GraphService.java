package wisp.models.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
//import java.util.List;
import wisp.dtos.ActivityDTO;
import wisp.dtos.InteractionDTO;
import wisp.models.entities.Activity;
import wisp.models.entities.Category;
import wisp.models.entities.User;
import wisp.models.entities.graph_structure.GraphNode;
import wisp.models.entities.graph_structure.TGrafo;
import wisp.models.entities.graph_structure.TNo;

@Service
public class GraphService {

    private TGrafo graph;

    @PostConstruct
    public void startServer() { // INICIALIZA SERVIDOR E CARREGA O GRAFO
        System.out.println("\nCarregando dados (200 vértices) de Data para o Grafo...");
        
        this.graph = new TGrafo(0);
        DataLoader loader = new DataLoader();   // Carrega dados no grafo
        loader.loadAll(this.graph, 200);        // Limite de 200 vértices
        
        System.out.println("Grafo carregado com sucesso! (200 vértices)\n");
    }

    public java.util.List<String> getAllCategoriesAPI() { // PEGA TODAS AS CATEGORIAS EXISTENTES PARA LISTÁ-LAS NO QUIZ
        java.util.List<String> categories = new java.util.ArrayList<>();
        for (int i = 0; i < this.graph.getN(); i++) {
            wisp.models.entities.graph_structure.GraphNode node = this.graph.getNodeByIndex(i);
            if (node instanceof wisp.models.entities.Category) {
                categories.add(node.getName());
            }
        }
        return categories;
    }

    public User registerUserAPI(String nome, String email, String idadeStr, List<String> preferences) { // CADASTRA UM NOVO USUÁRIO E SUAS CATEGORIAS PREFERIDAS NO GRAFO
        
        int idade = (idadeStr != null && !idadeStr.isEmpty()) ? Integer.parseInt(idadeStr) : 0; // Converte string p/ int

        User newUser = new User(nome, email, "", idade); // O CEP começa vazio pois o Controller o preencherá
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
            wisp.models.entities.graph_structure.GraphNode node = this.graph.getNodeByIndex(i);
            if (node instanceof User) {
                User u = (User) node;
                if (u.getEmail() != null && u.getEmail().equals(email)) {
                    return u.getId();
                }
            }
        }
        return null;                       // Se o usuário não existir, retorna null
    }

    public java.util.Map<String, Object> getUserProfileAPI(String userId) { // ADQUIRE DADOS DO PERFIL DE UM USUÁRIO EXISTENTE
        User targetUser = null;
        int userIndex = -1;

        // ----------------------- Procura usuário no grafo -----------------------
        for (int i = 0; i < this.graph.getN(); i++) {
            GraphNode node = this.graph.getNodeByIndex(i);
            if (node instanceof User && node.getId().equals(userId)) {
                targetUser = (User) node;
                userIndex = i;
                break;
            }
        }
        if (targetUser == null) return null;

        // ----------------------- Resposta para FrontEnd -----------------------
        java.util.Map<String, Object> profile = new java.util.HashMap<>();
        profile.put("nome", targetUser.getName());
        profile.put("email", targetUser.getEmail());
        profile.put("cep", targetUser.getCep());
        profile.put("idade", String.valueOf(targetUser.getAge()));

        // -------------- Percorre categorias preferidas do usuário --------------
        java.util.List<String> interesses = new java.util.ArrayList<>();
        TNo vizinho = this.graph.getAdj(userIndex);
        while (vizinho != null) {
            GraphNode nodeVizinho = this.graph.getNodeByIndex(vizinho.w);
            if (nodeVizinho instanceof Category) {
                interesses.add(nodeVizinho.getName());
            }
            vizinho = vizinho.prox;
        }
        profile.put("interesses", interesses);

        return profile;
    }


    // Método auxiliar (olha atividades adjacentes do usuário no grafo para ver se tem peso 1 (favorita) ou não (clique))
    private boolean isActivityFavoritedByUser(int userIndex, int actIndex) {
        TNo vizinho = this.graph.getAdj(userIndex);
        while (vizinho != null) {
            if (vizinho.w == actIndex && vizinho.peso != null && vizinho.peso == 1) {
                return true;
            }
            vizinho = vizinho.prox;
        }
        return false;
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
        int userIndex = this.graph.getIndexByNodeId(targetUser.getId());
        List<wisp.models.entities.Activity> rawActivities = recommendation(targetUser, this.graph);

        // Cria DTO para armazenar os dados das atividades e enviá-los para o React depois
        List<wisp.dtos.ActivityDTO> translatedList = new java.util.ArrayList<>();
        for (wisp.models.entities.Activity act : rawActivities) {
            String localName = (act.getEstablishment() != null) ? act.getEstablishment().getName() : "Placeholder"; // Tratamento de segurança caso a atividade não tenha estabelecimento/local ainda
            String actId = act.getId();
            String titulo = act.getName();
            String data = (act.getDate() != null && !act.getDate().isEmpty()) ? act.getDate() : "Placeholder";
            String valor = (act.getValue() != null && !act.getValue().isEmpty()) ? act.getValue() : "Placeholder";
            String imagem = (act.getImage() != null && !act.getImage().isEmpty()) ? act.getImage() : "";
            int actIndex = this.graph.getIndexByNodeId(act.getId());
            boolean favoritadoPeloGrafo = isActivityFavoritedByUser(userIndex, actIndex);

            ActivityDTO dto = new ActivityDTO(actId, titulo, localName, data, valor, imagem, act.getDescription(), act.getExternalLink(), favoritadoPeloGrafo);
            translatedList.add(dto);
        }

        System.out.println("Enviando " + translatedList.size() + " recomendações para o usuário " + targetUser.getName());
        return translatedList;
    }

    public List<wisp.dtos.ActivityDTO> getFavoritesAPI(String userId) { // PEGA ATIVIDADES FAVORITAS DESSE USUÁRIO
        List<wisp.dtos.ActivityDTO> favoritesList = new java.util.ArrayList<>();
        Integer userIndex = this.graph.getIndexByNodeId(userId);

        if (userIndex == null) return favoritesList; // Retorna vazia se o usuário não tiver ID (vistante)

        // ---------- SALTO PARA ATIVIDADES VIZINHAS ----------
        TNo vizinho = this.graph.getAdj(userIndex);

        while (vizinho != null) {
            GraphNode node = this.graph.getNodeByIndex(vizinho.w);
            
            if (node instanceof Activity && vizinho.peso != null && vizinho.peso == 1) { // Verifica se é uma atividade favoritada (tipo Activity e com aresta peso 1)
                Activity act = (Activity) node;
                String localName = (act.getEstablishment() != null) ? act.getEstablishment().getName() : "Placeholder";

                ActivityDTO dto = new ActivityDTO(act.getId(), act.getName(), localName, act.getDate(), act.getValue(), act.getImage(), act.getDescription(), act.getExternalLink(), true);
                favoritesList.add(dto);
            }
            vizinho = vizinho.prox;
        }
        return favoritesList;
    }

    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    //                       SISTEMA DE RECOMENDAÇÃO
    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    
    private double normalizedDistanceCalculation(User u, Activity a) {
    // Se o usuário não tem coordenadas por erro no Mapbox, retornamos 0.5
    if (u.getLatitude() == 0.0 && u.getLongitude() == 0.0) {
        return 0.5;
    }

    // -------------------- FÓRMULA DE HAVERSINE --------------------
    final int R = 6371;                // Raio da Terra em Km

    double lat1 = u.getLatitude();
    double lon1 = u.getLongitude();
    double lat2 = a.getLat();
    double lon2 = a.getLon();

    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);

    double aCalc = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
               Math.sin(dLon / 2) * Math.sin(dLon / 2);
    
    double cCalc = 2 * Math.atan2(Math.sqrt(aCalc), Math.sqrt(1 - aCalc));
    double distanceInKm = R * cCalc;

    // -------------------- NORMALIZAÇÃO (0.0 a 1.0) --------------------
    double maxDistance = 50.0;         // 50km é o limite máximo

    if (distanceInKm >= maxDistance) { // Se estiver muito longe,
        return 1.0;                    // Implica penalidade máxima no score
    }

    return distanceInKm / maxDistance; // Proporcional (ex: 10km vira 0.2)
}

    private List<Activity> recommendation(User user, TGrafo graph) { // CONTENT-BASED SCORING

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

    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    //        SISTEMA DE RECOMENDAÇÃO - TRECHO DO COLLABORATIVE FILTERING
    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public void registerInteractionAPI(String userId, String activityId, boolean isFavorite) {
        User targetUser = null;
        Activity targetActivity = null;

        // Procura os nós correspondentes no grafo usando NodeID
        for (int i = 0; i < this.graph.getN(); i++) {
            GraphNode node = this.graph.getNodeByIndex(i);
            if (node instanceof User && node.getId().equals(userId)) {
                targetUser = (User) node;
            } else if (node instanceof Activity && node.getId().equals(activityId)) {
                targetActivity = (Activity) node;
            }
        }

        if (targetUser != null && targetActivity != null) {
            processUserInteraction(targetUser, targetActivity, isFavorite);
            System.out.println("Interação registrada! Usuário: " + targetUser.getName() + " | Atividade: " + targetActivity.getName() + " | Favorito: " + isFavorite);
        } else {
            System.out.println("Erro ao registrar interação: Usuário ou Atividade não encontrados.");
        }
    }

    private void processUserInteraction(User user, Activity act, boolean isFavorite) {
        Integer userIndex = this.graph.getIndexByNodeId(user.getId());
        Integer actIndex = this.graph.getIndexByNodeId(act.getId());

        if (userIndex == null || actIndex == null) return;

        // 1. Ação Direta: Registra clique na atividade (para a fórmula da popularidade)
        act.registerClick();

        // Define os pesos
        int actWeight = isFavorite ? 1 : 0;          // 0 = Clique, 1 = Favorito
        int selfCatBonus = isFavorite ? 3 : 1;       // Bônus forte no próprio gosto se favoritar

        // 2. Feedback Direto: Cria/atualiza a aresta Usuário <-> Atividade
        atualizaPesoAresta(userIndex, actIndex, actWeight, false); // false = não soma, crava o valor

        // ---------------- SALTO 1: Atividade -> Suas Categorias ----------------
        TNo vizinhoAct = this.graph.getAdj(actIndex);

        while (vizinhoAct != null) {
            GraphNode nodeCat = this.graph.getNodeByIndex(vizinhoAct.w);

            if (nodeCat instanceof Category) {
                // Reforço de Perfil: Aumenta o peso das categorias dessa atividade para o usuário
                atualizaPesoAresta(userIndex, vizinhoAct.w, selfCatBonus, true); // true = soma ao existente
            }
            vizinhoAct = vizinhoAct.prox;
        }

        // ---------------- SALTO 2: Atividade -> Outros Usuários (Os "Pares") ----------------
        TNo vizinhoAct2 = this.graph.getAdj(actIndex);

        while (vizinhoAct2 != null) {
            GraphNode nodeOutro = this.graph.getNodeByIndex(vizinhoAct2.w);

            // Se for um usuário e NÃO for o nosso próprio usuário logado...
            if (nodeOutro instanceof User && !nodeOutro.getId().equals(user.getId())) {
                
                // Verifica se o "par" favoritou (peso 1) ou só clicou (peso 0)
                int interacaoDoPar = (vizinhoAct2.peso != null) ? vizinhoAct2.peso : 0;
                int collabBonus = (interacaoDoPar == 1) ? 2 : 1; // Se ele favoritou, a influência dele vale o dobro

                // ---------------- SALTO 3: Outro Usuário -> Categorias Dele ----------------
                TNo vizinhoOutro = this.graph.getAdj(vizinhoAct2.w);

                while (vizinhoOutro != null) {
                    GraphNode nodeCatOutro = this.graph.getNodeByIndex(vizinhoOutro.w);

                    if (nodeCatOutro instanceof Category) {
                        // O FILTRO COLABORATIVO: Injeta gosto do "par" no perfil do nosso usuário
                        atualizaPesoAresta(userIndex, vizinhoOutro.w, collabBonus, true);
                    }
                    vizinhoOutro = vizinhoOutro.prox;
                }
            }
            vizinhoAct2 = vizinhoAct2.prox;
        }
    }

    private void atualizaPesoAresta(int indexA, int indexB, int peso, boolean somar) {
        boolean arestaExiste = false;

        // Procura e atualiza na lista de adjacência de A (A -> B)
        TNo vizinhoA = this.graph.getAdj(indexA);
        while (vizinhoA != null) {
            if (vizinhoA.w == indexB) {
                vizinhoA.peso = somar ? ((vizinhoA.peso != null ? vizinhoA.peso : 1) + peso) : peso;
                arestaExiste = true;
                break;
            }
            vizinhoA = vizinhoA.prox;
        }

        // Procura e atualiza na lista de adjacência de B (B -> A)
        TNo vizinhoB = this.graph.getAdj(indexB);
        while (vizinhoB != null) {
            if (vizinhoB.w == indexA) {
                vizinhoB.peso = somar ? ((vizinhoB.peso != null ? vizinhoB.peso : 1) + peso) : peso;
                break;
            }
            vizinhoB = vizinhoB.prox;
        }

        // Se navegou e a aresta não existia, cria ela
        if (!arestaExiste) {
            this.graph.insereA(this.graph.getNodeByIndex(indexA), this.graph.getNodeByIndex(indexB), peso);
        }
    }

    // -------------------------------------------------------------------------
    //                            MODO VISITANTE
    // -------------------------------------------------------------------------
    public List<wisp.dtos.ActivityDTO> getPopularActivitiesAPI() {

        // -------------- Cria uma lista de TODAS as atividades existentes no grafo --------------
        List<wisp.models.entities.Activity> allActivities = new java.util.ArrayList<>();
        for (int i = 0; i < this.graph.getN(); i++) {
            GraphNode node = this.graph.getNodeByIndex(i);
            if (node instanceof Activity) {
                allActivities.add((Activity) node);
            }
        }

        // -------------- Ordena por mais clicada à menos clicada usando Bubble Sort --------------
        // PS: Num sistema real, haveriam otimizações feitas na base de dados.
        // Por ex, talvez as atividades fossem classificadas por 50+ cliques ou 100+ cliques, então 1000+ cliques, etc,
        // facilitando a ordenação em tempo real no banco de dados sempre que a atividade batesse uma "meta" de cliques, ou talvez
        // qualquer outro tipo de otimização melhor. Porém, para nosso caso atual, é mais didático varrer o grafo inteiro
        // e ordená-lo de forma bem simples, mesmo que não otimizada, para ter absoluta certeza de que qualquer diferença
        // pequena nos cliques será notada.
        for (int i = 0; i < allActivities.size() - 1; i++) {
            for (int j = 0; j < allActivities.size() - i - 1; j++) {
                if (allActivities.get(j).getClickCount() < allActivities.get(j + 1).getClickCount()) {
                    Activity temp = allActivities.get(j);
                    allActivities.set(j, allActivities.get(j + 1));
                    allActivities.set(j + 1, temp);
                }
            }
        }

        // ---------------------------------- Converte para DTO -----------------------------------
        List<wisp.dtos.ActivityDTO> translatedList = new java.util.ArrayList<>();
        for (wisp.models.entities.Activity act : allActivities) {
            String localName = (act.getEstablishment() != null) ? act.getEstablishment().getName() : "Placeholder";
            wisp.dtos.ActivityDTO dto = new wisp.dtos.ActivityDTO(act.getId(), act.getName(), localName, act.getDate(), act.getValue(), act.getImage(), act.getDescription(), act.getExternalLink(), false);
            translatedList.add(dto);
        }

        return translatedList;
    }

    // -------------------------------------------------------------------------
    //                             MODO FILTRO E BUSCA
    // -------------------------------------------------------------------------
    public List<wisp.dtos.ActivityDTO> searchActivitiesAPI(String nameFilter, String categoryFilter, String userId) {
        
        // -------------------- Variáveis auxiliares de filtro --------------------
        List<wisp.models.entities.Activity> filteredActivities = new java.util.ArrayList<>();
        // Pega o index do usuário para que ele ainda possa favoritar atividades SE ele não for um visitante
        Integer userIndex = null;
        if (userId != null && !userId.isEmpty() && !userId.equals("null")) {
            userIndex = this.graph.getIndexByNodeId(userId);
        }

        // -------------------------------- FILTRO --------------------------------
        for (int i = 0; i < this.graph.getN(); i++) {
            GraphNode node = this.graph.getNodeByIndex(i);
            
            if (node instanceof Activity) {
                
                // -------------------- Filtro de termo/nome --------------------
                Activity act = (Activity) node;
                boolean nameMatch = true;
                if (nameFilter != null && !nameFilter.trim().isEmpty()) {
                    nameMatch = act.getName().toLowerCase().contains(nameFilter.toLowerCase().trim());
                }
                
                // --------------------- Filtro de categoria --------------------
                boolean categoryMatch = true;
                if (categoryFilter != null && !categoryFilter.trim().isEmpty()) {
                    
                    categoryMatch = false;
                    TNo vizinho = this.graph.getAdj(i);
                    while (vizinho != null) {
                        GraphNode nodeVizinho = this.graph.getNodeByIndex(vizinho.w);
                        if (nodeVizinho instanceof Category && nodeVizinho.getName().equalsIgnoreCase(categoryFilter.trim())) {
                            categoryMatch = true;
                            break;
                        }
                        vizinho = vizinho.prox;
                    }
                }
                
                // Se passou nos filtros exigidos, entra na lista
                if (nameMatch && categoryMatch) {
                    filteredActivities.add(act);
                }
            }
        }

        // ---------------------------------- Converte para DTO -----------------------------------
        List<wisp.dtos.ActivityDTO> translatedList = new java.util.ArrayList<>();
        for (wisp.models.entities.Activity act : filteredActivities) {
            String localName = (act.getEstablishment() != null) ? act.getEstablishment().getName() : "Placeholder";
            int actIndex = this.graph.getIndexByNodeId(act.getId());
            
            boolean favoritadoPeloGrafo = false;
            if (userIndex != null) {
                favoritadoPeloGrafo = isActivityFavoritedByUser(userIndex, actIndex);
            }

            wisp.dtos.ActivityDTO dto = new wisp.dtos.ActivityDTO(
                act.getId(), act.getName(), localName, act.getDate(), 
                act.getValue(), act.getImage(), act.getDescription(), 
                act.getExternalLink(), favoritadoPeloGrafo
            );
            translatedList.add(dto);
        }

        return translatedList;
    }
}