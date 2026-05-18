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
17/05/2026 - Júlia - Adiciona atributos de latitude e longitude

*/

package graph_theory_wisp.graph_node_types;

import java.util.*;

public class Activity implements GraphNode {
    private String id;
    private String name;
    private double lat;
    private double lon;
    private int clickCount;

    public Activity(double lat, double lon, String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.clickCount = 0;
    }

    @Override
    public String getId() { return id; }
    @Override
    public String getName() { return name; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
    public void registerClick() { this.clickCount++; }
    public int getClickCount() { return clickCount; }
}