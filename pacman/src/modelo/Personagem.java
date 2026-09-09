package modelo;
import java.io.Serializable;
// superclasse abstrata de todos os personagens
public abstract class Personagem implements Serializable {
private static final long serialVersionUID = 1L;

protected int linha;
protected int coluna;
protected Direcao direcao;

public enum Direcao { CIMA, BAIXO, ESQUERDA, DIREITA, PARADO }
// inicializa o personagem na posição dada, começa parado
public Personagem(int linha, int coluna) {
    this.linha = linha;
    this.coluna = coluna;
    this.direcao = Direcao.PARADO;
}
// getters e setters de posição e direção
public int getLinha() { return linha; }
public int getColuna() { return coluna; }
public Direcao getDirecao() { return direcao; }
public void setDirecao(Direcao d) { this.direcao = d; }
public void setPosicao(int linha, int coluna) { this.linha = linha; this.coluna = coluna; }
// cada subclasse define como se move e qual o seu nome
public abstract void mover(Mapa mapa);
public abstract String getNome();
}
