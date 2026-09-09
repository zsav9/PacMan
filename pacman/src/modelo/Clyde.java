package modelo;
import java.awt.Color;
// fantasma  persegue pacman 25% do tempo
public class Clyde extends Fantasma {
    private static final long serialVersionUID = 1L; // id de versão para serialização que garante compatibilidade ao carregar o binário
    public Clyde(Color cor, int linha, int coluna) { super("Clyde", cor, linha, coluna); }
    @Override protected double getterChancedePerseguir() { return 0.25; } // 25%
}
