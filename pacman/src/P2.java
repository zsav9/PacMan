import modelo.EstadoJogo;
import visao.TelaSelecaoMapa;
import javax.swing.*;
import java.io.IOException;

public class P2 {
    public static void main(String[] args) {
    try {EstadoJogo estado = EstadoJogo.carregar("data/estado.bin");
        // invokeLater garante que a interface gráfica rode na thread correta do swing
        SwingUtilities.invokeLater(() -> {
            JFrame janela = new JFrame("PAC-MAN");
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // fecha o programa ao fechar a janela
            janela.setResizable(false); // tamanho fixo
            TelaSelecaoMapa selecao = new TelaSelecaoMapa(estado, janela); // tela inicial de seleção de mapa
            janela.setContentPane(selecao); // define a tela de seleção de mapa como conteúdo principal da janela
            janela.pack(); // ajusta o tamanho da janela ao conteúdo
            janela.setLocationRelativeTo(null);  // centraliza na tela
            janela.setVisible(true);});

    } catch (IOException e) { // arquivo não encontrado ou corrompido
        JOptionPane.showMessageDialog(null,
            "Erro ao carregar!!\nRode o P1 primeiro.\n\n" + e.getMessage(),
            "Erro", JOptionPane.ERROR_MESSAGE);
    } catch (ClassNotFoundException e) { // exception p quando o arquivo é incompatível (classe não encontrada)
        JOptionPane.showMessageDialog(null,
            "arquivo corrompido!\n" + e.getMessage(),
            "Erro", JOptionPane.ERROR_MESSAGE);
    }
}}
