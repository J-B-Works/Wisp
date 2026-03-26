package graph_theory_wisp;

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
    public String getName() { return name; }
}