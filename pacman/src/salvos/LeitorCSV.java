package salvos;
import modelo.*;
import java.io.*;
import java.util.*;
public class LeitorCSV {
// lê os arquivos csv e converte para objetos
public static String[] lerJogador(String caminho) throws IOException, ArquivoInvalidoException {
    // lê nome e vidas do jogador.csv, throws exceção se arquivo estiver vazio
    try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
        br.readLine(); // header
        String linha = br.readLine();
        if (linha == null) throw new ArquivoInvalidoException("Arquivo jogador.csv está vazio!!");
        return linha.split(",", -1);
    }
}
    // lê fantasmas.csv e converte cada linha em DadosFantasma throws exceção se tipo inválido ou lista vazia
public static List<EstadoJogo.DadosFantasma> LerFantasmas(String caminho)
        throws IOException, ArquivoInvalidoException {
    List<EstadoJogo.DadosFantasma> lista = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
        String linha;
        boolean primeira = true;
        while ((linha = br.readLine()) != null) {
            if (primeira) { primeira = false; continue; }  // pula header
            String[] p = linha.split(",", -1);
            if (p.length < 3) throw new ArquivoInvalidoException("Linha inválida em fantasmas.csv: " + linha);
            String nome = p[0].trim();
            TipoFantasma tipo;
            try {
                tipo = TipoFantasma.valueOf(p[1].trim()); // converte string para enum
            } catch (IllegalArgumentException e) {
                throw new ArquivoInvalidoException("Tipo de fantasma inválido: " + p[1].trim());
            }
            int cor = (int) Long.parseLong(p[2].trim(), 16); // converte hex para int RGB
            lista.add(new EstadoJogo.DadosFantasma(nome, tipo, cor));
        }
    }
    if (lista.isEmpty()) throw new ArquivoInvalidoException("Nenhum fantasma encontrado em " + caminho);
    return lista;
}
// lê mapaX.csv e converte para objeto Mapa
public static Mapa lerMapa(String caminho) throws IOException, ArquivoInvalidoException{
    List<int[]> linhas = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
        String linha;
        while ((linha = br.readLine()) != null) {
            linha = linha.trim();
            if (linha.isEmpty() || linha.startsWith("#")) continue; // ignora comentario
            String[] partes = linha.split(",");
            int[] row = new int[partes.length];
            for (int i = 0; i < partes.length; i++) {
                row[i] = Integer.parseInt(partes[i].trim()); // converte cada célula para int
            }
            linhas.add(row);
        }
    }
    if (linhas.isEmpty()) throw new ArquivoInvalidoException("Mapa vazio: " + caminho);
    return new Mapa(linhas.toArray(new int[0][]));
    }
}
