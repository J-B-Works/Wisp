package graph_theory_wisp_dev;

import java.io.*;
import java.util.*;

public class DataLoader {

    // =========================================================================
    //                   CONSTANTES DE CAMINHO DOS ARQUIVOS
    // =========================================================================

    // Atividades das Fábricas de Cultura
    private static final String FC_CATEGORIES_PATH = "Data/Fabricas_de_Cultura/Categorias.txt"; // Lista de Categorias que nós escolhemos como válidas para o projeto
    private static final String FC_CSV_PATH = "Data/Fabricas_de_Cultura/Atividades_FC.csv";

    // Atividades do SESC
    private static final String SESC_CATEGORIES_PATH = "Data/Sesc/Categorias/Categorias.txt";   // Lista de Categorias que nós escolhemos como válidas para o projeto
    private static final String SESC_CSV_PATH = "Data/Sesc/Atvdds/Atividades_SESC.csv";


    // =========================================================================
    //             CONSTANTES DE COORDENADAS PARA ESTABELECIMENTOS
    // =========================================================================

    // Mapa auxiliar de coordenadas para as Unidades do SESC (Estabelecimentos)
    private static final Map<String, double[]> SESC_COORDS = Map.of(
        "Casa Verde", new double[]{-23.5007, -46.6456},
        "Pompeia", new double[]{-23.5260, -46.6835},
        "Avenida Paulista", new double[]{-23.5706, -46.6456},
        "Consolação", new double[]{-46.6501, -23.5460},
        "Santana", new double[]{-23.4965, -46.6128},
        "14 Bis", new double[]{-23.5578, -46.6520},
        "Centro de Pesquisa e Formação", new double[]{-23.5577, -46.6519},
        "CineSesc", new double[]{-23.5604, -46.6624}
    );

    // Mapa auxiliar de coordenadas para as Fábricas de Cultura (Vila Nova Cachoeirinha e Brasilândia)
    private static final Map<String, double[]> FC_COORDS = Map.of(
        "Vila Nova Cachoeirinha", new double[]{-23.4749, -46.6503},
        "Brasilândia", new double[]{-23.4527, -46.6747}
    );


    // =========================================================================
    //                          CARREGAMENTO DE DADOS
    // =========================================================================

    public void loadAll(TGrafo graph) {
        loadAll(graph, null);                                   // Chama o método passando null como limite default, ou seja, sem limite (carrega tudo)
    }
    public void loadAll(TGrafo graph, Integer limit) {                // PS: O limite é opcional, se for null, carrega tudo. Se for um número, carrega até atingir esse número de vértices no grafo (útil para testes com grafos menores)
        
        // Criamos um mapa GLOBAL para juntar as categorias de FC e SESC (evita ter categorias duplicadas no grafo, já que algumas são iguais entre as duas instituições)
        Map<String, Category> globalCatMap = new HashMap<>();

        // Carrega Fábricas de Cultura Primeiro pois possui menos atividades que as do SESC
        loadFCCategories(graph, globalCatMap);                        // Primeiro carrega as categorias
        loadFCActivities(graph, globalCatMap, limit);                 // Depois carrega as atividades, usando as categorias já carregadas para criar as arestas
        
        // --- LIMITE DE VÉRTICES A SEREM CARREGADAS OPCIONAL ---
        if (limit != null && graph.getN() >= limit) { return; }
        // ------------------------------------------------------
        
        // Carrega SESC logo em seguida
        loadSescCategories(graph, globalCatMap);                      // Primeiro carrega as categorias
        loadSescActivities(graph, globalCatMap, limit);               // Depois carrega as atividades, usando as categorias já carregadas para criar as arestas
    
    }


    // =========================================================================
    //              MÉTODOS DE CARREGAMENTO DA FÁBRICA DE CULTURA
    // =========================================================================

    // Carrega CATEGORIAS das FÁBRICAS DE CULTURA e as insere como vértices no grafo
    private void loadFCCategories(TGrafo graph, Map<String, Category> globalCatMap) {      
        try (BufferedReader br = new BufferedReader(new FileReader(FC_CATEGORIES_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {                  
                if (!line.trim().isEmpty()) {                         
                    String catName = line.trim();
                    
                    // EXCESSÃO: Se a categoria se chamar "Cinema", entra no sistema como "Cinema e Vídeo"
                    // (pois no SESC tem essa categoria com nome diferente, e queremos evitar ter 2 categorias praticamente iguais no grafo só por causa disso)
                    if (catName.equalsIgnoreCase("Cinema")) {
                        catName = "Cinema e Vídeo";
                    }

                    // Só cria o vértice se a categoria ainda não existir no map global de categorias
                    String key = catName.toLowerCase();
                    if (!globalCatMap.containsKey(key)) {
                        Category cat = new Category(catName);         
                        graph.insereV(cat);
                        globalCatMap.put(key, cat);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar categorias FC: " + e.getMessage());
        }
    }

    // Carrega ATIVIDADES das FÁBRICAS DE CULTURA e as insere como vértices no grafo
    private void loadFCActivities(TGrafo graph, Map<String, Category> catMap, Integer limit) {
        Map<String, Establishment> tempEstMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FC_CSV_PATH))) {
            br.readLine(); // Pula cabeçalho
            String line;
            while ((line = br.readLine()) != null) {                  
                
                // --- LIMITE DE VÉRTICES A SEREM CARREGADAS OPCIONAL ---
                if (limit != null && graph.getN() >= limit) { break; } 
                // ------------------------------------------------------
            
                String[] cols = line.split(";");
                if (cols.length < 5) continue;                        // Verifica se linha não está errada

                // Extrai dados das colunas (PS: AINDA não estamos usando todos)
                String actName = cols[0];
                String catListStr = cols[1];
                String unitName = cols[2];
                String link = "https://www.fabricasdecultura.org.br/"; // Todas as atividades da FC possuem o mesmo link do site da FC

                Establishment est = tempEstMap.get(unitName);
                if (est == null) {
                    double[] coords = FC_COORDS.getOrDefault(unitName, new double[]{-23.4754, -46.6623}); // Default para a unidade da Vila Nova Cachoeirinha, caso alguma unidade nova apareça no CSV que não esteja no nosso mapa de coordenadas
                    est = new Establishment(unitName, coords[0], coords[1]);
                    tempEstMap.put(unitName, est);
                }

                // Prepara a lista de categorias válidas ANTES de inserir a atividade no grafo
                // (evita de adicionar atividades que não tenham categoria listada previamente/válida)
                String[] activityCats = catListStr.split(",");
                List<Category> validCatsForThisAct = new ArrayList<>();

                for (String catName : activityCats) {
                    
                    // EXCESSÃO EXCLUSIVA DA: Possui uma categoria chamada "Cinema", quando o SESC possui uma chamada "Cinema e Vídeo", então corrige o nome da categoria
                    String cleanCatName = catName.trim();
                    if (cleanCatName.equalsIgnoreCase("Cinema")) {
                        cleanCatName = "Cinema e Vídeo";
                    }

                    Category catNode = catMap.get(cleanCatName.toLowerCase());
                    if (catNode != null) {
                        validCatsForThisAct.add(catNode);
                    }
                }

                // Cria e insere atividade no grafo (que tenha pelo menos 1 categoria válida)
                if (!validCatsForThisAct.isEmpty()) {                 // Deve ter pelo menos uma categoria válida/listada previamente
                    Activity act = new Activity(actName, link, est);  // Cria atividade com nome, link externo e estabelecimento informados
                    graph.insereV(act);                               // Insere atividade no grafo

                    for (Category catNode : validCatsForThisAct) {    // Cria aresta entre a atividade e suas categorias
                        graph.insereA(act, catNode);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar atividades FC: " + e.getMessage());
        }
    }


    // =========================================================================
    //                    MÉTODOS DE CARREGAMENTO DO SESC
    // =========================================================================

    // Carrega CATEGORIAS dos SESC e as insere como vértices no grafo
    private void loadSescCategories(TGrafo graph, Map<String, Category> globalCatMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(SESC_CATEGORIES_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {                  // Enquanto houver linhas para ler
                if (!line.trim().isEmpty()) {                         // Se a linha for vazia, ignora
                    String catName = line.trim();
                    String key = catName.toLowerCase();
                    
                    // Só cria o vértice se o carregamento da FC já não tiver criado antes
                    if (!globalCatMap.containsKey(key)) {
                        Category cat = new Category(catName);         // Cria nova categoria com o nome dado na linha
                        graph.insereV(cat);                           // Insere a categoria como vértice no grafo
                        globalCatMap.put(key, cat);                   // Armazena a categoria no mapa temporário para referência futura (usado para criar arestas depois)
                    }
                    
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar categorias SESC: " + e.getMessage());
        }
    }

    // Carrega ATIVIDADES dos SESC e as insere como vértices no grafo
    private void loadSescActivities(TGrafo graph, Map<String, Category> catMap, Integer limit) {
        Map<String, Establishment> tempEstMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(SESC_CSV_PATH))) {
            br.readLine();                                            // Pula cabeçalho
            String line;
            while ((line = br.readLine()) != null) {                  // Enquanto houver linhas para ler

                // --- LIMITE DE VÉRTICES A SEREM CARREGADAS OPCIONAL ---
                if (limit != null && graph.getN() >= limit) { break; } 
                // ------------------------------------------------------
            
                
                String[] cols = line.split(";");
                if (cols.length < 8) {                                // Verifica se linha não está errada
                    continue;
                }

                // Extrai dados das colunas (PS: AINDA não estamos usando todos)
                String actName = cols[0];
                String unitName = cols[4];
                String catListStr = cols[5];
                String link = cols[7];

                // Se ainda não existir esse estabelecimento, cria novo estabelecimento com nome e coordenadas
                Establishment est = tempEstMap.get(unitName);
                if (est == null) {
                    double[] coords = SESC_COORDS.getOrDefault(unitName, new double[]{-23.5711, -46.6437}); // Default para a unidade da Paulista, caso alguma unidade nova apareça no CSV que não esteja no nosso mapa de coordenadas
                    est = new Establishment(unitName, coords[0], coords[1]);
                    tempEstMap.put(unitName, est);
                }


                // Prepara a lista de categorias válidas ANTES de inserir a atividade no grafo
                // (evita de adicionar atividades que não tenham categoria listada previamente/válida)
                String[] activityCats = catListStr.split(",");
                List<Category> validCatsForThisAct = new ArrayList<>();

                for (String catName : activityCats) {
                    Category catNode = catMap.get(catName.trim().toLowerCase());
                    if (catNode != null) {
                        validCatsForThisAct.add(catNode);
                    }
                }

                // Cria e insere atividade no grafo (que tenha pelo menos 1 categoria válida)
                if (!validCatsForThisAct.isEmpty()) {                 // Deve ter pelo menos uma categoria válida/listada previamente
                    Activity act = new Activity(actName, link, est);  // Cria atividade com nome, link externo e estabelecimento informados
                    graph.insereV(act);                               // Insere atividade no grafo

                    for (Category catNode : validCatsForThisAct) {    // Cria aresta entre a atividade e suas categorias
                        graph.insereA(act, catNode);
                    }
                }

            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar atividades SESC: " + e.getMessage());
        }
    }
}