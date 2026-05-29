package entidades.cursos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.security.SecureRandom;

import aed3.InterfaceEntidade;

public class Curso implements InterfaceEntidade {

    private int idCurso;
    private String nome;
    private int dataInicioEpoch;
    private String descricao;
    private String codigoCompartilhavel;
    private byte estado; // 0: aberto, 1: sem novas inscrições, 2: concluído, 3: cancelado
    private int idUsuario; // dono

    public Curso() {
        this(-1, "", (int)java.time.LocalDate.now().toEpochDay(), "", "", (byte)0, -1);
    }

    public Curso(String nome, java.time.LocalDate dataInicio, String descricao, int idUsuario) {
        this(-1, nome, (int)dataInicio.toEpochDay(), descricao, gerarCodigoCompartilhavel(), (byte)0, idUsuario);
    }

    public Curso(int id, String nome, int dataInicioEpoch, String descricao, String codigo, byte estado, int idUsuario) {
        this.idCurso = id;
        this.nome = nome;
        this.dataInicioEpoch = dataInicioEpoch;
        this.descricao = descricao;
        this.codigoCompartilhavel = codigo;
        this.estado = estado;
        this.idUsuario = idUsuario;
    }

    public int getID() { return idCurso; }
    public void setID(int id) { this.idCurso = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public java.time.LocalDate getDataInicio() { return java.time.LocalDate.ofEpochDay(this.dataInicioEpoch); }
    public void setDataInicio(java.time.LocalDate dataInicio) { this.dataInicioEpoch = (int)dataInicio.toEpochDay(); }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCodigoCompartilhavel() { return codigoCompartilhavel; }
    public void setCodigoCompartilhavel(String codigo) { this.codigoCompartilhavel = codigo; }

    public byte getEstado() { return estado; }
    public void setEstado(byte estado) { this.estado = estado; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    @Override
    public String toString() {
        String estadoStr = "";
        switch(estado) {
            case 0: estadoStr = "Aberto para inscricoes"; break;
            case 1: estadoStr = "Ativo (sem novas inscricoes)"; break;
            case 2: estadoStr = "Concluido"; break;
            case 3: estadoStr = "Cancelado"; break;
        }
         return "CODIGO........: " + codigoCompartilhavel +
             "\nNOME..........: " + nome +
             "\nDESCRICAO.....: " + descricao +
             "\nDATA DE INICIO: " + getDataInicio() +
             "\nESTADO........: " + estadoStr + "\n";
    }

    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(idCurso);
        dos.writeUTF(nome);
        dos.writeInt(this.dataInicioEpoch);
        dos.writeUTF(descricao);
        dos.writeUTF(codigoCompartilhavel);
        dos.writeByte(estado);
        dos.writeInt(idUsuario);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] vb) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        idCurso = dis.readInt();
        nome = dis.readUTF();
        dataInicioEpoch = dis.readInt();
        descricao = dis.readUTF();
        codigoCompartilhavel = dis.readUTF();
        estado = dis.readByte();
        idUsuario = dis.readInt();
    }

    public static String gerarCodigoCompartilhavel() {
        final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

}
