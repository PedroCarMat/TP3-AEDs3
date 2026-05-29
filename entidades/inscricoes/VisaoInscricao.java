package entidades.inscricoes;

import java.util.Scanner;
import entidades.cursos.Curso;

public class VisaoInscricao {

    public String pedirCodigo(Scanner console) {
        System.out.print("Codigo do curso: ");
        return console.nextLine().trim();
    }

    public void mostraCursoDetalhado(Curso c, String autorNome) {
        System.out.println("CÓDIGO........: " + c.getCodigoCompartilhavel());
        System.out.println("CURSO.........: " + c.getNome());
        System.out.println("AUTOR.........: " + (autorNome==null?"":autorNome));
        System.out.println("DESCRIÇÃO.....: " + c.getDescricao());
        System.out.println("DATA DE INÍCIO: " + c.getDataInicio());
        System.out.println();
    }

}
