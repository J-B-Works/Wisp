package graph_theory_wisp;

public class Main {
    public static void main(String[] args) {
        // =========================================================
        //         CRIAÇÃO DO GRAFO E CARREGAMENTO DE DADOS
        // =========================================================
        TGrafo tgraph = new TGrafo(0);    // Começa com 0 vértices e vai crescendo dinamicamente
        


        // ------ TESTES ------
        System.out.println("\nTESTANDO CARREGAMENTO DE DADOS\n\n");

        // Carrega dados
        System.out.println("Carregando dados...");
        DataLoader loader = new DataLoader();
        loader.loadAll(tgraph);

        // Teste de Sucesso p/ entrega do projeto parte 1
        if (tgraph.getN() >= 70 && tgraph.getM() >= 180) {
            System.out.println("\nSUCESSO: O grafo atingiu os requisitos mínimos de vértices e arestas!");
        } else {
            System.out.println("\nO grafo ainda não atingiu os requisitos mínimos D:");
        }
        System.out.println("\n\n");

        tgraph.show();


        // Teste de exportar grafo pra txt
        System.out.println("\n\nEXPORTAR GRAFO PRA FORMATO TXT");
        tgraph.exportToTxtFormat("text_format.txt");

        System.out.println("\n\nEXPORTAR GRAFO PRA GRAPH ONLINE");
        tgraph.exportToGraphOnline("graph_online.txt");

        System.out.println("\n\n");
    }
}