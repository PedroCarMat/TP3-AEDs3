package entidades.cursos;

import java.time.LocalDate;
import java.util.Scanner;

public class VisaoCurso {

    public Curso leCurso(Scanner console, int idUsuario) throws Exception {
        System.out.println("INCLUSAO DE CURSO");
        System.out.print("Nome do curso: ");
        String nome = console.nextLine();
        if(nome.length()==0)
            return null;

        LocalDate dataInicio = LocalDate.now();
        boolean dadosValidos = false;
        do {
            System.out.print("Data de inicio (dd/mm/aaaa): ");
            String aux = console.nextLine();
            if(aux.length()==0) {
                dataInicio = LocalDate.now();
                dadosValidos = true;
            } else {
                try {
                    String[] parts = aux.split("/");
                    dataInicio = LocalDate.of(
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[0]));
                    dadosValidos = true;
                } catch(Exception e) {
                    System.out.println("Data invalida!");
                }
            }
        } while(!dadosValidos);

        System.out.print("Descricao: ");
        String descricao = console.nextLine();
        if(descricao.length()==0)
            descricao = "";

        Curso c = new Curso(nome, dataInicio, descricao, idUsuario);
        return c;
    }

    public void mostraCurso(Curso c) {
        System.out.println(c);
    }

}
