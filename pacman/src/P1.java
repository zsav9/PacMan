import modelo.*;
import salvos.LeitorCSV;
import java.io.IOException;
import java.util.List;

// lê os dados do jogador, fantasmas e mapas dos csvs e salva o estado inicial em binário

public class P1 {
    public static void main(String[] args) {
    String base = "data/";
    String saida = "data/estado.bin";
    try {System.out.println("P1: define estados iniciais do Pacman \n");
        String[] dadosJogador = LeitorCSV.lerJogador(base + "jogador.csv");
        String nome = dadosJogador[0].trim();
        int vidas = Integer.parseInt(dadosJogador[1].trim());
        // valida se nome não está vazio e se vidas é positivo
        if (nome.isEmpty() || vidas <= 0) {
            throw new ConfiguracaoInvalidaException("Dados do jogador inválidos: nome='" + nome + "', vidas=" + vidas);
        }
        System.out.println("Jogador: " + nome + " =-=- Vidas: " + vidas);
        // lê cada linha do fantasmas.csv e converte para DadosFantasma
        List<EstadoJogo.DadosFantasma> fantasmas = LeitorCSV.LerFantasmas(base + "fantasmas.csv");
        System.out.println("Fantasmas carregados: " + fantasmas.size());
        for (EstadoJogo.DadosFantasma f : fantasmas)
            System.out.println(f.getNome() + " (" + f.getTipo() + ")");
        Mapa[] mapas = new Mapa[3]; // carrega os três mapas do csv converte para objetos Mapa
        for (int i = 1; i <= 3; i++) {
            mapas[i-1] = LeitorCSV.lerMapa(base + "mapas/mapa" + i + ".csv");
            System.out.println("Mapa " + i + " carregado: " +
                mapas[i-1].getLinhas() + "x" + mapas[i-1].getColunas());}
        EstadoJogo estado = new EstadoJogo(nome, vidas, fantasmas, mapas); // salva o estado em binário
        estado.salvar(saida);
        System.out.println("\nEstado salvo em: " + saida);
        System.out.println("P1 concluído");

    } catch (ArquivoInvalidoException e) { // csv vazio, linha faltando ou tipo de fantasma inválido
        System.err.println("erro de arquivo inválido " + e.getMessage());
    } catch (ConfiguracaoInvalidaException e) { // nome vazio ou número de vidas inválido
        System.err.println("erro de configuração " + e.getMessage());
    } catch (IOException e) { // arquivo não encontrado ou sem permissão de leitura/escrita
        System.err.println("erro de arquivo] " + e.getMessage());
    }
}
}