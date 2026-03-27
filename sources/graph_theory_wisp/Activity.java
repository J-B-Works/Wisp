package graph_theory_wisp;

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