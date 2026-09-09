package modelo;

// pacman controlado pelo jogador, extends personagem com vidas, pontos e direção solicitada
public class PacMan extends Personagem {
private static final long serialVersionUID = 1L;
private int linhaAnterior, colunaAnterior;  // posição anterior para detectar cruzamento com fantasmas
private String nome;
private int vidas;
private int linhaInicial, colunaInicial; // posição de spawn para resetar
private Direcao proximaDirecao; // direção que o jogador pediu
// inicializa pacman com nome, vidas e posição inicial
public PacMan(String nome, int vidas, int linha, int coluna) {
    super(linha, coluna);
    this.nome = nome;
    this.vidas = vidas;
    this.linhaInicial = linha;
    this.colunaInicial = coluna;
    this.proximaDirecao = Direcao.PARADO;
}

// getters (encapsulamento)
public int getVidas() { return vidas; }
public void perdervida() { vidas--; }
public boolean esta_vivo() { return vidas > 0;
}


public void setproximaDirecao(Direcao d) { this.proximaDirecao = d; }

    // reposiciona pacman na posição inicial e para o movimento, chamado após perder vida
public void resetar_posicao() {
    setPosicao(linhaInicial, colunaInicial);
    setDirecao(Direcao.PARADO);
    proximaDirecao = Direcao.PARADO;
}

@Override
public void mover(Mapa mapa) {
    // salva posição anterior para detectar cruzamento com fantasmas
    this.linhaAnterior = this.linha;
    this.colunaAnterior = this.coluna;
    // tenta virar na direção pedida pelo jogador se for possível
    if (proximaDirecao != Direcao.PARADO && podeMover(proximaDirecao, mapa)) {
        setDirecao(proximaDirecao);
    }
    // Continua na direção atual
    if (direcao != Direcao.PARADO && podeMover(direcao, mapa)) {
        int[] delta = deltaDirecao(direcao);
        int novaLinha = linha + delta[0];
        int novaColuna = coluna + delta[1];

        // Teletransporte lateral
        if (novaColuna < 0) novaColuna = mapa.getColunas() - 1;
        if (novaColuna >= mapa.getColunas()) novaColuna = 0;

        setPosicao(novaLinha, novaColuna);
    }
}

// verifica se  o pacman pode se mover nessa direção sem bater em parede
private boolean podeMover(Direcao d, Mapa mapa) {
    int[] delta = deltaDirecao(d);
    int novaLinha = linha + delta[0];
    int novaColuna = coluna + delta[1];
    // wrap horizontal para o túnel
    if (novaColuna < 0) novaColuna = mapa.getColunas() - 1;
    if (novaColuna >= mapa.getColunas()) novaColuna = 0;
    return mapa.ehTransitavel(novaLinha, novaColuna);
}

// converte direção em deslocamento de células, usado por pacman e fantasmas
public static int[] deltaDirecao(Direcao d) {
    switch (d) {case CIMA: return new int[]{-1, 0};
        case BAIXO: return new int[]{1, 0};
        case ESQUERDA: return new int[]{0, -1};
        case DIREITA: return new int[]{0, 1};
        default: return new int[]{0, 0};}
}
// getters da posição anterior
public int getLinhaAnterior() { return linhaAnterior; }
public int getColunaAnterior() { return colunaAnterior; }

@Override
public String getNome() { return nome; }
} // implementação do metodo abstrato de personagem