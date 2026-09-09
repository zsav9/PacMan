package modelo;
import java.awt.Color;
// fantasma  persegue pacman 100% do tempo
public class Blinky extends Fantasma {
    private static final long serialVersionUID = 1L; // id de versão para serialização que garante compatibilidade ao carregar o binário
    public Blinky(Color cor, int linha, int coluna) { super("Blinky", cor, linha, coluna); }
    @Override protected double getterChancedePerseguir() { return 1.0; } // 100%
}
