package software_engineering_wisp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("            WISP INICIALIZAÇÃO          ");
        System.out.println("========================================");
        System.out.println("1 - Modo DESENVOLVEDOR");
        System.out.println("2 - Modo NORMAL/APRESENTAÇÃO");
        System.out.print("Escolha o modo: ");
        
        int modo = lerInteiro();
        if (modo == 1) {
            DevMenu();
        } else if (modo == 2) {
            DefaultMenu();
        } else {
            System.out.println("Modo inválido. Encerrando.");
        }
        System.out.println("Encerrando...");
        
        sc.close();
    }


    // =========================================================================
    //                          MODO DESENVOLVEDOR
    // =========================================================================

    public static void DevMenu() {
        TGrafo tgraph = new TGrafo(0);     // Cria objeto grafo vazio (aumenta dinamicamente)
        DataLoader loader = new DataLoader(); // Carrega dados no grafo
        
        int op = -1;
        while (op != 6) {
            System.out.println("\n--- MENU DESENVOLVEDOR ---");
            System.out.println("1. Carregar TODOS os dados reais (FC + SESC)");
            System.out.println("2. Carregar dados reais (FC + SESC) reduzidos (Máx 100 Vértices)");
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
                    loader.loadAll(tgraph, 100); // Passa o limite de 70 vértices
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
                            User u = new User(nome);          
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
                    tgraph.exportToTxtFormat("sources/graph_theory_wisp/grafo.txt");          // Gera arquivo txt para apresentação
                    tgraph.exportToGraphOnline("sources/graph_theory_wisp/graph_online.txt"); // Gera arquivo txt para exportar pro graph online
                    System.out.println("Arquivos gerados com sucesso na raiz do projeto!");
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
    }


    // =========================================================================
    //                      MODO NORMAL / APRESENTAÇÃO
    // =========================================================================

    public static void DefaultMenu() {
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
                    System.out.println("Lendo grafo.txt...");
                    //GraphLoader txtLoader = new GraphLoader();
                    //txtLoader.loadFromTxt(tgraph, "sources/graph_theory_wisp/grafo.txt");
                    break;

                case 2:
                    tgraph.exportToTxtFormat("sources/graph_theory_wisp/grafo.txt");
                    System.out.println("Grafo atual salvo em grafo.txt.");
                    break;

                case 3:
                    System.out.println("Qual o tipo do vértice?");
                    System.out.println("| 1. Usuário | 2. Categoria | 3. Atividade | -> (Genéricos, sem detalhes)");
                    
                    System.out.print("Digite a opção: ");
                    int tipoNode = lerInteiro();

                    // ------------ TODO - TEMPORÁRIO, APAGAR/MELHORAR DPS ------------
                    if (tipoNode == 1) {
                        RecommendationTesting testModule = new RecommendationTesting();
                        testModule.runTest(sc);
                    }
                    // ----------------------------------------------------------------

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