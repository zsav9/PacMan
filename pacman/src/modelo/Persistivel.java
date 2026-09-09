package modelo;
import java.io.IOException;
// interface para objetos que podem ser salvos em arquivo binário
public interface Persistivel {
    void salvar(String caminho) throws IOException;
}
