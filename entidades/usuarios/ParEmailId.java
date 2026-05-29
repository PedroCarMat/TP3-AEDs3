package entidades.usuarios;

import aed3.InterfaceHashExtensivel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParEmailId implements InterfaceHashExtensivel {

    private String email;  // chave
    private int id;        // valor
    private final short TAMANHO = 64;  // 60 bytes email + 4 bytes id

    public ParEmailId() throws Exception {
        this.email = "";
        this.id = -1;
    }

    public ParEmailId(String email, int id) throws Exception {
        if(email.getBytes().length>60)
            throw new Exception("Email extenso demais. Diminua o número de caracteres.");
        this.email = email;
        this.id = id;
    }

    public String getEmail() { return email; }
    public int getId() { return id; }

    @Override
    public int hashCode() {
        return Math.abs(this.email.hashCode());
    }

    public short size() {
        return this.TAMANHO;
    }

    public String toString() {
        return "("+this.email + ";" + this.id+")";
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        byte[] vb = new byte[60];
        byte[] vbEmail = this.email.getBytes();
        int i=0;
        while(i<vbEmail.length && i<60) {
            vb[i] = vbEmail[i];
            i++;
        }
        while(i<60) {
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
        byte[] vb = new byte[60];
        dis.read(vb);
        this.email = (new String(vb)).trim();
        this.id = dis.readInt();
    }

}
