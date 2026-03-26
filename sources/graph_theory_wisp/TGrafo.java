package graph_theory_wisp;

import java.util.HashMap;
import java.util.Map;

/*
===================================================================
	MUDANÇAS FEITAS PELO NOSSO GRUPO NESSA CLASSE SERÁ MARACADA
	COM A TAG         // === MUDANÇA === 
===================================================================
*/

//definicao da classe de nós da lista
class TNo{ // define uma struct (registro)
	public	int w;  // vértice que é adjacente ao elemento da lista
	public TNo prox;
}

//definição de uma classe para armezanar um grafo
public class TGrafo{
	// atributos privados
	private	int n; // quantidade de vértices
	private	int m; // quantidade de arestas
	private	TNo adj[]; // um vetor onde cada entrada guarda o inicio de uma lista

	// === MUDANÇA ===
	private GraphNode[] indexToNode;            // índice -> objeto
    private Map<String, Integer> nodeIdToIndex; // objeto (seu ID) -> índice
	
	// métodos públicos
	// Construtor do grafo com a lista de
	// adjacência
	public TGrafo( int n ) {
	    // aloca a estrutura TGrafo
	    this.n = n;
	    this.m = 0;
	    // aloca m vetor para guardar lista de adjacencias
	    TNo adjac[] = new TNo[n];
	    // Inicia o vetor com nullL
		for(int i = 0; i< n; i++)
			adjac[i]=null;	
	    this.adj = adjac;

		// === MUDANÇA ===
        this.indexToNode = new GraphNode[n];
        this.nodeIdToIndex = new HashMap<>();
	};

	// === MUDANÇA ===
    public void insereV(GraphNode node) {

        // Copia dados antigos para os novos arrays
        int novoN = this.n + 1;
        TNo novaAdj[] = new TNo[novoN];
        GraphNode novoIndexToNode[] = new GraphNode[novoN];
        for (int i = 0; i < this.n; i++) { // Percorre os índices antigos
            novaAdj[i] = this.adj[i];
            novoIndexToNode[i] = this.indexToNode[i];
        }

        // Adiciona o novo nó no final
        int novoIndice = this.n;           // Novo índice é o antigo total de vértices pois agora temos uma a mais
        novaAdj[novoIndice] = null;        // O novo vértice ainda não tem adjacências
        novoIndexToNode[novoIndice] = node;
        
        // Atualiza o tradutor de ID para Índice
        nodeIdToIndex.put(node.getId(), novoIndice);

        // Atualiza referências da classe
        this.adj = novaAdj;
        this.indexToNode = novoIndexToNode;
        this.n = novoN;
    }
	
	/*
	=== MUDANÇA ===

	1. Método que recebe Nodes e retorna os índices correspondente
	a eles no vetor de adjacências.

	2. Método que cria uma aresta v-w no grafo. O método supõe que
	v e w são distintos, positivos e menores que V.
	Se o grafo já tem a aresta v-w, o método não faz nada.
	O método também atualiza a quantidade de arestas no grafo.

	3. Como o grafo é bidirecional, precisamos inserir a aresta
	nas duas direções: v -> w e w -> v.

	*/
    public void insereA(GraphNode v, GraphNode w) {
        Integer indexV = nodeIdToIndex.get(v.getId()); // Node V -> Índice V
        Integer indexW = nodeIdToIndex.get(w.getId()); // Node W -> Índice W

        // Verificação de segurança
        if (indexV == null || indexW == null) {
            throw new RuntimeException("Erro: Tentativa de criar aresta entre nós que não existem.");
        }


        // -------- INSERÇÃO DA IDA: Adicionando indexW na lista de indexV --------
        
        // Anda na lista de adj enquanto não for nulo e o índice de destino for maior ou igual ao atual
        TNo noV = adj[indexV];
        TNo antV = null;
        while (noV != null && indexW >= noV.w) {
            if (indexW == noV.w) {
                return; // A aresta já existe, não faz nada
            }
            antV = noV;
            noV = noV.prox;
        }
        // Cria o novo nó para guardar o índice W
        TNo novoNoW = new TNo();
        novoNoW.w = indexW;
        novoNoW.prox = noV;
        // Atualiza a lista de V
        if (antV == null) {
            adj[indexV] = novoNoW; // Insere no início
        } else {
            antV.prox = novoNoW;   // Insere no meio ou fim
        }


        // -------- INSERÇÃO DA VOLTA: Adicionando indexV na lista de indexW --------
        
        // Anda na lista de adj enquanto não for nulo e o índice de destino for maior ou igual ao atual
        TNo noW = adj[indexW];
        TNo antW = null;
        while (noW != null && indexV >= noW.w) {
            // Não precisamos verificar se aresta já existe aqui pois isso já foi verificado na inserção da ida
            antW = noW;
            noW = noW.prox;
        }
		// Cria o novo nó para guardar o índice V
        TNo novoNoV = new TNo();
        novoNoV.w = indexV;
        novoNoV.prox = noW;
		// Atualiza a lista de W
        if (antW == null) {
            adj[indexW] = novoNoV;
        } else {
            antW.prox = novoNoV;
        }
        
        // -------- Incrementa a contagem de arestas no grafo --------
        m++; 
    }
	
	/*
    === MUDANÇA ===

    1. Método que recebe Nodes e retorna os índices correspondente
	a eles no vetor de adjacências.

    2. Método que remove do grafo a aresta que tem ponta inicial v
	e ponta final w. O método supõe que v e w são distintos,
	positivos e menores que V. Se não existe a aresta v-w,
	o método não faz nada. O método também atualiza a
	quantidade de arestas no grafo.

	3. Como o grafo é bidirecional, precisamos remover a aresta
	nas duas direções: v -> w e w -> v.

    */
    public void removeA(GraphNode v, GraphNode w) {
        Integer indexV = nodeIdToIndex.get(v.getId()); // Node V -> Índice V
        Integer indexW = nodeIdToIndex.get(w.getId()); // Node W -> Índice W

        // Verificação de segurança
        if (indexV == null || indexW == null) {
            throw new RuntimeException("Erro: Tentativa de remover aresta entre nós que não existem.");
        }


        // -------- REMOÇÃO DA IDA: Removendo indexW da lista de indexV --------
        
		// Percorre a lista de adjacência de V procurando pelo índice W
        TNo noV = adj[indexV];
        TNo antV = null;
        while (noV != null && noV.w != indexW) {
            antV = noV;
            noV = noV.prox;
        }
        // Se a aresta foi encontrada, remove da lista de V
        if (noV != null) {
            if (antV == null) {
                adj[indexV] = noV.prox; // O nó era o primeiro
            } else {
                antV.prox = noV.prox;   // O nó estava no meio ou no fim
            }
        }


        // -------- REMOÇÃO DA VOLTA: Removendo indexV da lista de indexW --------

        // Percorre a lista de adjacência de W procurando pelo índice V
        TNo noW = adj[indexW];
        TNo antW = null;
        while (noW != null && noW.w != indexV) {
            antW = noW;
            noW = noW.prox;
        }
        // Se a aresta foi encontrada, ajusta os ponteiros para removê-la
        if (noW != null) {
            if (antW == null) {
                adj[indexW] = noW.prox; // O nó era o primeiro
            } else {
                antW.prox = noW.prox;   // O nó estava no meio ou no fim
            }
        }


        // -------- Decrementa a contagem de arestas no grafo --------
        m--; 
    }
	/*
	Para cada vértice v do grafo, este método imprime, em
	uma linha, todos os vértices adjacentes ao vértice v
	(vizinhos ao vértice v).
	*/
	public void show() {
		
		System.out.println("======== Impressão do Grafo ========\n\n");

		System.out.println("Vértices (n): " + n + "\n");
        System.out.println("Arestas (m): " + m + "\n");

	    for( int i=0; i < n; i++){

			// Pegando o Node correspondente ao índice i
			GraphNode node = indexToNode[i];
			
			// Pegando informações do Node
			String tipoNode = node.getClass().getSimpleName();
			String idNode = node.getId();

	    	System.out.print("\n" + i + ": " + tipoNode + " (" + idNode + ")" + " -> ");

	        // Percorre a lista na posição i do vetor
	        TNo no = adj[i];
	        while( no != null ){
	        	System.out.print(no.w + " ");
	            no = no.prox;
	        }
			System.out.print("null");
	    }
	    System.out.print("\n\nfim da impressao do grafo.\n");
	}
}