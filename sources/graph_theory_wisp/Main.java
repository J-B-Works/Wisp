/*

=======================================================
  WISP– Sistema de recomendação de educação e cultura
=======================================================

-- Grupo --
Teoria dos Grafos - Turma: 6G
- Bruna Gonçalves Corte David (RA: 10425696)
- Júlia Andrade (RA: 1042513)

-- Síntese do Conteúdo --

-> Menu de Opções para interagir com o grafo salvo em grafo.txt
   -> Métodos como carregar grafo.txt, calcular grafo reduzido, inserir e remover nós e arestas, etc

-- Histórico de Alterações --

27/03/2026 - Júlia - Main criada para realizar testes de carregamento de dados no grafo
27/03/2026 - Júlia - Adiciona a opção de interromper o carregamento quando atingir n quantia de nós para fins de teste
01/04/2026 - Bruna - Main refatorada para implementar menu de opções
                -> Menu dev para carregar dados, gerar grafos e testar adição de usuários
                -> Menu normal para lidar apenas com grafo.txt final
01/04/2026 - Bruna - Main refatorada para não conter mais Menu dev, apenas o Menu final
01/04/2026 - Júlia - Adição de cabeçalho, síntese, refatoração de comentários e melhoria das prints

*/


//package graph_theory_wisp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("       BEM-VINDO AO NOSSO PROJETO       ");
        System.out.println("========================================\n");
        System.out.println("Teoria dos Grafos - Turma: 6G");
        System.out.println("- Bruna Gonçalves Corte David (RA: 10425696)");
        System.out.println("- Júlia Andrade (RA: 1042513)\n");

        Menu();

        System.out.println("Encerrando...");
        sc.close();
    }

    // =========================================================================
    //                             MENU DE OPÇÕES
    // =========================================================================

    public static void Menu() {
        TGrafo tgraph = new TGrafo(0); // Cria objeto grafo vazio (aumenta dinamicamente)
        
        int op = -1;
        while (op != 10) {
            System.out.println("\n=======================================================");
            System.out.println(" WISP - SISTEMA DE RECOMENDAÇÃO DE EDUCAÇÃO E CULTURAL");
            System.out.println("=======================================================\n");

            System.out.println("1. Ler dados do arquivo grafo.txt");
            System.out.println("2. Gravar dados no arquivo grafo.txt");
            System.out.println("3. Inserir vértice");
            System.out.println("4. Inserir aresta");
            System.out.println("5. Remover vértice");
            System.out.println("6. Remover aresta");
            System.out.println("7. Mostrar conteúdo do arquivo (grafo.txt)");
            System.out.println("8. Mostrar grafo (lista de adjacências)");
            System.out.println("9. Apresentar conexidade do grafo e o reduzido");
            System.out.println("10. Encerrar a aplicação.");
            System.out.print("Escolha uma opção: ");
            
            op = lerInteiro();
            
            switch (op) {

                case 1:
                    System.out.println("\nLendo grafo.txt...");
                    GraphLoader txtLoader = new GraphLoader();
                    tgraph = new TGrafo(0);
                    txtLoader.loadFromTxt(tgraph, "sources/graph_theory_wisp/grafo.txt");
                    break;

                case 2:
                    tgraph.exportToTxtFormat("sources/graph_theory_wisp/grafo.txt");
                    break;

                case 3:
                    System.out.println("Qual o tipo do vértice?");
                    System.out.println("| 1. Usuário | 2. Categoria | 3. Atividade | -> (Genéricos, sem detalhes)");
                    
                    System.out.print("Digite a opção: ");
                    int tipoNode = lerInteiro();

                    System.out.print("Digite o nome: ");
                    String nomeV = sc.nextLine();
                    
                    GraphNode novoNode;
                    if (tipoNode == 1) { 
                        novoNode = new User(nomeV);
                    }
                    else if (tipoNode == 2) {
                        novoNode = new Category(nomeV);
                    }
                    else {
                        novoNode = new Activity(nomeV, "N/A", new Establishment("N/A", 0, 0));
                    }
                    
                    tgraph.insereV(novoNode);
                    System.out.println("Novo vértice inserido com sucesso!");
                    break;

                case 4:
                    System.out.print("Índice do Vértice de Origem: ");
                    int vOrigem = lerInteiro();
                    System.out.print("Índice do Vértice de Destino: ");
                    int vDestino = lerInteiro();
                    
                    try {
                        GraphNode node1 = tgraph.getNodeByIndex(vOrigem);
                        GraphNode node2 = tgraph.getNodeByIndex(vDestino);
                        
                        // Verifica se é a conexão especial User <-> Category para pedir peso ou se é uma conexão normal sem peso
                        if ((node1 instanceof User && node2 instanceof Category) || (node1 instanceof Category && node2 instanceof User)) {
                            System.out.print("Interação Usuário<->Categoria detectada! Informe o peso do interesse (ex: 1 a 5): ");
                            int peso = lerInteiro();
                            tgraph.insereA(node1, node2, peso);
                        } else {
                            tgraph.insereA(node1, node2);
                        }
                        System.out.println("Aresta inserida com sucesso!");
                    } catch (Exception e) {
                        System.out.println("Erro ao inserir aresta: verifique se os índices existem.");
                    }
                    break;

                case 5:
                    System.out.print("Índice do Vértice a ser removido: ");
                    int vRemover = lerInteiro();
                    try {
                        tgraph.removeV(tgraph.getNodeByIndex(vRemover));
                        System.out.println("Vértice e todas as suas arestas removidos!");
                    } catch (Exception e) {
                        System.out.println("Erro ao remover vértice.");
                    }
                    break;

                case 6:
                    System.out.print("Índice do Vértice de Origem: ");
                    int rOrigem = lerInteiro();
                    System.out.print("Índice do Vértice de Destino: ");
                    int rDestino = lerInteiro();
                    try {
                        GraphNode node1 = tgraph.getNodeByIndex(rOrigem);
                        GraphNode node2 = tgraph.getNodeByIndex(rDestino);
                        tgraph.removeA(node1, node2);
                        System.out.println("Aresta removida com sucesso!");
                    } catch (Exception e) {
                        System.out.println("Erro ao remover aresta.");
                    }
                    break;

                case 7:
                    System.out.println("\n--- CONTEÚDO DE GRAFO.TXT ---");
                    try {
                        List<String> linhas = Files.readAllLines(Paths.get("sources/graph_theory_wisp/grafo.txt"));
                        for (String linha : linhas) {
                            System.out.println(linha);
                        }
                    } catch (IOException e) {
                        System.out.println("Erro: Arquivo grafo.txt não encontrado.");
                    }
                    break;

                case 8:
                    tgraph.show();
                    System.out.println();
                    break;

                case 9:
                    tgraph.printConnectivityAndReducedGraph();
                    break;

                case 10:
                    break;
                    
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    // Método auxiliar para evitar crash do Scanner com letras no lugar de números
    private static int lerInteiro() {
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}