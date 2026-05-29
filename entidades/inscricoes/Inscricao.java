package entidades.inscricoes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import aed3.InterfaceEntidade;

public class Inscricao implements InterfaceEntidade {

    private int idInscricao;
    private int idCurso;
    private int idUsuario;
    private int dataInscricaoEpoch; // epoch day

    public Inscricao() {
        this(-1, -1, -1, (int)java.time.LocalDate.now().toEpochDay());
    }

    public Inscricao(int idCurso, int idUsuario) {
        this(-1, idCurso, idUsuario, (int)java.time.LocalDate.now().toEpochDay());
    }

    public Inscricao(int id, int idCurso, int idUsuario, int dataEpoch) {
        this.idInscricao = id;
        this.idCurso = idCurso;
        this.idUsuario = idUsuario;
        this.dataInscricaoEpoch = dataEpoch;
    }

    public int getID() { return idInscricao; }
    public void setID(int id) { this.idInscricao = id; }

    public int getIdCurso() { return idCurso; }
    public void setIdCurso(int idCurso) { this.idCurso = idCurso; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public java.time.LocalDate getDataInscricao() { return java.time.LocalDate.ofEpochDay(this.dataInscricaoEpoch); }
    public void setDataInscricao(java.time.LocalDate data) { this.dataInscricaoEpoch = (int)data.toEpochDay(); }

    @Override
    public String toString() {
        return "Inscricao("+idInscricao+";curso="+idCurso+";usuario="+idUsuario+";data="+getDataInscricao()+")";
    }

    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(idInscricao);
        dos.writeInt(idCurso);
        dos.writeInt(idUsuario);
        dos.writeInt(dataInscricaoEpoch);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] vb) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        idInscricao = dis.readInt();
        idCurso = dis.readInt();
        idUsuario = dis.readInt();
        dataInscricaoEpoch = dis.readInt();
    }

}
