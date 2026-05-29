
package entidades.cursos;

import aed3.Console;
import java.util.Scanner;
import entidades.inscricoes.ArquivoInscricao;
import entidades.inscricoes.Inscricao;
import entidades.usuarios.ArquivoUsuario;
import entidades.usuarios.Usuario;
import java.io.FileWriter;
import java.io.PrintWriter;

public class ControleCurso {

    ArquivoCurso arqCursos;
    ArquivoInscricao arqInscricoes;
    ArquivoUsuario arqUsuarios;
    Scanner console;

    public ControleCurso() throws Exception {
        arqCursos = new ArquivoCurso();
        arqInscricoes = new ArquivoInscricao();
        arqUsuarios = new ArquivoUsuario();
        console = new Scanner(System.in);
    }

    public void menuCursos(int userId) throws Exception {
        VisaoCurso v = new VisaoCurso();
        do {
            Curso[] cursos = arqCursos.readUsuario(userId);
            int page = 0;
            int pageSize = 10;
            if(cursos.length==0) {
                Console.clear();
                System.out.println("\nEntrePares 1.0");
                System.out.println("--------------");
                System.out.println("\n> Inicio > Meus Cursos\n");
                System.out.println("Nenhum curso cadastrado.");
                System.out.println("\n(A) Novo curso");
                System.out.println("(R) Retornar ao menu anterior");
                System.out.print("\nOpcao: ");
                String op0 = console.nextLine();
                if(op0.length()==0) continue;
                char ch0 = op0.charAt(0);
                if(ch0=='A' || ch0=='a') {
                    Curso novo = v.leCurso(console, userId);
                    if(novo!=null) {
                        arqCursos.create(novo);
                        System.out.println("Curso criado! Codigo: " + novo.getCodigoCompartilhavel());
                    }
                } else if(ch0=='R' || ch0=='r') {
                    break;
                }
                continue;
            }

            int totalPages = (cursos.length + pageSize - 1) / pageSize;
            boolean done = false;
            while(!done) {
                Console.clear();
                System.out.println("\nEntrePares 1.0");
                System.out.println("--------------");
                System.out.println("\n> Inicio > Meus Cursos\n");
                System.out.println("Pagina " + (page+1) + " de " + totalPages + "\n");

                    int start = page * pageSize;
                    int end = Math.min(start + pageSize, cursos.length);
                    for(int i = start; i < end; i++) {
                        int idx = i - start; // 0..9
                        System.out.println("(" + (idx+1) + ") " + cursos[i].getNome() + " - " + cursos[i].getDataInicio());
                    }

                    System.out.println();
                    if(page>0) System.out.println("(p) Pagina anterior");
                    if(page<totalPages-1) System.out.println("(n) Proxima pagina");
                    System.out.println("(A) Novo curso");
                    System.out.println("(R) Retornar ao menu anterior");
                    System.out.print("\nOpcao: ");
                    String op = console.nextLine();
                    if(op.length()==0) continue;
                    char ch = op.charAt(0);
                    if((ch=='P' || ch=='p') && page>0) { page--; continue; }
                    if((ch=='N' || ch=='n') && page<totalPages-1) { page++; continue; }
                    if(ch=='A' || ch=='a') {
                        Curso novo = v.leCurso(console, userId);
                        if(novo!=null) {
                            arqCursos.create(novo);
                            System.out.println("Curso criado! Codigo: " + novo.getCodigoCompartilhavel());
                        }
                        // reload cursos
                        cursos = arqCursos.readUsuario(userId);
                        totalPages = (cursos.length + pageSize - 1) / pageSize;
                        continue;
                    }
                    else if(ch=='R' || ch=='r') { done = true; break; }

                    // numeric selection (1..10)
                    try {
                        int num = Integer.parseInt(op);
                        if(num>=1 && num <= (end-start)) {
                            int selectedIndex = start + (num - 1);
                            Console.clear();
                            gerenciarCurso(cursos[selectedIndex]);
                            // after return, reload list and adjust pages
                            cursos = arqCursos.readUsuario(userId);
                            totalPages = (cursos.length + pageSize - 1) / pageSize;
                            if(page >= totalPages) page = Math.max(0, totalPages-1);
                        } else {
                            System.out.println("Opcao invalida");
                        }
                    } catch(NumberFormatException e) {
                        System.out.println("Opcao invalida");
                    }
            }
                if(done) break;
        } while(true);
    }

    private void gerenciarCurso(Curso c) throws Exception {
        VisaoCurso v = new VisaoCurso();
        do {
            Console.clear();
            System.out.println("\nEntrePares 1.0");
            System.out.println("--------------");
            System.out.println("\n> Inicio > Meus Cursos > " + c.getNome() + "\n");
            v.mostraCurso(c);
            if(c.getEstado()==0)
                System.out.println("Este curso esta aberto para inscricoes!\n");
            else
                System.out.println("Este curso nao permite novas inscricoes.\n");

            System.out.println("(A) Gerenciar inscritos no curso");
            System.out.println("(B) Corrigir dados do curso");
            System.out.println("(C) Encerrar inscricoes");
            System.out.println("(D) Concluir curso");
            System.out.println("(E) Cancelar curso");
            System.out.println("\n(R) Retornar ao menu anterior");
            System.out.print("\nOpcao: ");
            String op = console.nextLine();
            if(op.length()==0) continue;
            char ch = op.charAt(0);
            if(ch=='A' || ch=='a') {
                Console.clear();
                // Gerenciar inscritos no curso
                Inscricao[] inscritos = arqInscricoes.readByCurso(c.getID());
                if(inscritos.length==0) {
                    System.out.println("Nenhum inscrito neste curso.");
                } else {
                    for(int i=0;i<inscritos.length;i++) {
                        Inscricao ins = inscritos[i];
                        Usuario u = arqUsuarios.read(ins.getIdUsuario());
                        String nome = (u==null)?"[Usuario removido]":u.getNome();
                        System.out.println("(" + (i+1) + ") " + nome + " (" + ins.getDataInscricao() + ")");
                    }
                    System.out.println();
                    System.out.println("(A) Exportar lista");
                    System.out.println("\n(R) Retornar ao menu anterior");
                    System.out.print("\nOpcao: ");
                    String opIns = console.nextLine();
                    if(opIns.length()==0) continue;
                    char chIns = opIns.charAt(0);
                    if(chIns=='A' || chIns=='a') {
                        // export CSV to dados/inscricao
                        try {
                            java.io.File dir = new java.io.File("./dados/inscricao");
                            if(!dir.exists()) dir.mkdirs();
                            String fileName = "./dados/inscricao/inscritos_" + c.getID() + ".csv";
                            PrintWriter pw = new PrintWriter(new FileWriter(fileName));
                            pw.println("nome,email");
                            for(Inscricao ins : inscritos) {
                                Usuario u = arqUsuarios.read(ins.getIdUsuario());
                                if(u!=null) pw.println(u.getNome() + "," + u.getEmail());
                            }
                            pw.close();
                            System.out.println("Lista exportada para: " + fileName);
                        } catch(Exception e) {
                            System.out.println("Erro ao exportar lista: " + e.getMessage());
                        }
                    } else if(chIns=='R' || chIns=='r') {
                        // volta ao menu do curso
                        continue;
                    } else {
                        // try select a usuario to view/cancel
                        try {
                            int sel = Integer.parseInt(opIns);
                            if(sel>=1 && sel<=inscritos.length) {
                                Inscricao insSel = inscritos[sel-1];
                                Usuario u = arqUsuarios.read(insSel.getIdUsuario());
                                if(u==null) {
                                    System.out.println("Usuario nao encontrado");
                                } else {
                                    System.out.println("NOME...: " + u.getNome());
                                    System.out.println("EMAIL..: " + u.getEmail());
                                }
                                System.out.println("(A) Cancelar inscricao deste usuario");
                                System.out.println("(R) Retornar ao menu anterior");
                                System.out.print("Opcao: ");
                                String op2 = console.nextLine();
                                if(op2.length()==0) continue;
                                char ch2 = op2.charAt(0);
                                if(ch2=='A' || ch2=='a') {
                                    if(arqInscricoes.delete(insSel.getID()))
                                        System.out.println("Inscricao cancelada com sucesso.");
                                    else
                                        System.out.println("Erro ao cancelar inscricao.");
                                } else if(ch2=='R' || ch2=='r') {
                                    // volta para lista de inscritos
                                    continue;
                                }
                            }
                        } catch(NumberFormatException e) {
                            // ignore
                        }
                    }
                }
            } else if(ch=='B' || ch=='b') {
                // corrigir dados
                System.out.println("Altere os dados. Deixe em branco para manter.");
                System.out.print("Nome: ");
                String nome = console.nextLine();
                if(nome.length()>0) c.setNome(nome);
                System.out.print("Data de inicio (dd/mm/aaaa): ");
                String data = console.nextLine();
                if(data.length()>0) {
                    try {
                        String[] p = data.split("/");
                        c.setDataInicio(java.time.LocalDate.of(Integer.parseInt(p[2]), Integer.parseInt(p[1]), Integer.parseInt(p[0])));
                    } catch(Exception e) {
                        System.out.println("Data invalida, mantendo valor anterior.");
                    }
                }
                System.out.print("Descricao: ");
                String desc = console.nextLine();
                if(desc.length()>0) c.setDescricao(desc);

                System.out.print("\nConfirma alteracao (S/N)? ");
                String conf = console.nextLine();
                if(conf.length()>0 && (conf.charAt(0)=='S' || conf.charAt(0)=='s')) {
                    if(arqCursos.update(c))
                        System.out.println("Curso atualizado!");
                    else
                        System.out.println("Erro ao atualizar curso");
                }

            } else if(ch=='C' || ch=='c') {
                c.setEstado((byte)1);
                arqCursos.update(c);
                System.out.println("Inscricoes encerradas para este curso.");
            } else if(ch=='D' || ch=='d') {
                c.setEstado((byte)2);
                arqCursos.update(c);
                System.out.println("Curso marcado como concluido.");
            } else if(ch=='E' || ch=='e') {
                // Cancelar curso: se nao houver inscritos, excluir; caso contrario marcar como cancelado
                Inscricao[] inscritos = arqInscricoes.readByCurso(c.getID());
                if(inscritos.length==0) {
                    System.out.print("Nao ha inscritos. Confirma exclusao do curso (S/N)? ");
                    String confEx = console.nextLine();
                    if(confEx.length()>0 && (confEx.charAt(0)=='S' || confEx.charAt(0)=='s')) {
                        boolean ok = arqCursos.delete(c.getID());
                        if(ok) {
                            System.out.println("Curso excluido!");
                            return;
                        } else {
                            System.out.println("Erro na exclusao do curso!");
                        }
                    }
                } else {
                    c.setEstado((byte)3);
                    arqCursos.update(c);
                    System.out.println("Curso marcado como cancelado.");
                }
            } else if(ch=='R' || ch=='r') {
                break;
            } else {
                System.out.println("Opcao invalida");
            }

            // reload course from storage in case of updates
            c = arqCursos.read(c.getID());
        } while(true);
    }

    public void close() throws Exception {
        arqCursos.close();
        try { if(arqInscricoes!=null) arqInscricoes.close(); } catch(Exception e) {}
        try { if(arqUsuarios!=null) arqUsuarios.close(); } catch(Exception e) {}
    }

}
