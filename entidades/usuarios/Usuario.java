package entidades.usuarios;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import aed3.InterfaceEntidade;

public class Usuario implements InterfaceEntidade {

    private int idUsuario;
    private String nome;
    private String email;
    private String hashSenha;
    private String perguntaSecreta;
    private String hashRespostaSecreta;

    public Usuario() {
        this(-1, "", "", "", "", "");
    }

    public Usuario(String nome, String email, String hashSenha, String pergunta, String hashResposta) {
        this(-1, nome, email, hashSenha, pergunta, hashResposta);
    }

    public Usuario(int id, String nome, String email, String hashSenha, String pergunta, String hashResposta) {
        this.idUsuario = id;
        this.nome = nome;
        this.email = email;
        this.hashSenha = hashSenha;
        this.perguntaSecreta = pergunta;
        this.hashRespostaSecreta = hashResposta;
    }

    public int getID() { return idUsuario; }
    public void setID(int id) { this.idUsuario = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHashSenha() { return hashSenha; }
    public void setHashSenha(String hashSenha) { this.hashSenha = hashSenha; }

    public String getPerguntaSecreta() { return perguntaSecreta; }
    public void setPerguntaSecreta(String perguntaSecreta) { this.perguntaSecreta = perguntaSecreta; }

    public String getHashRespostaSecreta() { return hashRespostaSecreta; }
    public void setHashRespostaSecreta(String hashRespostaSecreta) { this.hashRespostaSecreta = hashRespostaSecreta; }

    @Override
    public String toString() {
        return "ID......: " + idUsuario +
               "\nNome....: " + nome +
               "\nEmail...: " + email +
               "\nPergunta: " + perguntaSecreta + "\n";
    }

    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(idUsuario);
        dos.writeUTF(nome);
        dos.writeUTF(email);
        dos.writeUTF(hashSenha);
        dos.writeUTF(perguntaSecreta);
        dos.writeUTF(hashRespostaSecreta);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] vb) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        idUsuario = dis.readInt();
        nome = dis.readUTF();
        email = dis.readUTF();
        hashSenha = dis.readUTF();
        perguntaSecreta = dis.readUTF();
        hashRespostaSecreta = dis.readUTF();
    }

}
