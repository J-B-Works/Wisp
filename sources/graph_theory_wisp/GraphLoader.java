/*

=======================================================
  WISP– Sistema de recomendação de educação e cultura
=======================================================

-- Grupo --
Teoria dos Grafos - Turma: 6G
- Bruna Gonçalves Corte David (RA: 10425696)
- Júlia Andrade (RA: 1042513)

-- Síntese do Conteúdo --

-> Classe de carregamento dos dados do grafo.txt
-> Lê o arquivo no formato do grafo.txt criando os nós e arestas no objeto TGrafo

-- Histórico de Alterações --

01/04/2026 - Júlia - Cria classe GraphLoader.java para ler os dados do grafo.txt e construir o grafo usando a classe TGrafo
01/04/2026 - Bruna - Aperfeiçoa GraphLoader.java e implementa leitura de pesos para as arestas User <-> Category

*/


package graph_theory_wisp;

import java.io.BufferedReader;
import java.io.FileReader;

public class GraphLoader {


    // =========================================================================
    //                        MÉTODO PARA LER DO TXT
    // =========================================================================

    public void loadFromTxt(TGrafo graph, String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();                                                      // Pula a linha inicial do tipo do grafo
            
            // Leitura dos vértices
            int numVertices = Integer.parseInt(br.readLine().trim());           // Lê total n de vértices
            for (int i = 0; i < numVertices; i++) {                             // Lê cada um dos n vértices
                String line = br.readLine();
                // Lê grafo.txt no formato: 0 "Category: Nome_da_Categoria"
                int startQuote = line.indexOf("\"");                        // Encontra a posição da primeira aspa  s
                int endQuote = line.lastIndexOf("\"");                      // Encontra a posição da última aspas
                String fullLabel = line.substring(startQuote + 1, endQuote);     // Extrai o rótulo completo (ex: "Category: Nome_da_Categoria")
                
                String[] parts = fullLabel.split(": ", 2);          // Divide o rótulo em tipo e nome (ex: ["Category", "Nome_da_Categoria"])
                String tipo = parts[0];
                String nome = parts[1];
            
                // Cria nós genéricos com base no tipo e rótulos extraídos
                GraphNode node = null;
                if (tipo.equals("Category")) {
                    node = new Category(nome);
                } else if (tipo.equals("User")) {
                    node = new User(nome);
                } else if (tipo.equals("Activity")) {
                    node = new Activity(nome, "N/A", new Establishment("N/A", 0, 0));
                }

                if (node != null) {                                              // Segurança no caso de erro de tipo
                    graph.insereV(node);                                         // Insere nó no grafo
                }
            }

            // Leitura das arestas
            int numArestas = Integer.parseInt(br.readLine().trim());              // Lê total m de arestas
            for (int i = 0; i < numArestas; i++) {                                // Lê cada uma das m arestas
                // Lê grafo.txt no formato: 0 1 5 (u v peso) ou 0 1 (u v sem peso)
                String[] parts = br.readLine().trim().split(" ");
                int u = Integer.parseInt(parts[0]);
                int v = Integer.parseInt(parts[1]);
                Integer peso = null; // Peso padrão (para arestas Activity <-> Category / Activity <-> User)
                if (parts.length >= 3) {
                    peso = Integer.parseInt(parts[2]);                             // Pega o peso se existir no txt (User <-> Category)
                }

                graph.insereA(graph.getNodeByIndex(u), graph.getNodeByIndex(v), peso); // Insere aresta no grafo, usando os índices origem/destino para obter os nós correspondentes
            }

            System.out.println("grafo.txt carregado com sucesso! Vértices: " + graph.getN() + " | Arestas: " + graph.getM());

        } catch (Exception e) {
            System.err.println("Erro ao carregar grafo.txt: " + e.getMessage());
        }
    }
}