import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class TGrafoTest {

    @Test
    public void testInicializacaoComZero() {
        // Testa se o grafo começa vazio como ordenado
        TGrafo g = new TGrafo(0);
        assertEquals(0, g.getN(), "O grafo deveria começar com 0 vértices");
    }

    @Test
    public void testInsercaoUnicaAumentaN() {
        TGrafo g = new TGrafo(0);
        int inicial = g.getN();

        // Criando um nó de usuário (que implementa GraphNode)
        User u = new User("Candidato Mackenzie");
        
        // Chamando o seu método real de inserção
        g.insereV(u);

        assertEquals(inicial + 1, g.getN(), "O n deve aumentar exatamente +1 após insereV");
    }

    @Test
    public void testMultiplasInsercoes() {
        TGrafo g = new TGrafo(0);
        
        g.insereV(new User("User 1"));
        g.insereV(new User("User 2"));
        g.insereV(new User("User 3"));

        assertEquals(3, g.getN(), "O n deve ser 3 após inserir 3 usuários");
    }
}
