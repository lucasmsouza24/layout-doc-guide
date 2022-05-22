import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class App {

    public static void gravaRegistro(String registro, String nomeArq) {
        BufferedWriter saida = null;

        // try-catch para abrir o arquivo
        try {
            saida = new BufferedWriter(new FileWriter(nomeArq, true));
        }
        catch (IOException erro) {
            System.out.println("Erro ao abrir o arquivo: " + erro);
        }

        // try-catch para gravar o registro e fechar o arquivo
        try {
            saida.append(registro + "\n");
            saida.close();
        }
        catch (IOException erro) {
            System.out.println("Erro ao gravar o arquivo: " + erro);
        }
    }

    public static void gravaArquivoTxt(List<Aluno> lista, String nomeArq) {
        int contaRegCorpo = 0;

        // Monta o registro de header
        String header = "00NOTA20221";
        header += LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        header += "01";
        // Grava o registro de header
        gravaRegistro(header, nomeArq);

        // Monta e grava os registros de corpo
        String corpo;
        for (Aluno a : lista) {
            corpo = "02";
            corpo += String.format("%-5.5s", a.getCurso());
            corpo += String.format("%-8.8s", a.getRa());
            corpo += String.format("%-50.50s", a.getNome());
            corpo += String.format("%-40.40s", a.getDisciplina());
            corpo += String.format("%05.2f", a.getMedia());
            corpo += String.format("%03d", a.getQtdFalta());
            contaRegCorpo++;
            gravaRegistro(corpo, nomeArq);
        }

        // Monta e grava o registro de trailer
        String trailer = "01";
        trailer += String.format("%010d", contaRegCorpo);
        gravaRegistro(trailer, nomeArq);
    }

    public static void leArquivoTxt(String nomeArq) {
        BufferedReader entrada = null;
        String registro, tipoRegistro;
        String ra, nome, curso, disciplina;
        Double media;
        Integer qtdFalta;
        int contaRegCorpoLido = 0;
        int qtdRegCorpoGravado;

        List<Aluno> listaLida = new ArrayList<>();

        // try-catch para abrir o arquivo
        try {
            entrada = new BufferedReader(new FileReader(nomeArq));
        }
        catch (IOException erro) {
            System.out.println("Erro ao abrir o arquivo: " + erro);
        }

        // try-catch para ler e fechar o arquivo
        try {
            // Leitura do primeiro registro do arquivo
            registro = entrada.readLine();

            while (registro != null) { // enquanto não chegou ao final do arquivo
                // obtém os 2 primeiros caracteres do registro
                // 01234567
                // 00NOTA20221
                tipoRegistro = registro.substring(0,2);
                if (tipoRegistro.equals("00")) {
                    System.out.println("É um registro de header");
                    System.out.println("Tipo de arquivo: " + registro.substring(2,6));
                    System.out.println("Ano e semestre: " + registro.substring(6,11));
                    System.out.println("Data e hora da gravação: " + registro.substring(11,30));
                    System.out.println("Versão do documento: " + registro.substring(30,32));
                }
                else if (tipoRegistro.equals("01")) {
                    System.out.println("É um registro de trailer");
                    qtdRegCorpoGravado = Integer.parseInt(registro.substring(2,12));
                    if (contaRegCorpoLido == qtdRegCorpoGravado) {
                        System.out.println("Quantidade de registros lidos é compatível " +
                                "com a quantidade de registros gravados");
                    }
                    else {
                        System.out.println("Quantidade de registros lidos não é compatível " +
                                "com a quantidade de registros gravados");
                    }
                 }
                else if (tipoRegistro.equals("02")) {
                    System.out.println("É um registro de corpo");
                    curso = registro.substring(2,7).trim();
                    ra = registro.substring(7,15).trim();
                    nome = registro.substring(15,65).trim();
                    disciplina = registro.substring(65,105).trim();
                    media = Double.valueOf(registro.substring(105,110).replace(',','.'));
                    qtdFalta = Integer.valueOf(registro.substring(110,113));
                    contaRegCorpoLido++;

                    Aluno a = new Aluno(ra,nome,curso,disciplina,media,qtdFalta);

                    // No projeto de PI, poderia fazer:
                    // repository.save(a);

                    // No nosso caso, vamos adicionar o objeto a na listaLida:
                    listaLida.add(a);
                }
                else {
                    System.out.println("Tipo de registro inválido!");
                }

                // Lê o próximo registro
                registro = entrada.readLine();
            }

            entrada.close();
        }
        catch (IOException erro) {
            System.out.println("Erro ao ler o arquivo: " + erro);
        }

        // No Projeto de PI, pode-se fazer:
        // repository.saveAll(listaLida);

        // Vamos exibir a listaLida
        System.out.println("\nConteúdo da lista lida:");
        for (Aluno a : listaLida) {
            System.out.println(a);
        }
    }


    public static void main(String[] args) {
        List<Aluno> lista = new ArrayList<Aluno>();

        lista.add(new Aluno("01211000", "Aleff Stampini",
                "ADS","Estrutura de Dados",9.0,10));
        lista.add(new Aluno("01211010", "Bianca Silva",
                "ADS","Programação WEB",9.5,7));
        lista.add(new Aluno("02211020", "José Sousa",
                "CCO","Cálculo Computacional",7.0,12));
        lista.add(new Aluno("03221000", "Ana Teixeira",
                "SIS","Banco de Dados",8.5,8));
        lista.add(new Aluno("04221030", "Mario Bros",
                "REDES","Segurança",6.0,20));

        for (Aluno a : lista) {
            System.out.println(a);
        }

        gravaArquivoTxt(lista, "aluno.txt");
        leArquivoTxt("aluno.txt");

    }
}
