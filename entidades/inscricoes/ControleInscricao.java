package entidades.inscricoes;

import aed3.Console;
import java.util.Scanner;
import entidades.cursos.ArquivoCurso;
import entidades.cursos.Curso;
import entidades.usuarios.ArquivoUsuario;
import entidades.usuarios.Usuario;

public class ControleInscricao {

    ArquivoInscricao arqInscricao;
    ArquivoCurso arqCursos;
    ArquivoUsuario arqUsuarios;
    Scanner console;

    public ControleInscricao() throws Exception {
        arqInscricao = new ArquivoInscricao();
        arqCursos = new ArquivoCurso();
        arqUsuarios = new ArquivoUsuario();
        console = new Scanner(System.in);
    }

    public void menuInscricoes(int userId) throws Exception {
        VisaoInscricao v = new VisaoInscricao();
        do {
            Console.clear();
            System.out.println("\nEntrePares 1.0");
            System.out.println("--------------");
            System.out.println("\n> Inicio > Minhas inscricoes\n");

            // mostra inscricoes do usuario
            entidades.inscricoes.Inscricao[] minhas = arqInscricao.readByUsuario(userId);
            if(minhas.length==0) {
                System.out.println("Nenhuma inscricao encontrada.");
            } else {
                for(int i=0;i<minhas.length;i++) {
                    entidades.inscricoes.Inscricao ins = minhas[i];
                    Curso c = arqCursos.read(ins.getIdCurso());
                    if(c!=null) {
                        String status = "";
                        if(c.getEstado()==1) status = " (INSCRIÇÕES ENCERRADAS)";
                        else if(c.getEstado()==2) status = " (CONCLUIDO)";
                        else if(c.getEstado()==3) status = " (CANCELADO)";
                        System.out.println("(" + (i+1) + ") " + c.getNome() + " - " + c.getDataInicio() + status);
                    }
                }
            }

            System.out.println();
            System.out.println("(A) Buscar curso por codigo");
            System.out.println("(B) Buscar curso por palavras-chave");
            System.out.println("(C) Listar todos os cursos");
            System.out.println();
            System.out.println("(R) Retornar ao menu anterior");
            System.out.print("\nOpcao: ");
            String op = console.nextLine();
            if(op.length()==0) continue;
            char ch = op.charAt(0);

            if(ch=='A' || ch=='a') {
                String codigo = v.pedirCodigo(console);
                if(codigo.length()==0) continue;
                Curso c = arqCursos.readCodigo(codigo);
                if(c==null) {
                    System.out.println("Curso nao encontrado com esse codigo.");
                    continue;
                }
                Usuario autor = arqUsuarios.read(c.getIdUsuario());
                Console.clear();
                v.mostraCursoDetalhado(c, autor==null?null:autor.getNome());
                boolean jaInscrito = arqInscricao.exists(userId, c.getID());
                if(jaInscrito)
                    System.out.println("(A) Cancelar minha inscricao no curso");
                else
                    System.out.println("(A) Fazer minha inscricao no curso");
                System.out.println("\n(R) Retornar ao menu anterior");
                System.out.print("\nOpcao: ");
                String op2 = console.nextLine();
                if(op2.length()==0) continue;
                char ch2 = op2.charAt(0);
                if(ch2=='A' || ch2=='a') {
                    if(arqInscricao.exists(userId, c.getID())) {
                        // cancelar inscricao existente
                        entidades.inscricoes.Inscricao[] userInscricoes = arqInscricao.readByUsuario(userId);
                        int insId = -1;
                        for(entidades.inscricoes.Inscricao ins : userInscricoes) {
                            if(ins.getIdCurso()==c.getID()) { insId = ins.getID(); break; }
                        }
                        if(insId!=-1) {
                            if(arqInscricao.delete(insId))
                                System.out.println("Inscricao cancelada com sucesso.");
                            else
                                System.out.println("Erro ao cancelar inscricao.");
                        } else {
                            System.out.println("Erro: inscricao nao encontrada.");
                        }
                        System.out.println();
                        System.out.println("Pressione ENTER para continuar...");
                        console.nextLine();
                        continue;
                    }
                    // tentará inscrever
                    if(c.getEstado()!=0) {
                        System.out.println("ALERTA: Este curso nao aceita inscricoes.");
                        System.out.println();
                        System.out.println("Pressione ENTER para continuar...");
                        console.nextLine();
                        continue;
                    }
                    entidades.inscricoes.Inscricao nova = new entidades.inscricoes.Inscricao(c.getID(), userId);
                    arqInscricao.create(nova);
                    System.out.println("Inscricao efetuada com sucesso!");
                    System.out.println();
                    System.out.println("Pressione ENTER para continuar...");
                    console.nextLine();
                } else if(ch2=='R' || ch2=='r') {
                    // retornar ao menu de inscricoes
                    continue;
                }
            }

            else if(ch=='B' || ch=='b') {
                System.out.print("Digite termos de busca: ");
                String consulta = console.nextLine();
                if(consulta.length()==0) continue;
                Curso[] encontrados = arqCursos.searchByKeywords(consulta);
                if(encontrados.length==0) {
                    System.out.println("Nenhum curso encontrado para os termos informados.");
                    System.out.println();
                    System.out.println("Pressione ENTER para continuar...");
                    console.nextLine();
                    continue;
                }
                int page = 0;
                int pageSize = 10;
                int totalPages = (encontrados.length + pageSize - 1)/pageSize;
                boolean done = false;
                while(!done) {
                    Console.clear();
                    System.out.println("\nEntrePares 1.0");
                    System.out.println("--------------");
                    System.out.println("\n> Inicio > Minhas inscricoes > Busca por palavras-chave\n");
                    System.out.println("Pagina " + (page+1) + " de " + totalPages + "\n");
                    int start = page*pageSize;
                    int end = Math.min(start+pageSize, encontrados.length);
                    for(int i=start;i<end;i++) {
                        int idx = i-start;
                        System.out.println("(" + (idx+1) + ") " + encontrados[i].getNome() + " - " + encontrados[i].getDataInicio());
                    }
                    System.out.println();
                    if(page>0) System.out.println("(p) Pagina anterior");
                    if(page<totalPages-1) System.out.println("(n) Proxima pagina");
                    System.out.println();
                    System.out.println("(R) Retornar ao menu anterior");
                    System.out.print("\nOpcao: ");
                    String sel = console.nextLine();
                    if(sel.length()==0) continue;
                    char c0 = sel.charAt(0);
                    if((c0=='P' || c0=='p') && page>0) { page--; continue; }
                    if((c0=='N' || c0=='n') && page<totalPages-1) { page++; continue; }
                    if(c0=='R' || c0=='r') { done = true; break; }
                    try {
                        int num = Integer.parseInt(sel);
                        if(num>=1 && num <= (end-start)) {
                            int selectedIndex = start + (num - 1);
                            Curso csel = encontrados[selectedIndex];
                            Usuario autor = arqUsuarios.read(csel.getIdUsuario());
                            Console.clear();
                            System.out.println("\nEntrePares 1.0");
                            System.out.println("--------------");
                            System.out.println("\n> Inicio > Minhas inscricoes > " + csel.getNome() + "\n");
                            v.mostraCursoDetalhado(csel, autor==null?null:autor.getNome());
                            boolean jaInscritoSel = arqInscricao.exists(userId, csel.getID());
                            if(jaInscritoSel)
                                System.out.println("(A) Cancelar minha inscricao no curso");
                            else
                                System.out.println("(A) Fazer minha inscricao no curso");
                            System.out.println("\n(R) Retornar ao menu anterior");
                            System.out.print("\nOpcao: ");
                            String op3 = console.nextLine();
                            if(op3.length()==0) continue;
                            char ch3 = op3.charAt(0);
                            if(ch3=='A' || ch3=='a') {
                                if(arqInscricao.exists(userId, csel.getID())) {
                                    // cancelar inscricao
                                    entidades.inscricoes.Inscricao[] userInscricoes = arqInscricao.readByUsuario(userId);
                                    int insId = -1;
                                    for(entidades.inscricoes.Inscricao ins : userInscricoes) {
                                        if(ins.getIdCurso()==csel.getID()) { insId = ins.getID(); break; }
                                    }
                                    if(insId!=-1) {
                                        if(arqInscricao.delete(insId))
                                            System.out.println("Inscricao cancelada com sucesso.");
                                        else
                                            System.out.println("Erro ao cancelar inscricao.");
                                    } else {
                                        System.out.println("Erro: inscricao nao encontrada.");
                                    }
                                    System.out.println();
                                    System.out.println("Pressione ENTER para continuar...");
                                    console.nextLine();
                                    continue;
                                }
                                if(csel.getEstado()!=0) {
                                    System.out.println("ALERTA: Este curso nao aceita inscricoes.");
                                    System.out.println();
                                    System.out.println("Pressione ENTER para continuar...");
                                    console.nextLine();
                                    continue;
                                }
                                entidades.inscricoes.Inscricao nova = new entidades.inscricoes.Inscricao(csel.getID(), userId);
                                arqInscricao.create(nova);
                                System.out.println("Inscricao efetuada com sucesso!");
                                System.out.println();
                                System.out.println("Pressione ENTER para continuar...");
                                console.nextLine();
                            } else if(ch3=='R' || ch3=='r') {
                                // return to list
                                continue;
                            }
                        } else {
                            System.out.println("Opcao invalida");
                        }
                    } catch(NumberFormatException e) {
                        System.out.println("Opcao invalida");
                    }
                }
            }

            else if(ch=='C' || ch=='c') {
                // paginação por data
                Curso[] todos = arqCursos.readAllByDate();
                if(todos.length==0) {
                    System.out.println("Nenhum curso cadastrado.");
                    continue;
                }
                int page = 0;
                int pageSize = 10;
                int totalPages = (todos.length + pageSize - 1)/pageSize;
                boolean doneList = false;
                while(!doneList) {
                    Console.clear();
                    System.out.println("\nEntrePares 1.0");
                    System.out.println("--------------");
                    System.out.println("\n> Inicio > Minhas inscricoes > Lista de cursos\n");
                    System.out.println("Pagina " + (page+1) + " de " + totalPages + "\n");
                                int start = page*pageSize;
                                int end = Math.min(start+pageSize, todos.length);
                                for(int i=start;i<end;i++) {
                                    int idx = i-start; // 0..9
                                    System.out.println("(" + (idx+1) + ") " + todos[i].getNome() + " - " + todos[i].getDataInicio());
                                }
                                System.out.println();
                                if(page>0) System.out.println("(p) Pagina anterior");
                                if(page<totalPages-1) System.out.println("(n) Proxima pagina");
                                System.out.println();
                                System.out.println("(R) Retornar ao menu anterior");
                                System.out.print("\nOpcao: ");
                                String sel = console.nextLine();
                                if(sel.length()==0) continue;
                                char c0 = sel.charAt(0);
                                if((c0=='P' || c0=='p') && page>0) { page--; continue; }
                                if((c0=='N' || c0=='n') && page<totalPages-1) { page++; continue; }
                                if(c0=='R' || c0=='r') { doneList = true; break; }
                                // try numeric selection (1..10)
                                try {
                                    int num = Integer.parseInt(sel);
                                    if(num>=1 && num <= (end-start)) {
                                        int selectedIndex = start + (num - 1);
                                        Curso csel = todos[selectedIndex];
                                        Usuario autor = arqUsuarios.read(csel.getIdUsuario());
                                        Console.clear();
                                        System.out.println("\nEntrePares 1.0");
                                        System.out.println("--------------");
                                        System.out.println("\n> Inicio > Minhas inscricoes > Lista de cursos > " + csel.getNome() + "\n");
                                        v.mostraCursoDetalhado(csel, autor==null?null:autor.getNome());
                                        boolean jaInscritoSel = arqInscricao.exists(userId, csel.getID());
                                        if(jaInscritoSel)
                                            System.out.println("(A) Cancelar minha inscricao no curso");
                                        else
                                            System.out.println("(A) Fazer minha inscricao no curso");
                                        System.out.println("\n(R) Retornar ao menu anterior");
                                        System.out.print("\nOpcao: ");
                                        String op3 = console.nextLine();
                                        if(op3.length()==0) continue;
                                        char ch3 = op3.charAt(0);
                                        if(ch3=='A' || ch3=='a') {
                                            if(arqInscricao.exists(userId, csel.getID())) {
                                                // cancelar inscricao
                                                entidades.inscricoes.Inscricao[] userInscricoes = arqInscricao.readByUsuario(userId);
                                                int insId = -1;
                                                for(entidades.inscricoes.Inscricao ins : userInscricoes) {
                                                    if(ins.getIdCurso()==csel.getID()) { insId = ins.getID(); break; }
                                                }
                                                if(insId!=-1) {
                                                    if(arqInscricao.delete(insId))
                                                        System.out.println("Inscricao cancelada com sucesso.");
                                                    else
                                                        System.out.println("Erro ao cancelar inscricao.");
                                                } else {
                                                    System.out.println("Erro: inscricao nao encontrada.");
                                                }
                                                System.out.println();
                                                System.out.println("Pressione ENTER para continuar...");
                                                console.nextLine();
                                                continue;
                                            }
                                            if(csel.getEstado()!=0) {
                                                System.out.println("ALERTA: Este curso nao aceita inscricoes.");
                                                System.out.println();
                                                System.out.println("Pressione ENTER para continuar...");
                                                console.nextLine();
                                                continue;
                                            }
                                            entidades.inscricoes.Inscricao nova = new entidades.inscricoes.Inscricao(csel.getID(), userId);
                                            arqInscricao.create(nova);
                                            System.out.println("Inscricao efetuada com sucesso!");
                                            System.out.println();
                                            System.out.println("Pressione ENTER para continuar...");
                                            console.nextLine();
                                        } else if(ch3=='R' || ch3=='r') {
                                            // return to list
                                            continue;
                                        }
                                    } else {
                                        System.out.println("Opcao invalida");
                                    }
                                } catch(NumberFormatException e) {
                                    System.out.println("Opcao invalida");
                                }
                }
            }

            else if(ch=='R' || ch=='r') {
                break;
            }

            else {
                // treat numeric selection from user's own inscricoes
                try {
                    int sel = Integer.parseInt(op);
                    if(sel>=1 && sel<=minhas.length) {
                        entidades.inscricoes.Inscricao ins = minhas[sel-1];
                        Curso c = arqCursos.read(ins.getIdCurso());
                        Usuario autor = arqUsuarios.read(c.getIdUsuario());
                        Console.clear();
                        System.out.println("\nEntrePares 1.0");
                        System.out.println("--------------");
                        System.out.println("\n> Inicio > Minhas inscricoes > " + c.getNome() + "\n");
                        v.mostraCursoDetalhado(c, autor==null?null:autor.getNome());
                        System.out.println("(A) Cancelar minha inscricao no curso");
                        System.out.println("\n(R) Retornar ao menu anterior");
                        System.out.print("\nOpcao: ");
                        String op4 = console.nextLine();
                        if(op4.length()==0) continue;
                        char ch4 = op4.charAt(0);
                        if(ch4=='A' || ch4=='a') {
                            if(arqInscricao.delete(ins.getID())) {
                                System.out.println("Inscricao cancelada.");
                            } else {
                                System.out.println("Erro ao cancelar inscricao.");
                            }
                        }
                    } else {
                        System.out.println("Opcao invalida");
                    }
                } catch(NumberFormatException e) {
                    System.out.println("Opcao invalida");
                }
            }

            // no pause: return immediately to menu loop

        } while(true);
    }

    public void close() throws Exception {
        arqInscricao.close();
        arqCursos.close();
        arqUsuarios.close();
    }

}
