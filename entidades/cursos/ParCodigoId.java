package entidades.cursos;

import aed3.InterfaceHashExtensivel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParCodigoId implements InterfaceHashExtensivel {
    
    private String codigo;  // chave
    private int id;      // valor
    private final short TAMANHO = 14;  // 10 bytes codigo + 4 bytes id

    public ParCodigoId() throws Exception {
        this.codigo = "";
        this.id = -1;
    }

    public ParCodigoId(String codigo, int id) throws Exception {
        if(codigo.getBytes().length>10)
            throw new Exception("Código inválido");
        this.codigo = codigo;
        this.id = id;
    }

    public String getCodigo() { return codigo; }
    public int getId() { return id; }

    @Override
    public int hashCode() {
        return Math.abs(this.codigo.hashCode());
    }

    public short size() {
        return this.TAMANHO;
    }

    public String toString() {
        return "("+this.codigo + ";" + this.id+")";
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        byte[] vb = new byte[10];
        byte[] vbCodigo = this.codigo.getBytes();
        int i=0;
        while(i<vbCodigo.length && i<10) {
            vb[i] = vbCodigo[i];
            i++;
        }
        while(i<10) {
            vb[i] = ' ';
            i++;
        }
        dos.write(vb);
        dos.writeInt(this.id);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        byte[] vb = new byte[10];
        dis.read(vb);
        this.codigo = (new String(vb)).trim();
        this.id = dis.readInt();
    }

}
