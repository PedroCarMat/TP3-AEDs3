package entidades.inscricoes;

import aed3.*;
import java.util.ArrayList;

public class ArquivoInscricao extends Arquivo<Inscricao> {

    ArvoreBMais<ParIdId> indiceCursoInscricao; // (idCurso; idInscricao)
    ArvoreBMais<ParIdId> indiceUsuarioInscricao; // (idUsuario; idInscricao)

    public ArquivoInscricao() throws Exception {
        super("inscricao", Inscricao.class.getConstructor());
        indiceCursoInscricao = new ArvoreBMais<>(
            ParIdId.class.getConstructor(),
            4, "./dados/inscricao/indiceCursoInscricao.db");
        indiceUsuarioInscricao = new ArvoreBMais<>(
            ParIdId.class.getConstructor(),
            4, "./dados/inscricao/indiceUsuarioInscricao.db");
    }

    @Override
    public int create(Inscricao ins) throws Exception {
        int id = super.create(ins);
        indiceCursoInscricao.create(new ParIdId(ins.getIdCurso(), id));
        indiceUsuarioInscricao.create(new ParIdId(ins.getIdUsuario(), id));
        return id;
    }

    public Inscricao[] readByCurso(int idCurso) throws Exception {
        ArrayList<ParIdId> p = indiceCursoInscricao.read(new ParIdId(idCurso, -1));
        if(p.isEmpty()) return new Inscricao[0];
        Inscricao[] resultado = new Inscricao[p.size()];
        int i=0;
        for(ParIdId par : p) {
            resultado[i++] = super.read(par.getId2());
        }
        return resultado;
    }

    public Inscricao[] readByUsuario(int idUsuario) throws Exception {
        ArrayList<ParIdId> p = indiceUsuarioInscricao.read(new ParIdId(idUsuario, -1));
        if(p.isEmpty()) return new Inscricao[0];
        Inscricao[] resultado = new Inscricao[p.size()];
        int i=0;
        for(ParIdId par : p) {
            resultado[i++] = super.read(par.getId2());
        }
        return resultado;
    }

    public boolean exists(int idUsuario, int idCurso) throws Exception {
        ArrayList<ParIdId> p = indiceUsuarioInscricao.read(new ParIdId(idUsuario, -1));
        for(ParIdId par : p) {
            Inscricao ins = super.read(par.getId2());
            if(ins!=null && ins.getIdCurso()==idCurso) return true;
        }
        return false;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Inscricao ins = super.read(id);
        if(ins!=null) {
            if(super.delete(id)) {
                indiceCursoInscricao.delete(new ParIdId(ins.getIdCurso(), id));
                indiceUsuarioInscricao.delete(new ParIdId(ins.getIdUsuario(), id));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Inscricao nova) throws Exception {
        Inscricao antiga = super.read(nova.getID());
        if(antiga==null) return false;
        if(super.update(nova)) {
            if(antiga.getIdCurso() != nova.getIdCurso()) {
                indiceCursoInscricao.delete(new ParIdId(antiga.getIdCurso(), antiga.getID()));
                indiceCursoInscricao.create(new ParIdId(nova.getIdCurso(), nova.getID()));
            }
            if(antiga.getIdUsuario() != nova.getIdUsuario()) {
                indiceUsuarioInscricao.delete(new ParIdId(antiga.getIdUsuario(), antiga.getID()));
                indiceUsuarioInscricao.create(new ParIdId(nova.getIdUsuario(), nova.getID()));
            }
            return true;
        }
        return false;
    }

    public void close() throws Exception {
        super.close();
        indiceCursoInscricao.close();
        indiceUsuarioInscricao.close();
    }

}
