package modelo;

import java.io.*;
import java.util.List;
// agrupa todos os dados iniciais do jogo para serialização
public class EstadoJogo implements Serializable, Persistivel {
    private static final long serialVersionUID = 1L;

private String nomeJogador;
private int vidasIniciais;
private List<DadosFantasma> dadosFantasmas;
private Mapa[] mapas;
// dados de um fantasma lidos do csv: nome, tipo e cor
public static class DadosFantasma implements Serializable {
private static final long serialVersionUID = 1L;
private String nome;
private TipoFantasma tipo;
private int cor; //cor em rgb como int

    // construtor de DadosFantasma armazena nome, tipo e cor lidos do csv
public DadosFantasma(String nome, TipoFantasma tipo, int cor) {
    this.nome = nome; this.tipo = tipo; this.cor = cor;
}

    public String getNome() { return nome; }
    public TipoFantasma getTipo() { return tipo; }
    public int getCor() { return cor; }}



    // construtor do estado completo do jogo
public EstadoJogo(String nomeJogador, int vidasIniciais,
    List<DadosFantasma> dadosFantasmas, Mapa[] mapas) {
    this.nomeJogador = nomeJogador;
    this.vidasIniciais = vidasIniciais;
    this.dadosFantasmas = dadosFantasmas;
    this.mapas = mapas;
}
// getters dos atributos privados
public String getNomeJogador() { return nomeJogador; }
public int getVidasIniciais() { return vidasIniciais; }
public List<DadosFantasma> getDadosFantasmas() { return dadosFantasmas; }
public Mapa[] getMapas() { return mapas; }
public Mapa getMapa(int idx) { return mapas[idx]; }

    // serializa o estado completo em binário, implementação da interface persistivel
@Override
public void salvar(String caminho) throws IOException {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminho))) {
        oos.writeObject(this); // escreve o objeto inteiro no arquivo binário
    }
}
    // desserializa o estado a partir do binário salvo pelo p1
public static EstadoJogo carregar(String caminho) throws IOException, ClassNotFoundException {
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminho))) {
        return (EstadoJogo) ois.readObject(); // reconstrói o objeto na memória
    }
}
}
