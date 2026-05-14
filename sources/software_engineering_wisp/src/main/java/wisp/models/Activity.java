package wisp.models;
import java.util.*;

public class Activity implements GraphNode {
    private String id;
    private String name;
    private String externalLink;
    private Establishment establishment;
    private String date;
    private String value;
    private String image;
    private String description;
    private int clickCount;

    public Activity(String name, String externalLink, Establishment establishment, String date, String value, String image, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.externalLink = externalLink;
        this.establishment = establishment;
        this.date = date;
        this.value = value;
        this.image = image;
        this.description = description;
        this.clickCount = 0;
    }

    @Override
    public String getId() { return id; }
    @Override
    public String getName() { return name; }
    public String getExternalLink() { return externalLink; }
    public double getLat() { return establishment.getLatitude(); }
    public double getLon() { return establishment.getLongitude(); }
    public String getDate() { return date; }
    public String getValue() { return value; }
    public String getImage() { return image; }
    public String getDescription() { return description; }
    public void registerClick() { this.clickCount++; }
    public int getClickCount() { return clickCount; }
    public Establishment getEstablishment() { return establishment; }
}