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

26/03/2026 - Bruna - Cria classe Activity.java para representar o nó do tipo "Atividade"

*/

//package graph_theory_wisp;

import java.util.*;

class Activity implements GraphNode {
    private String id;
    private String name;
    private String externalLink;
    private Establishment establishment;
    private int clickCount;

    public Activity(String name, String externalLink, Establishment establishment) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.externalLink = externalLink;
        this.establishment = establishment;
        this.clickCount = 0;
    }

    @Override
    public String getId() { return id; }
    @Override
    public String getName() { return name; }
    public double getLat() { return establishment.getLatitude(); }
    public double getLon() { return establishment.getLongitude(); }
    public void registerClick() { this.clickCount++; }
}