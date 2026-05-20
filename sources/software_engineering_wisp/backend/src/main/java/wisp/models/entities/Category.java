package wisp.models.entities;
import java.util.*;

import wisp.models.entities.graph_structure.GraphNode;

public class Category implements GraphNode { // TODO confirmar esse public dps
    private String id;
    private String name;

    public Category(String name) {
        this.id = UUID.randomUUID().toString(); // Gera um ID único aleatório (built-in do Java)
        this.name = name;
    }

    @Override
    public String getId() { return id; }
    @Override
    public String getName() { return name; }
}