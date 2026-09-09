package salvos;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// escreve eventos do jogo em um arquivo csv com timestamp
public class EscritorLog {
private String caminho;
private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
// cria o arquivo de log e escreve o cabeçalho e sobrescreve se já existir
public EscritorLog(String caminho) throws IOException {
    this.caminho = caminho;
    try (PrintWriter pw = new PrintWriter(new FileWriter(caminho, false))) {
        pw.println("timestamp,evento,descricao");
    }
}
// adiciona uma linha ao csv com timestamp, tipo do evento e descrição
public void registrar(String evento, String descricao) throws IOException {
    try (PrintWriter pw = new PrintWriter(new FileWriter(caminho, true))) {
        pw.println(LocalDateTime.now().format(fmt) + "," + evento + "," + descricao);
    }
}
}
