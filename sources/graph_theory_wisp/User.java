/*

=======================================================
  WISP– Sistema de recomendação de educação e cultura
=======================================================

-- Grupo --
Teoria dos Grafos - Turma: 6G
- Bruna Gonçalves Corte David (RA: 10425696)
- Júlia Andrade (RA: 1042513)

-- Síntese do Conteúdo --

-> Classe que representa um nó do tipo Usuário no grafo

-- Histórico de Alterações --

26/03/2026 - Bruna - Cria classe User.java para representar o nó do tipo "Usuário"

*/

//package graph_theory_wisp;

import java.util.*;

class User implements GraphNode {
    private String id;
    private String name;

    public User(String name) {
        this.id = UUID.randomUUID().toString(); // Gera um ID único aleatório (built-in do Java)
        this.name = name;
    }

    @Override
    public String getId() { return id; }
    @Override
    public String getName() { return name; }
}