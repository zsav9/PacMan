package modelo;
// exceção lançada quando um arquivo csv está vazio ou com dados inválidos ( vazio, linha faltando)
public class ArquivoInvalidoException extends Exception {
    public ArquivoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
