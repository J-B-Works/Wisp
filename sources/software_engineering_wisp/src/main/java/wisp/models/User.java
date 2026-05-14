package wisp.models;
import java.util.*;

public class User implements GraphNode {
    private String id;
    private String name;
    private String email;
    private String cep;
    private double latitude;
    private double longitude;

    public User(String name, String email, String cep) {
        this.id = UUID.randomUUID().toString(); // Gera um ID único aleatório (built-in do Java)
        this.name = name;
        this.email = email;
        this.cep = cep;
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    @Override
    public String getId() { return id; }
    @Override
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getCep() { return cep; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public void setCep(String cep) { this.cep = cep; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}

