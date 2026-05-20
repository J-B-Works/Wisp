package wisp.dtos;

public class InteractionDTO {
    private String userId;
    private String activityId;
    private boolean isFavorite; // true = favoritou, false = apenas clicou

    // Construtores
    public InteractionDTO() {}

    public InteractionDTO(String userId, String activityId, boolean isFavorite) {
        this.userId = userId;
        this.activityId = activityId;
        this.isFavorite = isFavorite;
    }

    // Getters e Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}