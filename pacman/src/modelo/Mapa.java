package modelo;
import java.io.Serializable;
public class Mapa implements Serializable {
// representa o labirinto do jogo como uma grade de inteiros
private static final long serialVersionUID = 1L; // id de versão para serialização
// cada célula da grade é um desses valores definidos no csv como números
public static final int celulaVazia = 0;
public static final int parede = 1;
public static final int ponto = 2; // (+10 pts)
public static final int powerup = 3; // (+50 pts)
public static final int InicioPacman = 4; //posição inicial do pacman no mapa
public static final int baseFantasmas = 5; //base dos fantasmas, fantasmas sao posicionados na 1a célula da base encontrada +1 offset dos outros
private int[][] grade; // matriz com o layout
private int linhas, colunas; // dimensão

// Referência ao PacMan pra "ia" dos fantasmas
private transient PacMan pacMan;
// inicializa o mapa com a grade lida do csv
public Mapa(int[][] grade) {
    this.grade = grade;
    this.linhas = grade.length;
    this.colunas = grade[0].length;
}
// retorna o tipo da célula, se for fora dos limites trata como parede
public int getTile(int l, int c) {
    if (l < 0 || l >= linhas || c < 0 || c >= colunas) return parede;
    return grade[l][c];
}
// atualiza o valor de uma célula por ex: remove ponto quando pacman passa
public void setTile(int l, int c, int val) {
    if (l >= 0 && l < linhas && c >= 0 && c < colunas) grade[l][c] = val;
}

// getters
public int getLinhas() { return linhas; }
public int getColunas() { return colunas; }

// retorna true se pacman pode andar nessa célula, qualquer coisa que não seja parede
public boolean ehTransitavel(int l, int c) {
    int t = getTile(l, c);
    return t != parede;
}
// mesmo do de cima, mas com fantasma
public boolean ehTransitavelFantasma(int l, int c) {
    int t = getTile(l, c);
    return t != parede;}

// percorre a grade e retorna a posição marcada com inicioPacman, o fallback é 10, 9 se não encontrar
public int[] EncontrarInicio() {
    for (int i = 0; i < linhas; i++)
        for (int j = 0; j < colunas; j++)
            if (grade[i][j] == InicioPacman) return new int[]{i, j};
    return new int[]{10, 9};
}
// percorre a grade e retorna a primeira célula da base dos fantasmas, fallback (8,7) se não encontrar
public int[] encontrarBase() {
    for (int i = 0; i < linhas; i++)
        for (int j = 0; j < colunas; j++)
            if (grade[i][j] == baseFantasmas) return new int[]{i, j};
    return new int[]{8, 7};
}
// referência o pacman no mapa, usada pela "ia" dos fantasmas para saber onde ele está
public void setPacMan(PacMan p) { this.pacMan = p;}
public PacMan getpacman() { return pacMan; }
public int getpacmanLinha() { return pacMan != null ? pacMan.getLinha() : 0;} // retorna 0 se pacman não existir
public int getpacmanColuna() { return pacMan != null ? pacMan.getColuna() : 0;} // retorna 0 se pacman não existir
}
