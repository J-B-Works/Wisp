/*

=======================================================
  WISP– Sistema de recomendação de educação e cultura
=======================================================

-- Grupo --
Teoria dos Grafos - Turma: 6G
- Bruna Gonçalves Corte David (RA: 10425696)
- Júlia Andrade (RA: 1042513)

-- Síntese do Conteúdo --

-> Interface que representa um nó/vértice do grafo
-> Para construir um grafo heterogêneo em Java, é necessário "embrulhar" as classes diferentes
para ser tratadas como um mesmo "nó de grafo" nessa interface

-- Histórico de Alterações --

26/03/2026 - Bruna - Cria interface GraphNode.java para representar os diferentes tipos de nós do grafo
como um único nó generalizado

*/

package graph_theory_wisp;

interface GraphNode {
    String getId();    // Todo nó do grafo é obrigado a ter um método interno que retorna seu ID único
    String getName();  // Todo nó do grafo é obrigado a ter um método interno que retorna seu nome
}