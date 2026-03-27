package graph_theory_wisp;

import java.io.*;
import java.util.*;

public class DataLoader {

    private static final String CATEGORIES_PATH = "Data/Sesc/Categorias/Categorias.txt";
    private static final String SESC_CSV_PATH = "Data/Sesc/Atvdds/Atividades_SESC.csv";

    // Mapa auxiliar de coordenadas para as Unidades do SESC (Estabelecimentos)
    private static final Map<String, double[]> SESC_COORDS = Map.of(
        "Casa Verde", new double[]{-23.5015, -46.6601},
        "Pompeia", new double[]{-23.5262, -46.6872},
        "Avenida Paulista", new double[]{-23.5711, -46.6437}
        // TODO preencher o resto manualmente na hora de testar algoritmos seguindo ex acima
    );


    public void loadAll(TGrafo graph) {
        loadAll(graph, null);                                   // Chama o método passando null como limite default, ou seja, sem limite (carrega tudo)
    }
    public void loadAll(TGrafo graph, Integer limit) {                // PS: O limite é opcional, se for null, carrega tudo. Se for um número, carrega até atingir esse número de vértices no grafo (útil para testes com grafos menores)
        Map<String, Category> tempCategories = loadCategories(graph); // Primeiro carrega as categorias
        loadSescActivities(graph, tempCategories, limit);             // Depois carrega as atividades, usando as categorias já carregadas para criar as arestas
    }


    private Map<String, Category> loadCategories(TGrafo graph) {      // Carrega as categorias do arquivo e as insere como vértices no grafo
        Map<String, Category> tempMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CATEGORIES_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {                  // Enquanto houver linhas para ler
                if (!line.trim().isEmpty()) {                         // Se a linha for vazia, ignora
                    Category cat = new Category(line.trim());         // Cria nova categoria com o nome dado na linha
                    graph.insereV(cat);                               // Insere a categoria como vértice no grafo
                    tempMap.put(cat.getName().toLowerCase(), cat);    // Armazena a categoria no mapa temporário para referência futura (usado para criar arestas depois)
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar categorias: " + e.getMessage());
        }
        return tempMap;
    }

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
                    double[] coords = SESC_COORDS.getOrDefault(unitName, new double[]{-23.5505, -46.6333});
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