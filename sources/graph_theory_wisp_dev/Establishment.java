//package graph_theory_wisp_dev;

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