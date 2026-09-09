package modelo;
import java.awt.Color;

public class Pinky extends Fantasma {
    private static final long serialVersionUID = 1L; // id de versão para serialização que garante compatibilidade ao carregar o binário
    public Pinky(Color cor, int linha, int coluna) { super("Pinky", cor, linha, coluna); }
    @Override protected double getterChancedePerseguir() { return 0.75; } // 75%
}
