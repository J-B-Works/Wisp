package software_engineering_wisp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class RecommendationTesting {

    public void runTest(Scanner sc) {
        // ------------------------------------------------------------------------------------
        //                                   INICIALIZAÇÃO
        // ------------------------------------------------------------------------------------

        // Cria o grafo com os dados de exemplo
        TGrafo testGraph = new TGrafo(0);
        insertData(testGraph);

        // Cria o nó do usuário
        System.out.print("Digite o nome do usuário: ");
        String userName = sc.nextLine();
        User user = new User(userName);
        testGraph.insereV(user);                              // Insere nó do User no grafo

        // Verifica quais categorias existem no grafo MANUALMENTE
        List<Category> categoriesList = new ArrayList<>();
        for (int i = 0; i < testGraph.getN(); i++) {          // Percorre todos os nós do grafo,
            GraphNode node = testGraph.getNodeByIndex(i);     // Pego nó atual 
            if (node instanceof Category) {                   // Verifico se ele é do tipo Category
                categoriesList.add((Category) node);          // Se sim, adiciono na lista de categorias existentes
            }
        }

        // ====================================================================================
        //                                       QUIZ
        // ====================================================================================
        System.out.println("\n=============== QUIZ ===============");
        System.out.println("Digite um nome por vez de categoria de atividade do seu interesse para selecioná-la");
        System.out.println("Digite \"encerrar quiz\" quando terminar");
        System.out.println("\nCategorias:");
        for (Category cat : categoriesList) {                // Percorre lista de categorias
            System.out.println("- " + cat.getName());        // Imprime cada categoria
        }

        while (true) {                                       // Loop do quiz
            System.out.print("\nSelecione: ");
            String input = sc.nextLine().trim();             // Lê entrada do usuário e remove espaços extras

            if (input.equalsIgnoreCase("encerrar quiz")) { // Se o usuário digitar "encerrar quiz", para loop
                break;
            }

            Category selectedCategory = null;                // Categoria selecionada deixará de ser null após validação do nome de categoria que usuário digitou
            
            // Valida se o nome da categoria selecionada existe na lista de categorias
            for (Category cat : categoriesList) {            // Percorre lista de categorias
                if (cat.getName().equalsIgnoreCase(input)) { // Para cada categoria na lista, verifica se seu nome bate com o nome digitado pelo usuário
                    selectedCategory = cat;                  // Se sim, define ela como categoria selecionada
                    break;
                }
            }

            // Cria uma aresta entre o usuário e a categoria selecionada
            if (selectedCategory != null) {                  // Verifica se uma categoria foi realmente selecionada ou não
                testGraph.insereA(user, selectedCategory);   // Se sim, insere uma aresta no grafo entre o usuário <-> categoria selecionada
            } else {                                         // Se não, informa usuário e faz nada
                System.out.println("Nome de categoria não existe.");
            }
        }

        // Quando o usuário encerrar o quiz, gera as recomendações
        generateInitialRecommendations(testGraph, user);
        
        // Encerra programa
        System.exit(0);
    }

    // ====================================================================================
    //                          GERADOR DE RECOMENDAÇÕES (WIP)
    // ====================================================================================
    private void generateInitialRecommendations(TGrafo graph, User user) {
        System.out.println("\n=============== RECOMENDAÇÕES ===============");

        // ------------------------------- INICIALIZAÇÃO -------------------------------
        Set<String> recommendedActivities = new HashSet<>(); // HashSet facilita a implementação por impedir elementos duplicados, assim não é necessário array de "visitados"
        Integer userIndex = graph.getIndexByNodeId(user.getId()); // Traduz NodeID -> Index do usuário

        // =-=-=-= PRIMEIRO NÍVEL DE PROFUNDIDADE =-=-=-=
        TNo userNeighbors = graph.getAdj(userIndex);         // Método pronto que pega nós vizinhos adjacentes (do usuário, ou seja, suas categorias)
        
        // Se ele não tiver selecionado nenhuma categoria, ele não terá vizinhos adjacentes, então avisa o usuário e encerra
        if (userNeighbors == null) {
          System.out.println("Você não selecionou nenhuma categoria! Sem recomendações no momento.");
          return;
        }

        // =============================== RECOMENDAÇÕES ===============================
        while (userNeighbors != null) {                      // Percorre lista encadeada
            int categoryIndex = userNeighbors.w;             // w é o índice do destino da aresta
            GraphNode catNode = graph.getNodeByIndex(categoryIndex); // Traduz Index -> NodeID da categoria
            
           // =-=-=-= SEGUNDO NÍVEL DE PROFUNDIDADE =-=-=-=
            TNo categoryNeighbors = graph.getAdj(categoryIndex); // Método pronto que pega nós vizinhos adjacentes (da categoria, ou seja, suas atividades)
            while (categoryNeighbors != null) {              // Percorre lista encadeada
                int activityIndex = categoryNeighbors.w;     // w é o índice do destino da aresta
                GraphNode actNode = graph.getNodeByIndex(activityIndex); // Traduz Index -> NodeID da atividade
                if (actNode instanceof Activity) {              // Segurança necessária para sistema não sem querer recomendar um outro usuário
                  recommendedActivities.add(actNode.getName()); // Adiciona atividade no HashSet (se já tiver sido visitada antes, automaticamente não será re-adicionada)                
                }
                categoryNeighbors = categoryNeighbors.prox;  // Avança para o próximo vizinho da Categoria
            }
            userNeighbors = userNeighbors.prox; // Avança para a próxima Categoria que o Usuário curtiu
        }

        // Imprime recomendações
        for (String actName : recommendedActivities) {
            System.out.println("- " + actName);
        }
    }


    // ====================================================================================
    //                                INSERE DADOS DE TESTE
    // ====================================================================================

    private void insertData(TGrafo graph) {
        
        // --------------- CRIAÇÃO DAS CATEGORIAS ---------------
        Category[] categories = new Category[5];

        // Índice 0: Música
        categories[0] = new Category("Música");
        graph.insereV(categories[0]);

        // Índice 1: Teatro
        categories[1] = new Category("Teatro");
        graph.insereV(categories[1]);

        // Índice 2: Cinema
        categories[2] = new Category("Cinema");
        graph.insereV(categories[2]);

        // Índice 3: Tecnologia
        categories[3] = new Category("Tecnologia");
        graph.insereV(categories[3]);

        // Índice 4: Literatura
        categories[4] = new Category("Literatura");
        graph.insereV(categories[4]);




        // Estabelecimento de teste genérico para construir as atividades
        Establishment testEstablishment = new Establishment("Local Teste", 0, 0);


        
        // --------------- CRIAÇÃO DAS ATIVIDADES (QUE SE LIGAM À 1 CATEGORIA APENAS) ---------------
        
        // --- Aresta com Música ---
        Activity act1 = new Activity("Aula de Violão", "N/A", testEstablishment);
        graph.insereV(act1);
        graph.insereA(act1, categories[0]);

        Activity act2 = new Activity("Roda de Samba", "N/A", testEstablishment);
        graph.insereV(act2);
        graph.insereA(act2, categories[0]);

        Activity act3 = new Activity("Concerto Sinfônico", "N/A", testEstablishment);
        graph.insereV(act3);
        graph.insereA(act3, categories[0]);

        // --- Aresta com Teatro ---
        Activity act4 = new Activity("Peça de Comédia", "N/A", testEstablishment);
        graph.insereV(act4);
        graph.insereA(act4, categories[1]);

        Activity act5 = new Activity("Drama Clássico", "N/A", testEstablishment);
        graph.insereV(act5);
        graph.insereA(act5, categories[1]);

        Activity act6 = new Activity("Teatro Infantil", "N/A", testEstablishment);
        graph.insereV(act6);
        graph.insereA(act6, categories[1]);

        // --- Aresta com Cinema ---
        Activity act7 = new Activity("Festival de Curtas", "N/A", testEstablishment);
        graph.insereV(act7);
        graph.insereA(act7, categories[2]);

        Activity act8 = new Activity("Sessão Pipoca", "N/A", testEstablishment);
        graph.insereV(act8);
        graph.insereA(act8, categories[2]);

        Activity act9 = new Activity("Cinema Mudo", "N/A", testEstablishment);
        graph.insereV(act9);
        graph.insereA(act9, categories[2]);

        // --- Aresta com Tecnologia ---
        Activity act10 = new Activity("Hackathon", "N/A", testEstablishment);
        graph.insereV(act10);
        graph.insereA(act10, categories[3]);

        Activity act11 = new Activity("Curso de Robótica", "N/A", testEstablishment);
        graph.insereV(act11);
        graph.insereA(act11, categories[3]);

        Activity act12 = new Activity("Feira de Ciências", "N/A", testEstablishment);
        graph.insereV(act12);
        graph.insereA(act12, categories[3]);

        // --- Aresta com Literatura ---
        Activity act13 = new Activity("Clube do Livro", "N/A", testEstablishment);
        graph.insereV(act13);
        graph.insereA(act13, categories[4]);

        Activity act14 = new Activity("Feira de Troca", "N/A", testEstablishment);
        graph.insereV(act14);
        graph.insereA(act14, categories[4]);

        Activity act15 = new Activity("Sarau de Poesia", "N/A", testEstablishment);
        graph.insereV(act15);
        graph.insereA(act15, categories[4]);


        // --------------- CRIAÇÃO DAS ATIVIDADES (QUE SE LIGAM À 2 CATEGORIAS) ---------------

        // Música + Teatro
        Activity actMulti1 = new Activity("Musical da Broadway", "N/A", testEstablishment);
        graph.insereV(actMulti1);
        graph.insereA(actMulti1, categories[0]); 
        graph.insereA(actMulti1, categories[1]); 

        // Cinema + Tecnologia
        Activity actMulti2 = new Activity("Documentário sobre IA", "N/A", testEstablishment);
        graph.insereV(actMulti2);
        graph.insereA(actMulti2, categories[2]); 
        graph.insereA(actMulti2, categories[3]); 

        // Música + Cinema
        Activity actMulti3 = new Activity("Oficina de Trilha Sonora", "N/A", testEstablishment);
        graph.insereV(actMulti3);
        graph.insereA(actMulti3, categories[0]); 
        graph.insereA(actMulti3, categories[2]); 
        
        // Literatura + Tecnologia
        Activity actMulti4 = new Activity("Audiobook Imersivo", "N/A", testEstablishment);
        graph.insereV(actMulti4);
        graph.insereA(actMulti4, categories[4]); 
        graph.insereA(actMulti4, categories[3]); 


        // --------------- CRIAÇÃO DAS ATIVIDADES (QUE SE LIGAM À 3 CATEGORIAS) ---------------

        // Música + Teatro + Cinema
        Activity actMulti5 = new Activity("Show de Projeção Mapeada Interativa", "N/A", testEstablishment);
        graph.insereV(actMulti5);
        graph.insereA(actMulti5, categories[0]); 
        graph.insereA(actMulti5, categories[1]); 
        graph.insereA(actMulti5, categories[2]); 
    }
}