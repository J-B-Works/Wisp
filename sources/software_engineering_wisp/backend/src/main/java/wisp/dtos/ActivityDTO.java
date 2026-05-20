package wisp.dtos;

public class ActivityDTO {
    public String id;
    public String titulo;
    public String local;
    public String data;
    public String valor;
    public String imagem;
    public String descricao;
    public String linkExterno;
    private boolean isFavorite;

    public ActivityDTO(String id, String titulo, String local, String data, String valor, String imagem, String descricao, String linkExterno, boolean isFavorite) {
        this.id = id;
        this.titulo = titulo;
        this.local = local;
        this.data = data;
        this.valor = valor;
        this.imagem = imagem;
        this.descricao = descricao;
        this.linkExterno = linkExterno;
        this.isFavorite = isFavorite;
    }

    public boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(boolean favorite) { isFavorite = favorite; }
}