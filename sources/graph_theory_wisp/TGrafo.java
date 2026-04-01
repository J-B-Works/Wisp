package graph_theory_wisp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


// =================================================================== //
// 	   MUDANÇAS FEITAS PELO NOSSO GRUPO NESSA CLASSE SERÁ MARACADA     //
//   	COM A TAG         // === MUDANÇA ===                           //
// =================================================================== //


//definicao da classe de nós da lista
class TNo{ // define uma struct (registro)
	public	int w;  // vértice que é adjacente ao elemento da lista
    public Integer peso = null; // === MUDANÇA === // Guarda peso da aresta, se ela tiver
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

    // === MUDANÇA ===
    // Insere uma aresta sem peso
    public void insereA(GraphNode v, GraphNode w) {
        insereA(v, w, null);
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
    public void insereA(GraphNode v, GraphNode w, Integer pesoDaAresta) {
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
        novoNoW.peso = pesoDaAresta;
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
        novoNoV.peso = pesoDaAresta;
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
    === MUDANÇA ===

    Método que remove um vértice do grafo e todas as suas arestas adjacentes.
    Como a estrutura utiliza arrays de tamanho estático,
    precisamos realocá-las com tamanho n - 1 e ajustar todos os índices
    dos vértices seguintes para "voltar" uma posição.
    */
    public void removeV(GraphNode node) {
        Integer indexToRemove = nodeIdToIndex.get(node.getId()); // Node -> Índice do nó a ser removido

        // Verificação de segurança
        if (indexToRemove == null) {
            throw new RuntimeException("Erro: Tentativa de remover aresta entre nós que não existem.");
        }

        // Remove todas as arestas adjacentes a este vértice (ida e volta)
        // Percorremos os vizinhos do nó a ser removido e apagamos a aresta de volta
        TNo atual = adj[indexToRemove];
        while (atual != null) {                                  // Enquanto não terminar de percorrer a lista ligada
            int indexVizinho = atual.w;                          // Pega o índice do vizinho atual
            
            // Percorre a lista do vizinho para remover o "indexToRemove"
            TNo noVizinho = adj[indexVizinho];                   // Pega o início da lista do vizinho
            TNo antVizinho = null;
            while (noVizinho != null && noVizinho.w != indexToRemove) { // Enquanto não encontrar o índice do nó a ser removido,
                antVizinho = noVizinho;                          // Percorre a lista do vizinho passando o nó antigo da lista para ser o atual
                noVizinho = noVizinho.prox;                      // e o nó atual da lista para ser o próximo da lista do vizinho
            }
            
            // Se encontrou a aresta no vizinho, remove ela e atualiza "m"
            if (noVizinho != null) {
                if (antVizinho == null) {                        // Se a aresta a ser removida é a primeira da lista do vizinho
                    adj[indexVizinho] = noVizinho.prox;          // Ajusta o início da lista do vizinho para a próxima
                } else {
                    antVizinho.prox = noVizinho.prox;            // Ajusta o ponteiro do anterior para pular o nó da aresta removida
                }
                m--; // Decrementa a quantidade de arestas do grafo
            }
            atual = atual.prox;                                  // Move para o próximo vizinho do nó a ser removido
        }

        // Realocação dos Arrays reduzindo o tamanho
        int novoN = this.n - 1;                                  // Novo número de vértices é o atual - 1
        TNo novaAdj[] = new TNo[novoN];                          // Nova lista de adjacências com tamanho reduzido
        GraphNode novoIndexToNode[] = new GraphNode[novoN];      // Novo "tradutor" índice -> objeto

        // Percorre todos os vértices atuais
        int novoIndice = 0;
        for (int i = 0; i < this.n; i++) {
            if (i == indexToRemove) {
                continue;                                        // Pula o vértice a ser removido
            }

            novoIndexToNode[novoIndice] = this.indexToNode[i];   // Copia o nó para o novo array de nós (na nova posição)
            
            nodeIdToIndex.put(this.indexToNode[i].getId(), novoIndice); // Atualiza o dicionário de Node ID -> Índice com a nova posição

            // Atualiza os índices de destino nas listas de adjacência
            // Se algum vizinho tinha índice MAIOR que o removido, o índice dele cai em 1
            TNo no = this.adj[i];                                // Pega o início da lista de adjacência do vértice atual
            TNo novaListaHead = null;                            // Cabeça da nova lista de adjacência para o vértice atual (com índices corrigidos)
            TNo novaListaTail = null;                            // Cauda da nova lista para facilitar inserção no final

            while (no != null) {
                TNo novoNoAdj = new TNo();
                
                // Subtrair 1 do índice se ele estava à frente do removido
                if (no.w > indexToRemove) {                      // Se estava depois do removido,
                    novoNoAdj.w = no.w - 1;                      // Atualiza para o novo índice - 1
                } else {
                    novoNoAdj.w = no.w;                          // Não muda índice
                }
                
                // Insere no final da nova lista
                novoNoAdj.prox = null;
                if (novaListaHead == null) {                     // Se a nova lista ainda estiver vazia,
                    novaListaHead = novoNoAdj;                   // Esse mesmo nó será o primeiro (head)
                    novaListaTail = novoNoAdj;                   // e o último (tail)
                } else {
                    novaListaTail.prox = novoNoAdj;              // O antigo último agora aponta para o novo nó
                    novaListaTail = novoNoAdj;                   // O novo nó assume o posto de último da fila (Tail)
                }

                no = no.prox;                                    // Move para o próximo vizinho da lista antiga
            }
            
            // Atribui a lista corrigida à nova posição
            novaAdj[novoIndice] = novaListaHead;
            novoIndice++;                                        // Incrementa o índice para o próximo vértice (que não é o removido)
        }

        // Remove do tradutor (mapa) Node ID -> índice, o Node ID do nó deletado
        nodeIdToIndex.remove(node.getId());

        // Atualiza
        this.adj = novaAdj;                                      // Atualiza lista de adjacências
        this.indexToNode = novoIndexToNode;                      // Atualiza "tradutor" índice -> objeto
        this.n = novoN;
    }

	/*
    // === MUDANÇA ===

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
                if (no.peso != null) {
                    System.out.print(no.w + "(" + no.peso + ") ");
                } else {
                    System.out.print(no.w + " ");
                }
	            no = no.prox;
	        }
			System.out.print("null");
	    }
	}





    // =================================================================== //
    //            MÉTODOS EXTRAS EXCLUSIVOS DO NOSSO PROJETO               //
    //         (Não fazem parte da implementação do grafo em si)           //
    // =================================================================== //

    // --- Getters ---
    public int getN() { return n; }
    public int getM() { return m; }
    public TNo getAdj(int v) {
        return adj[v];
    }
    public GraphNode getNodeByIndex(int index) {
        return indexToNode[index];
    }
    public Integer getIndexByNodeId(String id) {
        return nodeIdToIndex.get(id);
    }

    // --- Método extra p/ converter o grafo numa edge list p/ graph online ---
    // a-b representa uma aresta/edge bidirecional no graph online
    // https://graphonline.top/create_graph_by_edge_list
    public void exportToGraphOnline(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (int i = 0; i < n; i++) {
                TNo no = adj[i];
                while (no != null) {
                    // Como o grafo é bidirecional, só escrevemos se i < no.w
                    // Isso evita escrever "0 87" e "87 0" como duas arestas separadas
                    if (i < no.w) {
                        if (no.peso != null) {
                            writer.println(i + "-(" + no.peso + ")-" + no.w);
                        } else {
                            writer.println(i + "-" + no.w);
                        }
                    }
                    no = no.prox;
                }
            }
            System.out.println("Arquivo " + fileName + " gerado com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao gerar arquivo: " + e.getMessage());
        }
    }

    // --- Exportador para formato simples (txt) ---
    public void exportToTxtFormat(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            // Tipo do Grafo
            writer.println("2"); // 2 – grafo não orientado com peso na aresta

            // Quantidade de vértices
            writer.println(n);

            // Lista de Vértices: ID  “Identificação do Rótulo” “peso do vértice”
            for (int i = 0; i < n; i++) {
                GraphNode node = indexToNode[i];
                
                // Define o rótulo com base no tipo do objeto
                String label = "";
                if (node instanceof Category) {
                    label = "Category: " + node.getName();
                } else if (node instanceof Activity) {
                    label = "Activity: " + node.getName();
                } else if (node instanceof User) {
                    label = "User: " + node.getName();
                }

                // Formato: i "Rótulo"
                writer.println(i + " \"" + label + "\"");
            }

            // Quantidade de arestas
            writer.println(m);

            // Lista de Arestas: Aresta  Peso_aresta
            for (int i = 0; i < n; i++) {
                TNo no = adj[i];
                while (no != null) {
                    // Como o grafo é bidirecional, só escrevemos se i < no.w
                    // Isso evita escrever "0 87" e "87 0" como duas arestas separadas
                    if (i < no.w) {
                        // Atividade-Categoria e Atividade-Usuário não há peso, porém Usuário-Categoria há peso
                        writer.println(i + " " + no.w + (no.peso != null ? " " + no.peso : ""));
                    }
                    no = no.prox;
                }
            }
            System.out.println("Grafo simples em txt " + fileName + " gerado com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao gerar arquivo: " + e.getMessage());
        }
    }

   // --- Método extra p/ Verificar se o grafo é Conexo usando BFS e gerar sua versão Reduzida ---
    public void printConnectivityAndReducedGraph() {
        System.out.println("\n======== ANÁLISE DE CONEXIDADE E GRAFO REDUZIDO ========");
    
        // Contando componentes conexos e imprimindo os vértices que compõe cada componente
        boolean[] visited = new boolean[n];
        int numComponentes = 0;
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {                                   // Se esse vértice já não tiver sido visitado,
                numComponentes++;                                // Conta +1 componente conexo encontrado
                System.out.print("Componente " + numComponentes + " (Vértices): { ");
                
                //  ------------ BFS ------------
                // Mapeando todos os vértices da componente conexa atual
                java.util.Queue<Integer> queue = new java.util.LinkedList<>(); // Fila p/ BFS
                queue.add(i);                                    // Adiciona o vértice inicial à fila
                visited[i] = true;                               // Marca ele como visitado

                while (!queue.isEmpty()) {                       // Enquanto a fila não estiver vazia,
                    int v = queue.poll();                        // Pega e remove o próximo vértice da fila
                    System.out.print(v + " ");                   // Imprime ele (pois faz parte da componente conexa atual)
                    
                    TNo no = adj[v];                             // Olha para os vizinhos adjacentes do vértice atual
                    while (no != null) {                         // Enquanto não tiver terminado de percorrer a lista ligada,
                        if (!visited[no.w]) {                    // Se o vizinho ainda não tiver sido visitado, 
                            visited[no.w] = true;                // Visita vizinho
                            queue.add(no.w);                     // Adiciona vizinho na fila para ser processado depois
                        }
                        no = no.prox;                            // Continua percorrendo lista ligada
                    }
                }
                System.out.println("}");                       // Finaliza impressão dessa componente conexa
            }
        }

        // ------- RESULTADO DO GRAFO REDUZIDO -------
        System.out.println("\n--- CONCLUSÃO ---");
        if (numComponentes == 1) {
            System.out.println("Conexidade: O Grafo é CONEXO.");
            System.out.println("Grafo Reduzido: É composto por 1 único vértice (que engloba todos os " + n + " vértices atuais) e 0 arestas.");
        } else {
            System.out.println("Conexidade: O Grafo é DESCONEXO.");
            System.out.println("Grafo Reduzido: É composto por " + numComponentes + " vértices isolados e 0 arestas.");
        }
    }
}