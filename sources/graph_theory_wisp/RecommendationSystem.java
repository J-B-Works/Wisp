/*

=======================================================
  WISP– Sistema de recomendação de educação e cultura
=======================================================

-- Grupo --
Teoria dos Grafos - Turma: 6G
- Bruna Gonçalves Corte David (RA: 10425696)
- Júlia Andrade (RA: 1042513)

-- Síntese do Conteúdo --

-> Sistema de Recomendação
  -> Possui um menu com quatro opções, cada uma representando um tipo diferente de recomendação que nossa plataforma Wisp faz

    -> 1. Cadastrar um Novo Usuário               -> (Content-based filtering)
      -> Cria um novo usuário realizando um quiz para saber suas categorias preferidas, assim, superando o problema
         do Cold-Start que muitos sistemas de recomendação têm, fazendo com que a primeira recomendação que esse usuário
         vá receber seja do tipo Content-based filtering (se baseando no conteúdo explícito que o usuário escolheu) 

    -> 2. Realizar Ação com um Usuário Cadastrado -> (Collaborative filtering)
      -> Simula a interação do usuário com a plataforma Wisp: sempre que ele clica ou favorita uma atividade,
         mostramos como seria se ele "retornasse ao feed" logo depois ou um tempo depois, ou seja, como o sistema
         de recomendações muda suas recomendações com base no quanto ele aprende sobre as preferências do usuário.

         -> O nosso Collaborative Filtering funciona em saltos no grafo feitos a partir do vértice User, para
            encontrar fatores que vão mudar o peso nas arestas Usuário <-> Categorias (preferidas) Adjacentes
            -> Saltos para influenciar recomendações com base nas Categorias da atividade que foi clicada/favoritada
              -> Usuário -> Atividade -> (todas) Categorias Adjacentes
            -> Saltos para influenciar recomendações com base nas Categorias (preferidas) DOS OUTROS usuários que também clicaram/favoritaram a mesma atividade
              -> Usuário -> Atividade -> (todos) Usuários Adjacentes -> (todas) Categorias Adjacentes
        
         -> Depois de rodar o Collaborative Filtering que APENAS ATUALIZOU AS ARESTAS DO GRAFO, só então o sistema
            recalcula as atividades a serem recomendadas através de outros saltos, utilizando uma fórmula de
            scoring que leva em consideração o peso das preferências do usuário (que foram atualizadas) e a distância
            dele em relação à cada atividade
            -> Saltos para encontrar nova recomendação de atividades
              -> Usuário -> (todas) Categorias (preferidas) Adjacentes -> (todas) Atividades Adjacentes

    -> 3. Realizar Ação como Visitante            -> (Popularity-based)
      -> As atividades todas possuem um contador próprio de quantos cliques ela teve, assim, podemos permitir que
         qualquer pessoa possa abrir o feed da plataforma sem necessariamente se cadastrar e, apesar dela não
         receber um feed personalizado, ela receberá um feed calculado com base na atividade mais popular para a
         menos popular.

    -> 4. Modo Filtro                             -> (Filtro manual normal)
      -> O modo filtro serve para pessoas pesquisarem uma atividade por nome ou por categoria na plataforma, dando
         uma liberdade de navegação mínima. Ao mesmo tempo, ele serve para mostrar a diferença entre um sistema de
         recomendações para um filtro comum e prova na prática que a diferença entre os dois é enorme.

-- Histórico de Alterações --

27/03/2026 - Júlia - Main criada para realizar testes de carregamento de dados no grafo
27/03/2026 - Júlia - Adiciona a opção de interromper o carregamento quando atingir n quantia de nós para fins de teste
01/04/2026 - Bruna - Main refatorada para implementar menu de opções
                -> Menu dev para carregar dados, gerar grafos e testar adição de usuários
                -> Menu normal para lidar apenas com grafo.txt final
01/04/2026 - Bruna - Main refatorada para não conter mais Menu dev, apenas o Menu final
01/04/2026 - Júlia - Adição de cabeçalho, síntese, refatoração de comentários e melhoria das prints

-- Histórico de Alterações --

09/05/2026 - Bruna - Estruturação do menu de interações e desenvolvimento do Modo Visitante (guestAction).
09/05/2026 - Bruna - Desenvolvimento do Quiz para a geração inicial das arestas de afinidade (Usuário <-> Categoria) / (Content-Based Filtering).
16/05/2026 - Bruna - Implementação das recomendações Collaborative Filtering e scoring (sem distância).
17/05/2026 - Júlia - Inclusão de latitude e longitude nos dados (Activity e User) para cálculo de scoring com distância.
17/05/2026 - Júlia - Implementação do cálculo matemático de distância geográfica (normalizedDistanceCalculation) via Haversine.
17/05/2026 - Bruna - Implementação da lógica matemática de recomendação (recommendation) com cálculo do score final.
17/05/2026 - Júlia - Revisão da estrutura do código e de comentários.

*/

package graph_theory_wisp;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import graph_theory_wisp.graph_node_types.Activity;
import graph_theory_wisp.graph_node_types.Category;
import graph_theory_wisp.graph_node_types.GraphNode;
import graph_theory_wisp.graph_node_types.User;

public class RecommendationSystem {

  // =========================================================================
  //                               GLOBAIS
  // =========================================================================

  private static Scanner sc = new Scanner(System.in);     // Scanner para leitura de input no terminal
  
  private List<User> registeredUsers = new ArrayList<>(); // Lista auxiliar para armazenar usuários cadastrados

  // =========================================================================
  //                     PÚBLICOS (SOMENTE USO EXTERNO)
  // =========================================================================

  public void registerNewUserManually(User u) {              // Função auxiliar para os vértices tipo User inseridos na Main manualmente sejam cadastrados aqui
    if (!registeredUsers.contains(u)) {
      registeredUsers.add(u);
    }
  }

  // -------------------------------------------------------------------------
  //                   FUNÇÕES AUXILIARES DO MENU PRINCIPAL
  // -------------------------------------------------------------------------

  // Método auxiliar para evitar crash do Scanner com letras no lugar de números
  private static int lerInteiro() {
    try {
      return Integer.parseInt(sc.nextLine());
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  // =========================================================================
  //                            MENU PRINCIPAL
  // =========================================================================
  public void menu(TGrafo graph) {

    int op = -1;
    while (true) {
      System.out.println("\n=======================================================================");
      System.out.println("                     SISTEMA DE RECOMENDAÇÃO HÍBRIDO                   ");
      System.out.println("=======================================================================");
      System.out.println("1. Cadastrar um Novo Usuário               -> (Content-based filtering)");
      System.out.println("2. Realizar Ação com um Usuário Cadastrado -> (Collaborative filtering)");
      System.out.println("3. Realizar Ação como Visitante            -> (Popularity-based)");
      System.out.println("4. Modo Filtro                             -> (Filtro manual normal)");
      System.out.println("5. Voltar ao Menu Principal");
      System.out.println("\nO cálculo de recomendação muda dependendo do que fizer.\n");
      System.out.print("Escolha uma opção: ");

      op = lerInteiro();

      switch (op) {
        case 1:
          registerNewUser(graph);
          break;
        case 2:
          userAction(graph);
          break;
        case 3:
          guestAction(graph);
          break;
        case 4:
          filterAction(graph);
          break;
        case 5:
          return;
        default:
          System.out.println("Opção inválida.");
      }
    }
  }

  // =========================================================================
  //                               CADASTRO
  // =========================================================================
  private void registerNewUser(TGrafo graph) {
    System.out.print("Digite o nome do novo usuário: ");
    String nome = sc.nextLine();

    System.out.println("\n--- ONDE VOCÊ MORA? ---");
    System.out.println("1. Cachoeirinha");
    System.out.println("2. Santana");
    System.out.println("3. Lapa");
    System.out.println("4. Perdizes");
    System.out.println("5. Bela Vista");
    System.out.println("6. Sé");

    int regiao;
    while (true){     // Validação rápida da entrada do usuário
      System.out.print("Sua escolha: ");
      regiao = lerInteiro();
      if (regiao >= 1 || regiao <= 6) {
        break;
      }
      System.out.println("Opção inválida.");
    }
    
    double lat = 0.0;
    double lon = 0.0;
    switch (regiao) { // Define latitude e longitude do usuário com base na região em que ele mora
      case 1: // Cachoeirinha
        lat = -23.4749;
        lon = -46.6503;
        break;
      case 2: // Santana
        lat = -23.5050;
        lon = -46.6350;
        break;
      case 3: // Lapa
        lat = -23.5250;
        lon = -46.7050;
        break;
      case 4: // Perdizes
        lat = -23.5360;
        lon = -46.6740;
        break;
      case 5: // Bela Vista
        lat = -23.5610;
        lon = -46.6450;
        break;
      case 6: // Sé
        lat = -23.5500;
        lon = -46.6330;
        break;
    }

    User novoUser = new User(nome, lat, lon); // Cria novo usuário (vértice tipo User) com as coordenadas
    graph.insereV(novoUser);        // Adiciona ele no grafo
    registeredUsers.add(novoUser);  // Adiciona ele na lista de usuários cadastrados auxiliar
    
    System.out.println("Usuário '" + nome + "' cadastrado com sucesso!");
  }
  
  // -------------------------------------------------------------------------
  //                   QUIZ - MÉTODO AUXILIAR DE AÇÃO COM USUÁRIO
  // -------------------------------------------------------------------------
  // PS: Esse método não está otimizado de propósito pois adicionaria complexidade desnecessária
  // no carregamento de dados (está desotimizado porque percorremos o grafo inteiro para definir as categorias).
  // Em um sistema real, o banco de dados iria armazenar/acompanhar as categorias existentes para
  // otimizar a criação de um quiz com elas, e provavelmente se concentrando nas mais populares também.
  private void runQuiz(User user, TGrafo graph) {
    System.out.println("\n-----------------------------------------------------------------------");
    System.out.println("               'QUIZ' DAS SUAS CATEGORIAS DE PREFERÊNCIA               ");
    System.out.println("-----------------------------------------------------------------------");
    
    // -------------- Cria uma lista de TODAS as atividades existentes no grafo --------------
    List<Category> allCategories = new ArrayList<>();   // Cria lista
    for (int i = 0; i < graph.getN(); i++) {            // Percorre grafo INTEIRO
      GraphNode node = graph.getNodeByIndex(i);         // Index -> Node
      if (node instanceof Category) {                   // Se esse vértice for do tipo categoria,
        allCategories.add((Category) node);             // insere na lista de todas as categorias
      }
    }

    // ------------------------ Mostra as categorias existentes ------------------------
    System.out.println("Categorias existentes:");
    for (Category cat : allCategories) {
      System.out.println("- " + cat.getName());
    }

    // ------------------- Seleção das categorias preferidas do usuário -------------------
    while (true) {
      System.out.println("\nDigite o nome da(s) categoria(s) que você gosta uma de cada vez ou 'PRONTO' para finalizar o quiz:");
      System.out.print("Sua escolha: ");
      String input = sc.nextLine();

      if (input.equals("PRONTO")) break;       // Finalizar quiz

      boolean found = false;                          // Flag para verificar se categoria digitada existe
      for (Category cat : allCategories) {
        if (cat.getName().equals(input)) {            // Se foi digitada corretamente,
          graph.insereA(user, cat, 1);  // CRIA ARESTA INICIAL DE PESO DE INTERESSE IGUAL A 1
          System.out.println("Categoria '" + cat.getName() + "' adicionada aos seus interesses.");
          found = true;
          break;
        }
      }
      if (!found) {                                   // Se não foi digitada corretamente, informa o usuário
        System.out.println("Essa categoria não existe.");
      }
    }
  }
 
  // -------------------------------------------------------------------------
  //  ATUALIZAR PESO DE ARESTA - MÉTODO AUXILIAR DO PROCESSADOR DE INTERAÇÃO
  // -------------------------------------------------------------------------
  private void updateEdgeWeight(TGrafo graph, int indexA, int indexB, int peso, boolean somar) {
    
    boolean arestaExiste = false; // Flag para segurança no final do método

    // --------- SE A ARESTA ENTRE A <-> B JÁ EXISTE, ATUAlIZA ELA BIDIRECIOONALMENTE ---------

    // Procura e atualiza na lista de adjacência de A (A -> B)
    TNo vizinhoA = graph.getAdj(indexA);
    while (vizinhoA != null) {
      if (vizinhoA.w == indexB) {
        vizinhoA.peso = somar ? ((vizinhoA.peso != null ? vizinhoA.peso : 1) + peso) : peso;
        arestaExiste = true;
        break;
      }
      vizinhoA = vizinhoA.prox;
    }
    // Procura e atualiza na lista de adjacência de B (B -> A)
    TNo vizinhoB = graph.getAdj(indexB);
    while (vizinhoB != null) {
      if (vizinhoB.w == indexA) {
        vizinhoB.peso = somar ? ((vizinhoB.peso != null ? vizinhoB.peso : 1) + peso) : peso;
        break;
      }
      vizinhoB = vizinhoB.prox;
    }

    // ----------------- SE A ARESTA ENTRE A <-> B >NÃO< EXISTE, CRIA ELA  -----------------
    if (!arestaExiste) {
      graph.insereA(graph.getNodeByIndex(indexA), graph.getNodeByIndex(indexB), peso);
    }
  }

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // PROCESSADOR DE INTERAÇÃO DO USUÁRIO - MÉTODO AUXILIAR DE AÇÃO COM USUÁRIO
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  public void processUserInteraction(User user, Activity act, TGrafo graph, boolean isFavorite) {
    Integer userIndex = graph.getIndexByNodeId(user.getId()); // NodeID -> Index
    Integer actIndex = graph.getIndexByNodeId(act.getId());   // NodeID -> Index
    
    if (userIndex == null || actIndex == null) return; // Segurança: se algum dos dois não existir, interrompe processamento

    act.registerClick();                   // Atividade contabiliza esse clique
    
    // Define como os pesos serão calculados/settados dependendo se a atividade foi clicada ou favoritada
    int actWeight = isFavorite ? 1 : 0;    // Se a atividade foi "favoritada", isso é registrado fazendo o peso dela valer 1, se foi só "clicada", vale 0
    int selfCatBonus = isFavorite ? 3 : 1; // Se a atividade foi "favoritada", as categorias dessa atividade ganham +3 pontos de interesse, se foi só "clicada", ganham só +1
    
    // Cria/atualiza a aresta User <-> Activity
    // PS: "false" é colocado no último parâmetro pois a aresta é User <-> Activity, o que significa que o valor NÃO deve ser somado, e sim settado
    updateEdgeWeight(graph, userIndex, actIndex, actWeight, false);
    
    // ---------------- SALTO 1: Atividade -> Suas Categorias ----------------
    TNo vizinhoAct = graph.getAdj(actIndex);
    
    while (vizinhoAct != null) {           // Percorre lista ligada dos vizinhos (categorias) dessa atividade
      GraphNode nodeCat = graph.getNodeByIndex(vizinhoAct.w); // Pega a vértice atual
      
      if (nodeCat instanceof Category) {   // Confirma que o vértice é realmente do tipo categoria
        // Aumenta o peso dessa categorias para o usuário
        updateEdgeWeight(graph, userIndex, vizinhoAct.w, selfCatBonus, true);
      }
      vizinhoAct = vizinhoAct.prox;
    }

    // ---------------- SALTO 2: Atividade -> Outros Usuários ----------------
    vizinhoAct = graph.getAdj(actIndex);   // Reinicia o ponteiro da lista de adjacências da atividade de novo

    while (vizinhoAct != null) {           // Percorre lista ligada dos vizinhos (usuários) dessa atividade
      GraphNode nodeOutro = graph.getNodeByIndex(vizinhoAct.w); // Pega a vértice atual
      
      if (nodeOutro instanceof User && !nodeOutro.getId().equals(user.getId())) { // Confirma que o vértice é realmente do tipo usuário E que não é o usuário atual
        
        // Verifica se o outro usuário favoritou (peso 1) ou só clicou (peso 0) na mesma atividade
        int otherUserInteraction = (vizinhoAct.peso != null) ? vizinhoAct.peso : 0;
        int collabBonus = (otherUserInteraction == 1) ? 2 : 1;  // Se o outro usuário favoritou, as preferências dele valerão o dobro de pontos pro usuário atual

        // ---------------- SALTO 3: Outro Usuário -> Categorias Dele ----------------
        TNo vizinhoOutro = graph.getAdj(vizinhoAct.w);

        while (vizinhoOutro != null) {     // Percorre lista ligada dos vizinhos (categorias) desse outro usuário
          GraphNode nodeCatOther = graph.getNodeByIndex(vizinhoOutro.w); // Pega a vértice atual

          if (nodeCatOther instanceof Category) { // Confirma que o vértice é realmente do tipo categoria
            // ----------------------- COLLABORATIVE-FILTERING -----------------------
            updateEdgeWeight(graph, userIndex, vizinhoOutro.w, collabBonus, true); // Gostos do outro usuário influenciam gostos do usuário atual
          }
          vizinhoOutro = vizinhoOutro.prox;
        }
      }
      vizinhoAct = vizinhoAct.prox;
    }
  }
  
  // -------------------------------------------------------------------------
  //    CÁLCULO DE DISTÂNCIA NORMALIZADA - MÉTODO AUXILIAR DE RECOMENDAÇÃO
  // -------------------------------------------------------------------------
  private double normalizedDistanceCalculation(User u, Activity a) {
    // Se o usuário não tem coordenadas cadastradas, retornamos distância média (0.5)
    if (u.getLat() == 0.0 && u.getLon() == 0.0) {
      return 0.5;
    }
    
    // -------------------- FÓRMULA DE HAVERSINE --------------------
    final int R = 6371;                // Raio da Terra em Km

    double lat1 = u.getLat();
    double lon1 = u.getLon();
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
      return 1.0;                      // Implica penalidade máxima no score
    }

    return distanceInKm / maxDistance; // Proporcional (ex: 10km vira 0.2)
}

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  //          RECOMENDAÇÃO - MÉTODO AUXILIAR DE AÇÃO COM USUÁRIO
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  public List<Activity> recommendation(User user, TGrafo graph) {
    Integer userIndex = graph.getIndexByNodeId(user.getId()); // NodeID -> Index
    
    List<Activity> activities = new ArrayList<>();            // Lista de atividades que podem ser recomendadas
    List<Double> activitiesScore = new ArrayList<>();         // Lista de scoring dessas atividades

    // ---------------- SALTO 1: Usuário -> Categorias Preferidas ----------------
    TNo neighboringCategory = graph.getAdj(userIndex);

    while (neighboringCategory != null) {                              // Percorre lista ligada de categorias adjacentes
      GraphNode nodeCat = graph.getNodeByIndex(neighboringCategory.w); // Pega categoria
      
      if (nodeCat instanceof Category) {                      // Confirma que é mesmo uma categoria antes de prosseguir
        
        // Pega o peso do gosto do usuário por essa categoria. Se for nulo/zero, assume 1.0 por segurança.
        double weightUC = (neighboringCategory.peso != null && neighboringCategory.peso > 0) ? neighboringCategory.peso : 1.0;

        // ---------------- SALTO 2: Categoria -> Atividades ----------------
        TNo neighboringActivity = graph.getAdj(neighboringCategory.w);
        
        while (neighboringActivity != null) {                          // Percorre lista ligada de atividades adjacentes
          GraphNode nodeAct = graph.getNodeByIndex(neighboringActivity.w); // Pega atividade

          if (nodeAct instanceof Activity) {                  // Confirma que é mesmo uma atividade antes de prosseguir
            Activity act = (Activity) nodeAct;                // Especifica que o node é tipo Activity para acessar atributos mais específicos
            
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

            // -------------------------------- Adiciona atividade nova --------------------------------
            int index = activities.indexOf(act);              // Verifica se a atividade já está na lista de atividades possíveis a serem recomendadas (indexOf retorna -1 quando não estiver)
            if (index == -1) {                                // Se a atividade não existe ainda na lista (primeira vez que é encontrada),
              activities.add(act);                            // simplesmente adiciona a atividade na lista de possíveis recomendações
              activitiesScore.add(currentScore);              // e o score dela na lista de scores
            } else {                                          // Se ela JÁ EXISTE na lista (veio de outra categoria analisada no passado),
              // --------- AJUSTE FINAL NO SCORE NO CASO DE ATIVIDADE COM MÚLTIPLAS CATEGORIAS PREFERIDAS ---------
              double bestScore = Math.min(currentScore, activitiesScore.get(index)); // Comparar o score atual com o score vindo da outra categoria e pegar o melhor (menor) dos dois
              double bonus = 0.15;                            // Define um bônus extra porque a atividade está ligada à mais de uma categoria preferida do usuário simultaneamente
              activitiesScore.set(index, bestScore - bonus);  // Atualizamos o score para ser o melhor score encontrado dentre os dois E também possuir o bônus de match extra
            }
          }
          neighboringActivity = neighboringActivity.prox; // Avança para próxima atividade
        }
      }
      neighboringCategory = neighboringCategory.prox; // Avança para próxima categoria
    }

    // --------------------- ORDENA ATIVIDADES DE RECOMENDAÇÃO COM BUBBLE SORT ---------------------
    // PS: A ordenação das atividades em si não está otimizada propositalmente para simplificar
    // o processo. Numa plataforma real, provavelmente haveria um enorme foco em solucionar a otimização
    // da atualização ao vivo das arestas de preferências do usuário (User <-> Category) E da seleção/apresentação
    // dos resultados. Esse problema seria tão complexo (talvez mais complexo que o cálculo de recomendação em si)
    // que provavelmente se tornaria o problema principal de uma plataforma real, como o YouTube, que possui
    // tantos vídeos que jamais poderia calcular para TODOS os seus vídeos existentes quais combinam ou não com
    // TODOS os usuários. Logo, como o foco do nosso projeto é o cálculo da recomendação em si, e foco do projeto
    // é estruturar o grafo de forma a otimizar o CÁLCULO da recomendação em si, além de querermos propositalmente
    // testar que ele está funcionando de TODOS para TODOS corretamente, decidimos não se preocupar em
    // pensar na otimização da chamada desse cálculo múltiplas vezes e, como agora, nem com a otimização da
    // apresentação/reordenação dos resultados.
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
    
    return activities;
  }
  
  // =========================================================================
  //                            AÇÃO COM USUÁRIO
  // =========================================================================
  private void userAction(TGrafo graph) {

    // --------------- Se não existem usuários cadastrado, interrompe função ---------------
    if (registeredUsers.isEmpty()) {
      System.out.println("\nNenhum usuário cadastrado ainda.");
      return;
    }

    // --------------- Mostra usuários cadastrados/existentes para selecionar um ---------------
    System.out.println("\n--- SELECIONE UM USUÁRIO ---");
    for (int i = 0; i < registeredUsers.size(); i++) {
      System.out.println((i + 1) + ". " + registeredUsers.get(i).getName());
    }
    System.out.println("0. Voltar");
    System.out.print("Sua escolha: ");
    
    int op = lerInteiro();                                          // Recebe opção de qual usuário foi selecionado no terminal
    if (op <= 0 || op > registeredUsers.size()) return;             // Se a opção for inválida, interrompe função

    User selectedUser = registeredUsers.get(op - 1);                // Pega usuário selecionado
    Integer userIndex = graph.getIndexByNodeId(selectedUser.getId()); // Descobr Index (NodeID -> Index) do usuário selecionado

    // --------------- Se for o usuário acabou de ser cadastrado, ele realiza quiz ---------------
    if (graph.getAdj(userIndex) == null) { // Se não tem vizinhos (categorias preferidas), ele acabou de ser cadastrado
      runQuiz(selectedUser, graph);        // Logo, o faz realizar o quiz para ligar arestas com primeiras categorias
    }

    // ============================== LOOP PRINCIPAL DO SISTEMA ==============================
    while (true) {
      System.out.println("\n---------------------------- MODO USUÁRIO ----------------------------");
      System.out.println("As atividades abaixo foram calculadas com base no seu perfil e nas suas interações com a plataforma:");

      // =-=-=-= GERA TODAS AS RECOMENDAÇÕES DO USUÁRIO (SISTEMA DE RECOMENDAÇÃO) =-=-=-=
      // Faz APENAS dois saltos de BFS / Percorre apenas dois níveis de BFS
      List<Activity> allRecommendations = recommendation(selectedUser, graph);
      
      // Lista para controlar o que já está visível na tela (Paginação)
      List<Activity> visibleActivities = new ArrayList<>();
      
      boolean flag = true;
      while (flag) { // Loop para o usuário poder solicitar o carregamento de mais atividades
        
        // Carrega 10 atividades das mais populares para as menos populares
        int start = visibleActivities.size();
        int end = start + 10;
        for (int i = start; i < end; i++) {
          if (i >= allRecommendations.size()) {
            break;
          }
          visibleActivities.add(allRecommendations.get(i));
        }

        // Mostra as atividades carregadas
        System.out.println();
        for (int i = 0; i < visibleActivities.size(); i++) {
          Activity activity = visibleActivities.get(i);
          System.out.println(activity.getName() + " (" + activity.getClickCount() + " cliques)");
        }

        // ------------------------- Menu de opções para prosseguir -------------------------
        System.out.println("\n1. [CLICAR] - Simular acesso a uma atividade");
        System.out.println("2. [FAVORITAR] - Simular salvar atividade (super bônus)");
        System.out.println("3. [MAIS] - Carregar mais atividades");
        System.out.println("4. [SAIR] - Deslogar do usuário");
        System.out.print("Sua escolha: ");

        int action = lerInteiro();

        switch (action) {
          case 1:
          case 2:
            String actionName = (action == 1) ? "clicada" : "favoritada";
            System.out.println("\nDigite o nome exato da atividade a ser " + actionName + ":");
            String inputName = sc.nextLine();

            boolean activity_found = false;
            for (Activity act : visibleActivities) {
              if (act.getName().equalsIgnoreCase(inputName)) {
                
                // --- PROCESSAMENTO DA INTERAÇÂO DO USUÁRIO ---
                // Esse é o collaborative-filtering, onde as arestas do grafo serão atualizadas
                // a partir do vértice do User atual, desse jeito:
                // User (atual) -> Atividade clicada -> Categorias Adjacentes
                // User (atual) -> Atividade clicada -> Usuários Adjacentes -> Categorias (preferidas desses usuários)
                // O resultado final desse processamento é que as arestas User (atual) -> Categorias (preferidas)
                // terão seus pesos recalculados com base nas categorias da atividade clicada / favoritada
                // E nos gostos de outros usuários que também clicaram / favoritaram a mesma atividade
                boolean isFavorite = (action == 2);
                processUserInteraction(selectedUser, act, graph, isFavorite);
                
                System.out.println("\nVocê interagiu com: " + act.getName());
                
                activity_found = true;
                flag = false; // Interrompe o loop da página para refazer a recommendation
                break;
              }
            }
            if (!activity_found) {
              System.out.println("O nome da atividade a ser 'clicada' foi digitado errado.");
            }

          case 3:
            if (visibleActivities.size() >= allRecommendations.size()) {
              System.out.println("\n[ LISTA DE RECOMENDAÇÕES JÁ FOI COMPLETAMENTE CARREGADA ]");
            }
            break;

          case 4:
            return; // Encerra o método / "Desloga" usuário

          default:
            System.out.println("\nOpção inválida.");
        }
      }
    }
  }

  // =========================================================================
  //                            MODO VISITANTE
  // =========================================================================
  private void guestAction(TGrafo graph) {
    while (true) {
      System.out.println("\n---------------------------- MODO VISITANTE ----------------------------");
      System.out.println("\nAs atividades são ordenadas da mais popular (atividade mais clicada) para a menos popular.");
      
      // -------------- Cria uma lista de TODAS as atividades existentes no grafo --------------
      List<Activity> allActivities = new ArrayList<>();   // Cria lista
      for (int i = 0; i < graph.getN(); i++) {            // Percorre grafo INTEIRO
        GraphNode node = graph.getNodeByIndex(i);         // Index -> Node
        if (node instanceof Activity) {                   // Se esse vértice for do tipo atividade,
          allActivities.add((Activity) node);             // insere na lista de todas as atividades
        }
      }

      // -------------- Ordena por mais clicada à menos clicada usando Bubble Sort --------------
      // PS: Num sistema real, haveriam otimizações feitas na base de dados.
      // Por ex, talvez as atividades fossem classificadas por 50+ cliques ou 100+ cliques, então 1000+ cliques, etc,
      // facilitando a ordenação em tempo real no banco de dados sempre que a atividade batesse uma "meta" de cliques, ou talvez
      // qualquer outro tipo de otimização melhor. Porém, para nosso caso atual, é mais didático varrer o grafo inteiro
      // e ordená-lo de forma bem simples, mesmo que não otimizada, para ter absoluta certeza de que qualquer diferença
      // pequena nos cliques será notada.
      for (int i = 0; i < allActivities.size() - 1; i++) {       // Esse loop controla quantas passadas completas faremos na lista.
        for (int j = 0; j < allActivities.size() - i - 1; j++) { // Esse loop compara os vizinhos. O "- i" serve para ignorar os últimos elementos que já foram ordenados anteriormente
          if (allActivities.get(j).getClickCount() < allActivities.get(j + 1).getClickCount()) { // Se a atividade atual tem MENOS cliques que a próxima, elas estão na ordem errada
            Activity temp = allActivities.get(j);                                                // Realiza o Swap
            allActivities.set(j, allActivities.get(j + 1));
            allActivities.set(j + 1, temp);
          }
        }
      }


      // -------------- Pegar as recomendações de atividades e mostrar na tela --------------
      List<Activity> recommendedActivities = new ArrayList<>(); // Lista de atividades recomendadas iniciando vazia

      boolean flag = true;
      while (flag) {                                            // Loop para o usuário poder solicitar o carregamento de mais atividades

        // Carrega 10 atividades das mais populares para as menos populares
        int start = recommendedActivities.size();
        int end = start + 10;
        for (int i = start; i < end; i++) {
          if (i >= allActivities.size()) {
            break;
          }
          recommendedActivities.add(allActivities.get(i));
        }

        // Mostra as atividades carregadas
        System.out.println();
        for (int i = 0; i < recommendedActivities.size(); i++) {
          Activity activity = recommendedActivities.get(i);
          System.out.println(activity.getName() + " (" + activity.getClickCount() + " cliques)");
        }

        // ------------------------- Menu de opções para prosseguir -------------------------
        System.out.println("\n1. [CLICAR] - Simular novas recomendações após 'clicar' numa atividade");
        System.out.println("2. [MAIS] - Simular o 'scroll' do usuário carregando mais atividades");
        System.out.println("3. [SAIR] - Volta no menu anterior");
        System.out.print("Sua escolha: ");
  
        int op = lerInteiro();
        
        switch (op) {
          case 1:
            System.out.println("\nDigite o nome da atividade a ser 'clicada'.");
            String input = sc.nextLine();

            boolean activity_found = false; // Flag auxiliar para informar o usuário se ele digitou nome da atividade errado
            for (Activity activity : recommendedActivities) {
              if (activity.getName().equals(input)) {
                activity.registerClick();
                System.out.println("\nVocê clicou em: " + activity.getName());
                activity_found = true;
                flag = false;              // Interrompe o while atual para o while de fora e refazer a recomendação
                break;
              }
            }
            if (!activity_found) {
              System.out.println("O nome da atividade a ser 'clicada' foi digitado errado.");
            }

            break;
          case 2:
            break;
          case 3:
            return;
          default:
            System.out.println("Opção inválida.");
        }
      }
    }
  }

  // =========================================================================
  //                             MODO FILTRO
  // =========================================================================
  private void filterAction(TGrafo graph) {

    // -------------------- Variáveis auxiliares de filtro --------------------
    List<Category> categoryFilter = new ArrayList<>();
    String nameFilter = "";

    // ---------------------------- Modo Filtro -------------------------------
    while (true) {

      // ---------- Impressão detalhada dos filtros selecionados atuais ----------
      System.out.println("\n---------------------------- MODO FILTRO ----------------------------");
      System.out.println("Filtros atuais:");
      System.out.println("- Nome: " + (nameFilter.isEmpty() ? "Nenhum" : nameFilter));
      System.out.print("- Categorias: ");
      if (categoryFilter.isEmpty()) {
        System.out.println("Nenhuma");
      } else {
        for (Category c : categoryFilter) System.out.print(c.getName() + " | ");
        System.out.println();
      }
      
      System.out.println("\n1. Definir categorias a serem filtradas");
      System.out.println("2. Definir nome a ser filtrado");
      System.out.println("3. Iniciar Filtro");
      System.out.println("4. Voltar");
      System.out.print("Sua escolha: ");

      // --------------------------- Seleção do Terminal ---------------------------
      int op = lerInteiro();
      switch (op) {
        case 1:
          // -------------------- Adicionando categorias no filtro --------------------
          while (true) {
            System.out.print("\nDigite o nome da Categoria para adicionar no filtro ou 'SAIR' para voltar: ");
            String input = sc.nextLine();

            if (input.equals("SAIR")) break; // Finalizar seleção

            boolean achou = false;                          // Flag para verificar se categoria digitada existe
            for (int i = 0; i < graph.getN(); i++) {        // Percorre grafo INTEIRO (não otimizado de propósito)
              GraphNode n = graph.getNodeByIndex(i);        // Index -> NodeID
              if (n instanceof Category && n.getName().equals(input)) { // Se o vértice atual é do tipo categoria e seu nome é o mesmo que o digitado,
                  categoryFilter.add((Category) n);         // Adiciona a categoria no filtro
                System.out.println("Categoria adicionada ao filtro.");
                achou = true;
                break;
              }
            }                                               // Se não foi digitada corretamente, informa o usuário
            if (!achou) System.out.println("Essa categoria não existe.");
          }
          break;
          
        case 2:
          // ------------- Adicionando nome/termo de atividade no filtro --------------
          System.out.print("\nDigite o termo para buscar nos nomes das atividades: ");
          nameFilter = sc.nextLine();
          break;
          
        case 3:
          // -------------------- Mostra resultados com o filtro ---------------------
          System.out.println("\n--- RESULTADOS DO FILTRO ---");
          
          for (int i = 0; i < graph.getN(); i++) {       // Percorre o grafo inteiro
            GraphNode node = graph.getNodeByIndex(i);    // Vértice atual
            if (node instanceof Activity) {              // Verifica se vértice atual é do tipo Activity
              Activity act = (Activity) node;            // Se sim, especifica que ele é do tipo Activity (mais específico que GraphNode)
              
              // -------------------- Filtro de termo/nome --------------------
              boolean filteredNameIsValid = nameFilter.isEmpty() || act.getName().toLowerCase().contains(nameFilter.toLowerCase());
              
              // --------------------- Filtro de categoria --------------------
              boolean filteredCategoryIsValid = categoryFilter.isEmpty(); // Se não houver filtro estiver vazio, já vai ser true p/ essa atividade
              if (!filteredCategoryIsValid) {            // Se o filtro não estiver vazio assumimos que é false p/ essa atividade e validamos ela
                TNo vizinho = graph.getAdj(i);           // Verificamos as categorias às quais essa atividade pertence
                while (vizinho != null) {                // Percorremos seus vizinhos até o final da lista ligada
                  GraphNode nodeVizinho = graph.getNodeByIndex(vizinho.w); // Index -> NodeID do vizinho
                  // nodeVizinho instanceof Category -> Confere se o vizinho é uma categoria mesmo e não um usuário ligado à essa atividade
                  // categoryFilter.contains(nodeVizinho) -> Confere se a categoria está ou não no filtro
                  if (nodeVizinho instanceof Category && categoryFilter.contains(nodeVizinho)) {
                    filteredCategoryIsValid = true;     // Se a categoria estiver no filtro, então ela é válida
                    break;
                  }
                  vizinho = vizinho.prox;               // Continuamos vendo os vizinhos dessa atividade seguindo a lista ligada
                }
              }

              if (filteredNameIsValid && filteredCategoryIsValid) { // Se a atividade passou pelos dois filtros, ela é impressa
                System.out.println("- " + act.getName() + " (" + act.getClickCount() + " cliques)");
              }
            }
          }
          
          System.out.println("Impressão dos resultados aplicando os filtros definidos foi finalizada.");
          break;

        case 4:
          return;
          
        default:
          System.out.println("Opção inválida.");
      }
    }
  }
}