package modelo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
// classe base abstrata para todos os fantasmas, define comportamento comum de movimento e estado
public abstract class Fantasma extends Personagem {
private static final long serialVersionUID = 1L;
public enum Estado { NORMAL, ASSUSTADO, MORTO }

private String nome;
private Color cor_normal;
private Estado estado;
private int linha_inicial, coluna_inicial; // posição de spawn para resetar
private int ticksAssustado;
private int linhaAnterior, colunaAnterior;
protected static final Random random = new Random(); // compartilhado entre subclasses
public static final int DURACAO_ASSUSTADO = 150; // ticks

// inicializa o fantasma com nome, cor e posição, começa no estado normal
public Fantasma(String nome, Color cor, int linha, int coluna) {
    super(linha, coluna);
    this.nome = nome;
    this.cor_normal = cor;
    this.estado = Estado.NORMAL;
    this.linha_inicial = linha;
    this.coluna_inicial = coluna;
}

public Estado getEstado() { return estado; }

// retorna a cor atual baseada no estado: azul se assustado, cinza se morto, cor normal caso contrário
public Color getCorAtual() {
    switch (estado) {
        case ASSUSTADO:
            return ticksAssustado < 60 && (ticksAssustado / 10) % 2 == 0 // quando faltam menos de 60 ticks, divide o tempo em blocos de 10 e verifica se o bloco é par
                ? Color.WHITE : new Color(0, 0, 200); // pisca antes de acabar
        case MORTO: return new Color(100, 100, 100);
        default: return cor_normal;
    }
}

// muda estado para assustado e reinicia o contador, ignorado se já estiver morto
public void assustar() {
    if (estado != Estado.MORTO) {
        estado = Estado.ASSUSTADO;
        ticksAssustado = DURACAO_ASSUSTADO;
    }
}
    // pacman comeu o fantasma e vira morto
public void matar() {
    estado = Estado.MORTO;
    ticksAssustado = 0;
}
    // reseta posição e estado para o início, chamado após pacman perder vida
public void resetar() {
    estado = Estado.NORMAL;
    setPosicao(linha_inicial, coluna_inicial);
    setDirecao(Direcao.PARADO);
}
    // decrementa o contador do assustado a cada tick, volta ao normal quando zera
public void tickUpdate() {
    if (estado == Estado.ASSUSTADO) {
        ticksAssustado--;
        if (ticksAssustado <= 0) estado = Estado.NORMAL;
    }
}

@Override
public void mover(Mapa mapa) {
    // salva posição anterior para detectar cruzamento com pacman
    this.linhaAnterior = this.linha;
    this.colunaAnterior = this.coluna;

    List<Direcao> possiveis = DirecoesDisponiveis(mapa);
    if (possiveis.isEmpty()) return; //se não tem nenhuma direção disponível o fantasma não se move naquele tick

    Direcao escolhida;
    if (estado == Estado.MORTO) {
        // vai direto pra base
        int[] base = mapa.encontrarBase();
        escolhida = direcaoParaAlvo(possiveis, base[0], base[1], true);
    } else {
        escolhida = escolherDirecao(possiveis, mapa);
    }
    setDirecao(escolhida);
    int[] delta = PacMan.deltaDirecao(escolhida);
    setPosicao(linha + delta[0], coluna + delta[1]); //define a direção escolhida, calcula o delta e atualiza a posição

    // se morto e chegou na base, renasce normal
    if (estado == Estado.MORTO) {
        int[] base = mapa.encontrarBase();
        if (linha == base[0] && coluna == base[1]) {
            estado = Estado.NORMAL;
        }
    }
}

    // retorna direções disponíveis excluindo a oposta, evita o fantasma voltar 180 graus
protected List<Direcao> DirecoesDisponiveis(Mapa mapa) {
    List<Direcao> lista = new ArrayList<>();
    Direcao oposta = oposta(direcao);
    for (Direcao d : Direcao.values()) {
        if (d == Direcao.PARADO || d == oposta) continue; // ignora parado e oposta
        int[] delta = PacMan.deltaDirecao(d);
        int nl = linha + delta[0], nc = coluna + delta[1]; // célula vizinha nessa direção
        if (mapa.ehTransitavelFantasma(nl, nc)) lista.add(d); // só adiciona se não for parede
    }
    if (lista.isEmpty()) {
        // Se não tem outra opção, pode voltar
        if (oposta != Direcao.PARADO) {
            int[] delta = PacMan.deltaDirecao(oposta);
            if (mapa.ehTransitavelFantasma(linha + delta[0], coluna + delta[1]))
                lista.add(oposta);
        }
    }
    return lista;
}

// Cada subclasse define sua chance de perseguir
protected abstract double getterChancedePerseguir();

protected Direcao escolherDirecao(List<Direcao> possiveis, Mapa mapa) {
    // se pacman não existe no mapa, move aleatório
    if (mapa.getpacman() == null) return possiveis.get(random.nextInt(possiveis.size()));

    int pacLinha = mapa.getpacmanLinha();
    int pacColuna = mapa.getpacmanColuna();
    // em fantasmas assustados a chance é invertida, foge em vez de perseguir
    double chance = estado == Estado.ASSUSTADO
        ? 1.0 - getterChancedePerseguir()
        : getterChancedePerseguir();
    // sorteia se vai perseguir/fugir ou mover aleatório
    if (random.nextDouble() < chance) {
        return direcaoParaAlvo(possiveis, pacLinha, pacColuna, estado != Estado.ASSUSTADO);
    }
    return possiveis.get(random.nextInt(possiveis.size()));
}

// retorna a direção que minimiza (aproximar=true) ou maximiza (aproximar=false) a distância ao alvo
protected Direcao direcaoParaAlvo(List<Direcao> possiveis, int targetL, int targetC, boolean aproximar) {
    Direcao melhor = possiveis.get(0);
    int melhorDist = Integer.MAX_VALUE; // começa com distância máxima
    for (Direcao d : possiveis) {
        int[] delta = PacMan.deltaDirecao(d);
        int nl = linha + delta[0], nc = coluna + delta[1]; // célula candidata
        int dist = (int)(Math.pow(nl - targetL, 2) + Math.pow(nc - targetC, 2)); // distância euclidiana (Δlinha² + Δcoluna²)
        if (aproximar ? dist < melhorDist : dist > melhorDist) { // escolhe menor ou maior distância
            melhorDist = dist;
            melhor = d;
        }
    }
    return melhor;
}
    // retorna a direção oposta
private Direcao oposta(Direcao d) {
    switch (d) {
        case CIMA: return Direcao.BAIXO;
        case BAIXO: return Direcao.CIMA;
        case ESQUERDA: return Direcao.DIREITA;
        case DIREITA: return Direcao.ESQUERDA;
        default: return Direcao.PARADO;
    }
}
    // getters da posição anterior, usados no controlador para detectar cruzamento com pacman
public int getLinhaAnterior() { return linhaAnterior; }
public int getColunaAnterior() { return colunaAnterior; }

@Override
public String getNome() { return nome; } //implementação do metodo abstrato de personagem
}
