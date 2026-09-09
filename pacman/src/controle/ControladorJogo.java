package controle;
import modelo.*;
import salvos.EscritorLog;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ControladorJogo {public enum PartidaStatus { JOGANDO, MORREU, VENCEU, GAME_OVER }

private EstadoJogo estado; // estado carregado pelo p1 com jogador, fantasmas e mapas
private EscritorLog log;
private PacMan pacman;   // instância do pacman controlada pelo jogador
private List<Fantasma> fantasmas; // lista de fantasmas ativos no mapa
private Mapa mapaatual;
private int mapaIndex; // índice do mapa selecionado (0, 1 ou 2)
private PartidaStatus partida_status;
private int pontuacao_total;
private int fantasmas_assustados;

// throws IOException pois o EscritorLog pode falhar ao criar o arquivo de log
public ControladorJogo(EstadoJogo estado, int mapaIndex, String caminhoLog) throws IOException {
    this.estado = estado;
    this.mapaIndex = mapaIndex;
    this.log = new EscritorLog(caminhoLog);
    this.pontuacao_total = 0;
    inicializarJogo();}
// configura mapa, pacman e fantasmas para o início da partida
private void inicializarJogo() {
    mapaatual = copiarMapa(estado.getMapa(mapaIndex)); // cria uma cópia do mapa original para não modificar o estado salvo
    int[] inicio = mapaatual.EncontrarInicio();
    pacman = new PacMan(estado.getNomeJogador(),
            estado.getVidasIniciais(),
            inicio[0], inicio[1]);
    mapaatual.setTile(inicio[0], inicio[1], Mapa.celulaVazia); // remove o marcador de início do mapa pro pacman conseguir mexer
    mapaatual.setPacMan(pacman);
    fantasmas = criarFantasmas();
    partida_status = PartidaStatus.JOGANDO;
    fantasmas_assustados = 0;}


// cria uma cópia independente do mapa original, evitando modificar o estado salvo pelo p1
private Mapa copiarMapa(Mapa original) {
    int[][] grade = new int[original.getLinhas()][original.getColunas()];
    for (int i = 0; i < original.getLinhas(); i++)
        for (int j = 0; j < original.getColunas(); j++)
            grade[i][j] = original.getTile(i, j);
    return new Mapa(grade);
}
// cria os fantasmas a partir dos dados carregados pelo P1
private List<Fantasma> criarFantasmas() {
    List<Fantasma> lista = new ArrayList<>();
    int[] base = mapaatual.encontrarBase();
    int offset = 0;
    for (EstadoJogo.DadosFantasma d : estado.getDadosFantasmas()) {
        Color cor = new Color(d.getCor()); // converte cor de int RGB para color
        int l = base[0], c = base[1] + offset; // posiciona cada fantasma numa célula diferente da base
        Fantasma f = FazerFantasma(d.getTipo(), cor, l, c);
        lista.add(f);
        offset++;
    }return lista;}

// cria fantasma pelo tipo: polimorfismo!!
private Fantasma FazerFantasma(TipoFantasma tipo, Color cor, int l, int c) {
    switch (tipo) {
        case blinky: return new Blinky(cor, l, c);
        case pinky: return new Pinky(cor, l, c);
        case inky: return new Inky(cor, l, c);
        case clyde: return new Clyde(cor, l, c);
        default: return new Blinky(cor, l, c);
    }
}

public void update() {
if (partida_status != PartidaStatus.JOGANDO) return;
// mover pacman, polimorfismo
pacman.mover(mapaatual);
// come pontos ou power-up
int tile = mapaatual.getTile(pacman.getLinha(), pacman.getColuna());
if (tile == Mapa.ponto) { // se pacman está em cima de um ponto, remove do mapa e soma 10 pts
    mapaatual.setTile(pacman.getLinha(), pacman.getColuna(), Mapa.celulaVazia);
    pontuacao_total += 10;
} else if (tile == Mapa.powerup) { // se é um power-up, remove do mapa, soma 50 pts e "assusta" todos os fantasmas
    mapaatual.setTile(pacman.getLinha(), pacman.getColuna(), Mapa.celulaVazia);
    pontuacao_total += 50;
    for (Fantasma f : fantasmas) f.assustar(); // polimorfismo! chama assustar() polimorficamente em cada subtipo de fantasma
    fantasmas_assustados = 0; // reseta o contador de fantasmas assustados nessa rodada de power-up
    try { log.registrar("POWERUP", "PacMan comeu um power-up!"); } catch (IOException ignored) {} // loga o evento no csv
}
for (Fantasma f : fantasmas) f.tickUpdate();
// Move fantasmas: chamada polimórfica via mover()
for (Fantasma f : fantasmas) {
    f.mover(mapaatual);
    verificarColisao(f);}

// verifica vitória
if (pontosRestantes() == 0) {
    partida_status = PartidaStatus.VENCEU;
    try { log.registrar("VITORIA", "Pontos: " + pontuacao_total); } catch (IOException ignored) {}
}}
// verifica se pacman e fantasma colidiram por célula comum ou cruzamento no mesmo tick
private void verificarColisao(Fantasma f) {
    boolean mesmacelula = f.getLinha() == pacman.getLinha() &&
            f.getColuna() == pacman.getColuna(); // colisão direta: estão na mesma célula

    boolean cruzou = f.getLinhaAnterior() == pacman.getLinha() &&
            f.getColunaAnterior() == pacman.getColuna() &&
            pacman.getLinhaAnterior() == f.getLinha() &&
            pacman.getColunaAnterior() == f.getColuna(); // cruzamento: trocaram de célula ao mesmo tempo
    if (mesmacelula || cruzou) {
        // se cruzou um fantasma assustado, pacman come ele e ganha bônus a cada consecutivo
        if (f.getEstado() == Fantasma.Estado.ASSUSTADO) {
            f.matar();
            fantasmas_assustados++;
            int bonus = 200 * (int) Math.pow(2, fantasmas_assustados - 1);
            pontuacao_total += bonus;
            try { log.registrar("FANTASMA_COMIDO", f.getNome() + " +" + bonus); } catch (IOException ignored) {}
            // fantasma normal: pacman perde uma vida
        } else if (f.getEstado() == Fantasma.Estado.NORMAL) {
            pacman.perdervida();
            try { log.registrar("MORTE", "Vidas restantes: " + pacman.getVidas()); } catch (IOException ignored) {}
            if (!pacman.esta_vivo()) {
                // sem vidas restantes: game over
                partida_status = PartidaStatus.GAME_OVER;
            } else { // ainda tem vidas: reseta posições
                partida_status = PartidaStatus.MORREU;
            }
        }
    }
}
// reposiciona pacman e todos os fantasmas ao perder uma vida
public void resetarPosicoes() {
    pacman.resetar_posicao();
    for (Fantasma f : fantasmas) f.resetar();
    partida_status = PartidaStatus.JOGANDO;
}

// percorre a matriz contando pontos e power-ups restantes e retorna 0 quando pacman venceu
public int pontosRestantes() {
    int total = 0;
    for (int i = 0; i < mapaatual.getLinhas(); i++)
        for (int j = 0; j < mapaatual.getColunas(); j++) {
            int t = mapaatual.getTile(i, j);
            if (t == Mapa.ponto || t == Mapa.powerup) total++;
        }
    return total;
}

// get de atributos privados
public PacMan getPacman() { return pacman; }
public List<Fantasma> getFantasmas() { return fantasmas; }
public Mapa getMapaatual() { return mapaatual; }
public PartidaStatus getPartida_Status() { return partida_status; }
public int getPontuacao_total() { return pontuacao_total; }
public int getMapaIndex() { return mapaIndex; }
}
