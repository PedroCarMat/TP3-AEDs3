package entidades.usuarios;

import java.security.MessageDigest;
import java.util.Scanner;

public class VisaoUsuario {

    public Usuario leUsuarioParaCadastro(Scanner console) throws Exception {
        System.out.println("CADASTRO DE USUARIO");
        System.out.print("Nome: ");
        String nome = console.nextLine();
        if(nome.length()==0)
            return null;

        String email = "";
        boolean valido = false;
        do {
            System.out.print("Email: ");
            email = console.nextLine();
            if(email.length()==0)
                return null;
                if(!email.contains("@")) {
                System.out.println("Email invalido!");
            } else {
                valido = true;
            }
        } while(!valido);

        System.out.print("Senha: ");
        String senha = console.nextLine();
        if(senha.length()==0)
            return null;

        System.out.print("Pergunta secreta (para recuperacao): ");
        String pergunta = console.nextLine();
        if(pergunta.length()==0)
            pergunta = "";
        System.out.print("Resposta secreta: ");
        String resposta = console.nextLine();
        if(resposta.length()==0)
            resposta = "";

        String hashSenha = hash(senha);
        String hashResposta = hash(resposta);

        Usuario u = new Usuario(-1, nome, email, hashSenha, pergunta, hashResposta);
        return u;
    }

    public void mostraUsuario(Usuario u) {
        System.out.println(u);
    }

    public static String hash(String s) throws Exception {
        if(s==null)
            return "";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = md.digest(s.getBytes("UTF-8"));
        return bytesToHex(bytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

}
