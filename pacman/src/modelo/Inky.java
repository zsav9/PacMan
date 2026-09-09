package modelo;
import java.awt.Color;
// fantasma persegue pacman 50% do tempo
public class Inky extends Fantasma {
    private static final long serialVersionUID = 1L; // id de versão para serialização que garante compatibilidade ao carregar o binário
    public Inky(Color cor, int linha, int coluna) { super("Inky", cor, linha, coluna); }
    @Override protected double getterChancedePerseguir() { return 0.5; } // 50%
}
