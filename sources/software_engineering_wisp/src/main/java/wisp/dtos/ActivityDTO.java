package wisp.dtos;

public class ActivityDTO {
    public String id;
    public String titulo;
    public String local;
    public String data;
    public String valor;
    public String imagem;

    public ActivityDTO(String id, String titulo, String local, String data, String valor, String imagem) {
        this.id = id;
        this.titulo = titulo;
        this.local = local;
        this.data = data;
        this.valor = valor;
        this.imagem = imagem;
    }
}