package wisp.models;
import java.util.*;

public class User implements GraphNode { // TODO remover public depois (foi pro TGrafoTest, mas irei melhorar os testes dele dps)
    private String id;
    private String name;
    private String email;

    public User(String name, String email) {
        this.id = UUID.randomUUID().toString(); // Gera um ID único aleatório (built-in do Java)
        this.name = name;
        this.email = email;
    }

    @Override
    public String getId() { return id; }
    @Override
    public String getName() { return name; }
    public String getEmail() { return email; }
}