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
17/05/2026 - Júlia - Corrige classe para eliminar atributos não utilizados e incluir latitude e longitude

*/

package graph_theory_wisp.graph_node_types;

import java.util.*;

public class User implements GraphNode {
    private String id;
    private String name;
    private double lat;
    private double lon;

    public User(String name, double lat, double lon) {
        this.id = UUID.randomUUID().toString(); // Gera um ID único aleatório (built-in do Java)
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public String getId() { return id; }
    @Override
    public String getName() { return name; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
}