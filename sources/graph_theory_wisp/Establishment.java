/*

=======================================================
  WISP– Sistema de recomendação de educação e cultura
=======================================================

-- Grupo --
Teoria dos Grafos - Turma: 6G
- Bruna Gonçalves Corte David (RA: 10425696)
- Júlia Andrade (RA: 1042513)

-- Síntese do Conteúdo --

-> Classe que representa o atributo Estabelecimento das atividades (cada uma possui o atributo estabelecimento)
-> Possui nome, localização (em latitude e longitutude) do estabelecimento em que ocorre a atividade
-> Por enquanto, esse atributo não utilidade nesta entrega e foi feito para uso futuro

-- Histórico de Alterações --

26/03/2026 - Bruna - Cria classe Establishment.java para representar o atributo "Estabelecimento" das atividades

*/

package graph_theory_wisp;

import java.util.*;

class Establishment {
    private String id;
    private String name;
    private double latitude;
    private double longitude;

    public Establishment(String name, double latitude, double longitude) {
        this.id = UUID.randomUUID().toString(); // Gera um ID único aleatório (built-in do Java)
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}