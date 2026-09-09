package visao;
import modelo.EstadoJogo;

import javax.swing.*;
import java.awt.*;

public class TelaSelecaoMapa extends JPanel{
// cores da interface
private static final Color COR_FUNDO= new Color(0, 0, 0);
private static final Color COR_AMARELO= new Color(255, 220, 0);
private static final Color COR_TEXTO= new Color(220, 220, 220);

public TelaSelecaoMapa(EstadoJogo estado, JFrame janela) {
    setBackground(COR_FUNDO);
    setLayout(new GridBagLayout()); // centraliza os elementos na tela

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(12, 20, 12, 20); // espaçamento
    gbc.gridx = 0;

    JLabel titulo = new JLabel("PAC-MAN");
    titulo.setFont(new Font("Monospaced", Font.BOLD, 48));
    titulo.setForeground(COR_AMARELO);
    gbc.gridy = 0;
    add(titulo, gbc);

    JLabel subtitulo = new JLabel("Escolha o mapa");
    subtitulo.setFont(new Font("Monospaced", Font.PLAIN, 18));
    subtitulo.setForeground(COR_TEXTO);
    gbc.gridy = 1;
    add(subtitulo, gbc);

    String[] nomes = {"Mapa 1 - Fácil", "Mapa 2 - Médio", "Mapa 3 - Difícil"};
    Color[] cores = {new Color(0, 180, 0), new Color(200, 150, 0), new Color(200, 0, 0)};

    // cria um botão para cada mapa com nome e cor correspondente
    for (int i = 0; i < 3; i++) {
        final int idx = i;
        JButton btn = new JButton(nomes[i]);
        btn.setFont(new Font("Monospaced", Font.BOLD, 16));
        btn.setBackground(cores[i]);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(COR_AMARELO, 2));
        btn.setPreferredSize(new Dimension(220, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // cursor de mãozinha ao passar
        btn.addActionListener(e -> iniciarJogo(estado, janela, idx)); // inicia o jogo com o mapa idx
        gbc.gridy = 2 + i;
        add(btn, gbc);
    }
// exibe nome e vidas do jogador carregados pelo p1
   JLabel info = new JLabel("Jogador: " + estado.getNomeJogador() +
        "  ||  Vidas: " + estado.getVidasIniciais());
    info.setFont(new Font("Monospaced", Font.PLAIN, 13));
    info.setForeground(new Color(150, 150, 150));
    gbc.gridy = 5;
    add(info, gbc);
}

private void iniciarJogo(EstadoJogo estado, JFrame janela, int mapaIdx) {
    try {
        TelaJogo telaJogo = new TelaJogo(estado, mapaIdx, janela, this);
        janela.setContentPane(telaJogo);
        janela.pack(); // ajusta o tamanho da janela ao novo conteúdo
        janela.revalidate();
        telaJogo.requestFocusInWindow(); // necessário para capturar o teclado
        telaJogo.iniciar();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(janela, "Erro ao iniciar jogo: " + e.getMessage());
    }
}
}
