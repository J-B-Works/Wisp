/*

=======================================================
  WISP– Sistema de recomendação de educação e cultura
=======================================================

-- Grupo --
Teoria dos Grafos - Turma: 6G
- Bruna Gonçalves Corte David (RA: 10425696)
- Júlia Andrade (RA: 1042513)

-- Síntese do Conteúdo --

-> Classe que representa um nó do tipo Atividade no grafo
-> Alguns atributos e métodos como link, cliques e localização ainda não estão sendo utilizados, são para uso futuro

-- Histórico de Alterações --

17/05/2026 - Júlia - Transforma antigo graph_theory_wisp_dev nesta classe apagando o que não faz parte de data loading

*/

package graph_theory_wisp.data_loader;

import java.util.Scanner;

import graph_theory_wisp.TGrafo;
import graph_theory_wisp.graph_node_types.Category;
import graph_theory_wisp.graph_node_types.GraphNode;
import graph_theory_wisp.graph_node_types.User;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) {
        TGrafo tgraph = new TGrafo(0);     // Cria objeto grafo vazio (aumenta dinamicamente)
        DataLoader loader = new DataLoader(); // Carrega dados no grafo
        
        int op = -1;
        while (op != 6) {
            System.out.println("\n--- DATA LOADER: Para ser utilizado uma ÚNICA vez ---");
            System.out.println("1. Carregar TODOS os dados reais (FC + SESC)");
            System.out.println("2. Carregar dados reais (FC + SESC) reduzidos (Máx 200 Vértices)");
            System.out.println("3. Adicionar Usuário e criar arestas com vértices existentes no grafo");
            System.out.println("4. Gerar arquivos TXT (grafo.txt e graph_online.txt)");
            System.out.println("5. Mostrar estrutura do Grafo atual (Show)");
            System.out.println("6. Sair");
            System.out.print("Escolha uma opção: ");
            
            op = lerInteiro();

            switch (op) {

                case 1:
                    tgraph = new TGrafo(0);        // Cria novo grafo para substituir atual (com os dados carregados)
                    loader.loadAll(tgraph);           // Carrega TODOS os dados reais (FC + SESC) sem limite
                    System.out.println("TODOS os dados carregados com sucesso! Vértices: " + tgraph.getN() + " | Arestas: " + tgraph.getM());
                    break;

                case 2:
                    tgraph = new TGrafo(0);         // Cria novo grafo para substituir atual (com os dados carregados)
                    loader.loadAll(tgraph, 200); // Passa o limite de 200 vértices
                    System.out.println("Dados reduzidos carregados com sucesso! Vértices: " + tgraph.getN() + " | Arestas: " + tgraph.getM());
                    break;

                case 3:
                    boolean gerenciarUsuarios = true;
                    while (gerenciarUsuarios) {
                        System.out.println("\n--- GERENCIAR USUÁRIOS E INTERAÇÕES ---");
                        System.out.println("1. Criar novo usuário");
                        System.out.println("2. Conectar um usuário a um vértice (Categoria/Atividade)");
                        System.out.println("0. Voltar ao Menu Desenvolvedor");
                        System.out.print("Escolha: ");
                        int optUser = lerInteiro();
                        
                        if (optUser == 1) {
                            System.out.print("Digite o nome do Usuário: ");
                            String nome = sc.nextLine();
                            User u = new User(nome, 0.0, 0.0);          
                            tgraph.insereV(u);                
                            System.out.println("Novo Usuário criado! Nome: " + nome + " | Índice: " + tgraph.getIndexByNodeId(u.getId()));
                        } 
                        else if (optUser == 2) {
                            System.out.print("Digite o ÍNDICE do Usuário: ");
                            int idxUser = lerInteiro();
                            System.out.print("Digite o ÍNDICE do alvo (Categoria/Atividade): ");
                            int idxTarget = lerInteiro();
                            
                            try {
                                GraphNode userNode = tgraph.getNodeByIndex(idxUser);
                                GraphNode targetNode = tgraph.getNodeByIndex(idxTarget);
                                
                                // Verifica se é a conexão especial User <-> Category para pedir peso
                                if (userNode instanceof User && targetNode instanceof Category) {
                                    System.out.print("Interação com Categoria detectada! Informe o peso do interesse (ex: 1 a 5): ");
                                    int peso = lerInteiro();
                                    tgraph.insereA(userNode, targetNode, peso); // Usa método com peso p/ interações User-Category
                                    System.out.println("Aresta criada com peso " + peso + "!");
                                } else {
                                    tgraph.insereA(userNode, targetNode);       // Usa método sem peso p/ interações normais
                                    System.out.println("Aresta criada com sucesso");
                                }
                            } catch (Exception e) {
                                System.out.println("Erro: Índices inválidos ou não encontrados.");
                            }
                        } 
                        else if (optUser == 0) {
                            gerenciarUsuarios = false;
                        } else {
                            System.out.println("Opção inválida.");
                        }
                    }
                    break;

                case 4:
                    tgraph.exportToTxtFormat("sources/graph_theory_wisp/exported_txt_files/grafo.txt");          // Gera arquivo txt para apresentação
                    tgraph.exportToGraphOnline("sources/graph_theory_wisp/exported_txt_files/graph_online.txt"); // Gera arquivo txt para exportar pro graph online
                    System.out.println("Arquivos gerados com sucesso!");
                    break;

                case 5:
                    tgraph.show();
                    break;

                case 6:
                    break;

                default:
                    System.out.println("Opção inválida.");
            }
        }
        
        sc.close();
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