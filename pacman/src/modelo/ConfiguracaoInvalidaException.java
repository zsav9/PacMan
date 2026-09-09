package modelo;
// exceção lançada quando os dados do jogador são inválidos, como nome vazio ou vidas <= 0
public class ConfiguracaoInvalidaException extends Exception {
    public ConfiguracaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
