package wisp.models;

//definicao da classe de nós da lista
public class TNo{ // define uma struct (registro)
	public	int w;  // vértice que é adjacente ao elemento da lista
    public Integer peso = null; // === MUDANÇA === // Guarda peso da aresta, se ela tiver
	public TNo prox;
}