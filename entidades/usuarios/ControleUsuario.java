package entidades.usuarios;

import aed3.Console;
import java.util.Scanner;

import entidades.cursos.ArquivoCurso;
import entidades.cursos.Curso;

public class ControleUsuario {

    ArquivoUsuario arqUsuarios;
    ArquivoCurso arqCursos;
    Scanner console;
    private String ultimaMensagem = null;

    public ControleUsuario() throws Exception {
        arqUsuarios = new ArquivoUsuario();
        arqCursos = new ArquivoCurso();
        console = new Scanner(System.in);
    }

    public int login() throws Exception {
        Console.clear();
        System.out.print("Email: ");
        String email = console.nextLine();
        if(email.length()==0)
            return -1;
        System.out.print("Senha: ");
        String senha = console.nextLine();
        Usuario u = arqUsuarios.readEmail(email);
        if(u==null)
            return -1;
        String hash = VisaoUsuario.hash(senha);
        if(hash.equals(u.getHashSenha()))
            return u.getID();
        return -1;
    }

    public void cadastrar() throws Exception {
        Console.clear();
        VisaoUsuario v = new VisaoUsuario();
        Usuario u = v.leUsuarioParaCadastro(console);
        if(u==null)
            return;
        arqUsuarios.create(u);
        System.out.println("Usuario cadastrado!");
    }

    public void recuperarSenha() throws Exception {
        Console.clear();
        System.out.print("Email: ");
        String email = console.nextLine();
        if(email.length()==0)
            return;
        Usuario u = arqUsuarios.readEmail(email);
        if(u==null) {
            System.out.println("Usuario nao encontrado!");
            return;
        }
        String pergunta = u.getPerguntaSecreta();
        if(pergunta==null || pergunta.length()==0) {
            System.out.println("Nenhuma pergunta secreta cadastrada para este usuario.");
            return;
        }
        System.out.println("Pergunta: " + pergunta);
        System.out.print("Resposta: ");
        String resposta = console.nextLine();
        String hashResp = VisaoUsuario.hash(resposta);
        if(hashResp.equals(u.getHashRespostaSecreta())) {
            System.out.print("Nova senha: ");
            String nova = console.nextLine();
            if(nova.length()==0) {
                System.out.println("Senha vazia. Operacao cancelada.");
                return;
            }
            u.setHashSenha(VisaoUsuario.hash(nova));
            if(arqUsuarios.update(u))
                System.out.println("Senha atualizada com sucesso.");
            else
                System.out.println("Erro ao atualizar a senha.");
        } else {
            System.out.println("Resposta incorreta.");
        }
    }

    public boolean meusDados(int userId) throws Exception {
        Usuario u = arqUsuarios.read(userId);
        if(u==null) {
            System.out.println("Usuario nao encontrado");
            return true;
        }

        VisaoUsuario v = new VisaoUsuario();
        String mensagemAviso = null;
        while(true) {
            Console.clear();
            if(mensagemAviso != null) {
                System.out.println(mensagemAviso);
                System.out.println();
                mensagemAviso = null;
            }
            v.mostraUsuario(u);
            System.out.println("\n(A) Atualizar dados");
            System.out.println("(D) Deletar usuario");
            System.out.println("(R) Retornar");
            System.out.print("\nOpcao: ");
            String op = console.nextLine();
            if(op.length()==0) continue;
            char ch = op.charAt(0);
            if(ch=='A' || ch=='a') {
                System.out.println("\nAltere os dados a seguir. Deixe o campo em branco quando nao quiser alterar.");
                System.out.print("Nome: ");
                String novoNome = console.nextLine();
                if(novoNome.length()>0) u.setNome(novoNome);
                System.out.print("Email: ");
                String novoEmail = console.nextLine();
                if(novoEmail.length()>0) u.setEmail(novoEmail);
                System.out.print("Senha: ");
                String novaSenha = console.nextLine();
                if(novaSenha.length()>0) u.setHashSenha(VisaoUsuario.hash(novaSenha));

                System.out.print("\nConfirma alteracao (S/N) ?");
                String confirma = console.nextLine();
                if(confirma.length()>0 && (confirma.charAt(0)=='S' || confirma.charAt(0)=='s')) {
                    if(arqUsuarios.update(u))
                        System.out.println("Usuario atualizado!");
                    else
                        System.out.println("Erro na alteracao!");
                }
                // reload
                u = arqUsuarios.read(userId);
            }
            else if(ch=='D' || ch=='d') {
                System.out.print("Confirma exclusao da sua conta (S/N)? ");
                String confirma = console.nextLine();
                if(confirma.length()>0 && (confirma.charAt(0)=='S' || confirma.charAt(0)=='s')) {
                    boolean ok = excluirUsuario(userId);
                    if(ok) {
                        // user deleted: return false to indicate logout required
                        return false;
                    } else {
                        mensagemAviso = this.ultimaMensagem;
                    }
                }
            }
            else if(ch=='R' || ch=='r') {
                return true;
            }
            else {
                System.out.println("Opcao invalida");
            }
        }
    }

    public boolean excluirUsuario(int userId) throws Exception {
        Usuario u = arqUsuarios.read(userId);
        if(u==null) {
            this.ultimaMensagem = "Usuario nao encontrado";
            return false;
        }
        Curso[] cursos = arqCursos.readUsuario(userId);
        if(cursos != null && cursos.length > 0) {
            this.ultimaMensagem = "Nao e possivel excluir este usuario. Existem cursos vinculados.";
            return false;
        }
        if(arqUsuarios.delete(userId)) {
            this.ultimaMensagem = "Usuario excluido!";
            return true;
        }
        else {
            this.ultimaMensagem = "Erro na exclusao!";
            return false;
        }
    }

    public void close() throws Exception {
        arqUsuarios.close();
        arqCursos.close();
    }

}
