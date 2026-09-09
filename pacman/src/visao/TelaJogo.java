package visao;

import controle.ControladorJogo;
import modelo.*;
import salvos.EscritorLog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class TelaJogo extends JPanel implements KeyListener, ActionListener {

private static final int CELULA = 28; // tamanho de cada célula em pixels
private static final int DELAY = 105; // ms por tick

private ControladorJogo controlador;
private EstadoJogo estado;
private JFrame janela;
private JPanel telaAnterior; // tela de seleção, usada para voltar após game over
private Timer timer; // timer do swing que dispara o update a cada tick
private EscritorLog log;
// cores usadas na renderização
private static final Color COR_FUNDO= Color.BLACK;
private static final Color COR_PAREDE= new Color(0, 0, 180);
private static final Color COR_PONTO= new Color(255, 200, 150);
private static final Color COR_POWERUP= new Color(255, 220, 0);
private static final Color cor_PACMAN = new Color(255, 220, 0);
private static final Color COR_TEXTO= new Color(220, 220, 220);
private static final Color COR_HUD= new Color(20, 20, 20);

// inicializa a tela, o controlador e o timer
public TelaJogo(EstadoJogo estado, int mapaIdx, JFrame janela, JPanel telaAnterior) throws IOException {
this.estado = estado;
this.janela = janela;
this.telaAnterior = telaAnterior;
this.log = new EscritorLog("output/log.csv");
this.controlador = new ControladorJogo(estado, mapaIdx, "output/log.csv");

Mapa mapa = controlador.getMapaatual();
// tamanho da janela baseado no mapa + 50px do HUD
setPreferredSize(new Dimension(
    mapa.getColunas() * CELULA,
    mapa.getLinhas() * CELULA + 50
));
setBackground(COR_FUNDO);
setFocusable(true); // necessário para capturar eventos de teclado
addKeyListener(this);

timer = new Timer(DELAY, this);}

public void iniciar() {
    timer.start();
}

@Override
public void actionPerformed(ActionEvent e) {
    controlador.update();
    repaint();
    ControladorJogo.PartidaStatus ep = controlador.getPartida_Status();
    if (ep == ControladorJogo.PartidaStatus.MORREU) {
        timer.stop();
        mostrarMensagem("Você morreu!! Vidas: " + controlador.getPacman().getVidas(), () -> {
            controlador.resetarPosicoes();
            timer.start();
        });
    } else if (ep == ControladorJogo.PartidaStatus.GAME_OVER) {
        timer.stop();
        mostrarMensagem("GAME OVER!! Pontos: " + controlador.getPontuacao_total(), () -> voltarSelecao());
    } else if (ep == ControladorJogo.PartidaStatus.VENCEU) {
        timer.stop();
        mostrarMensagem("VOCÊ VENCEU!! Pontos: " + controlador.getPontuacao_total(), () -> voltarSelecao());}
}
// renderiza todos os elementos visuais a cada frame
@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    desenharMapa(g2);
    desenharFantasmas(g2);
    desenharPacMan(g2);
    desenharHUD(g2);
}
// desenha cada célula do mapa
private void desenharMapa(Graphics2D g) {
    Mapa mapa = controlador.getMapaatual();
    for (int i = 0; i < mapa.getLinhas(); i++) {
        for (int j = 0; j < mapa.getColunas(); j++) {
            int tile = mapa.getTile(i, j);
            int x = j * CELULA, y = i * CELULA;
            switch (tile) {
                case Mapa.parede:
                    g.setColor(COR_PAREDE);
                    g.fillRoundRect(x + 1, y + 1, CELULA - 2, CELULA - 2, 6, 6);
                    g.setColor(new Color(0, 0, 220)); // borda mais clara
                    g.drawRoundRect(x + 1, y + 1, CELULA - 2, CELULA - 2, 6, 6);
                    break;
                case Mapa.ponto:
                    g.setColor(COR_PONTO);
                    g.fillOval(x + CELULA /2 - 3, y + CELULA /2 - 3, 6, 6);
                    break;
                case Mapa.powerup:
                    g.setColor(COR_POWERUP);
                    g.fillOval(x + CELULA /2 - 7, y + CELULA /2 - 7, 14, 14); // maior que o ponto normal
                    break;
                default:
                    // "vazio" e "base" ficam com fundo preto
                    break;
            }
        }
    }}
// desenha pacman com a boca apontando para a direção atual
private void desenharPacMan(Graphics2D g) {
    PacMan pac = controlador.getPacman();
    int x = pac.getColuna() * CELULA + 2;
    int y = pac.getLinha() * CELULA + 2;
    int size = CELULA - 4;

    g.setColor(cor_PACMAN);
    // Ângulo da boca baseado na direção
    int startAngle = 30;
    switch (pac.getDirecao()) {
        case DIREITA: startAngle = 30; break;
        case ESQUERDA: startAngle = 210; break;
        case CIMA: startAngle = 120; break;
        case BAIXO: startAngle = 300; break;
    }
    g.fillArc(x, y, size, size, startAngle, 300);}

// desenha cada fantasminha com corpo, ondas e olhos
private void desenharFantasmas(Graphics2D g) {
    for (Fantasma f : controlador.getFantasmas()) {
    int x = f.getColuna() * CELULA + 2;
    int y = f.getLinha() * CELULA + 2;
    int w = CELULA - 4, h = CELULA - 4;

    g.setColor(f.getCorAtual()); // cor muda conforme o estado
    // Corpo circular no topo
    g.fillArc(x, y, w, h, 0, 180);
    g.fillRect(x, y + h/2, w, h/2); // corpo retangular

    // ondas na base do fantasma
    int sw = w / 3;
    for (int i = 0; i < 3; i++) {
        g.fillArc(x + i * sw, y + h - sw/2, sw, sw, 0, -180);
    }

    // Olhinhos
    g.setColor(Color.WHITE);
    g.fillOval(x + w/4 - 2, y + h/4, 7, 7);
    g.fillOval(x + w*3/4 - 5, y + h/4, 7, 7);
    if (f.getEstado() != Fantasma.Estado.ASSUSTADO) {
        g.setColor(new Color(0, 0, 180));
        g.fillOval(x + w/4, y + h/4 + 1, 4, 4);
        g.fillOval(x + w*3/4 - 3, y + h/4 + 1, 4, 4);
    }
}
}
// desenha a HUD inferior
private void desenharHUD(Graphics2D g) {
    Mapa mapa = controlador.getMapaatual();
    int y = mapa.getLinhas() * CELULA;
    PacMan pac = controlador.getPacman();
    g.setColor(COR_HUD);
    g.fillRect(0, y, getWidth(), 50);
    g.setColor(COR_TEXTO);
    g.setFont(new Font("Monospaced", Font.BOLD, 14));
    g.drawString("Pontos: " + controlador.getPontuacao_total(), 10, y + 20);
    g.drawString("Vidas: " + pac.getVidas(), 10, y + 38);
    g.drawString("Mapa " + (controlador.getMapaIndex() + 1), getWidth() - 80, y + 20);
    g.drawString(pac.getNome(), getWidth() - 120, y + 38);
}
// exibe uma caixa de diálogo após 500ms e executa o callback ao fechar
private void mostrarMensagem(String msg, Runnable callback) {
    Timer t = new Timer(500, e -> {
        JOptionPane.showMessageDialog(janela, msg);
        callback.run();
    });
    t.setRepeats(false);
    t.start();
}
// volta para a tela de seleção de mapa
private void voltarSelecao() {
    janela.setContentPane(telaAnterior);
    janela.revalidate();
}
// captura teclas do jogador e define a próxima direção do pacman
@Override
public void keyPressed(KeyEvent e) {
    PacMan pac = controlador.getPacman();
    switch (e.getKeyCode()) {
        case KeyEvent.VK_UP:case KeyEvent.VK_W: pac.setproximaDirecao(Personagem.Direcao.CIMA); break;
        case KeyEvent.VK_DOWN:case KeyEvent.VK_S: pac.setproximaDirecao(Personagem.Direcao.BAIXO); break;
        case KeyEvent.VK_LEFT:case KeyEvent.VK_A: pac.setproximaDirecao(Personagem.Direcao.ESQUERDA); break;
        case KeyEvent.VK_RIGHT:case KeyEvent.VK_D: pac.setproximaDirecao(Personagem.Direcao.DIREITA); break;
    }
}

@Override public void keyReleased(KeyEvent e) {}
@Override public void keyTyped(KeyEvent e) {}
}
