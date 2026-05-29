package entidades.usuarios;

import aed3.*;

import java.util.ArrayList;
import java.io.File;

public class ArquivoUsuario extends Arquivo<Usuario> {
    
    HashExtensivel<ParEmailId> indiceEmail;
    ArvoreBMais<ParNomeId> indiceNome;

    public ArquivoUsuario() throws Exception {
        super("usuario", Usuario.class.getConstructor());
        indiceEmail = new HashExtensivel<>(
            ParEmailId.class.getConstructor(),
            4,
           "./dados/usuario/indiceEmail.d.db",
           "./dados/usuario/indiceEmail.c.db");
        indiceNome = new ArvoreBMais<>(
            ParNomeId.class.getConstructor(),
            4,
           "./dados/usuario/indiceNome.db");
        // se o indiceNome estiver em formato antigo/incompatível, reconstruir
        try {
            indiceNome.read(null);
        } catch(Exception e) {
            try { indiceNome.close(); } catch(Exception ex) {}
            File f = new File("./dados/usuario/indiceNome.db");
            if(f.exists()) f.renameTo(new File("./dados/usuario/indiceNome.db.bak"));
            indiceNome = new ArvoreBMais<>(ParNomeId.class.getConstructor(), 4, "./dados/usuario/indiceNome.db");
            int last = super.getLastID();
            for(int id=1; id<=last; id++) {
                Usuario u = super.read(id);
                if(u!=null) {
                    try { indiceNome.create(new ParNomeId(u.getNome(), u.getID())); } catch(Exception ex) {}
                }
            }
        }
    }

    @Override
    public int create(Usuario u) throws Exception {
        int id = super.create(u);
        indiceEmail.create(new ParEmailId(u.getEmail(), id));
        indiceNome.create(new ParNomeId(u.getNome(), id));
        return id;
    }

    public Usuario readEmail(String email) throws Exception {
        ParEmailId pei = indiceEmail.read(Math.abs(email.hashCode()));
        if(pei==null)
            return null;
        Usuario u = read(pei.getId());
        return u;
    }

    public Usuario[] readNome(String nome) throws Exception {
        ArrayList<ParNomeId> pnis = indiceNome.read(new ParNomeId(nome,-1));
        if(pnis.isEmpty())
            return new Usuario[0];

        Usuario[] usuarios = new Usuario[pnis.size()];
        int i=0;
        for (ParNomeId pni : pnis) {
            usuarios[i++] = super.read(pni.getId());            
        }
        return usuarios;
    }

    public Usuario[] readAll() throws Exception {
        ArrayList<ParNomeId> pnis = indiceNome.read(null);
        if(pnis.isEmpty())
            return new Usuario[0];

        Usuario[] usuarios = new Usuario[pnis.size()];
        int i=0;
        for (ParNomeId pni : pnis) {
            usuarios[i++] = super.read(pni.getId());            
        }
        return usuarios;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Usuario u = read(id);
        if(u!=null)
            if(super.delete(id)) {
                indiceEmail.delete(Math.abs(u.getEmail().hashCode()));
                indiceNome.delete(new ParNomeId(u.getNome(), u.getID()));
                return true;
            }
        return false;
    }

    @Override
    public boolean update(Usuario nova) throws Exception {
        Usuario u = read(nova.getID());
        if(u==null)
            return false;
        if(super.update(nova)) {
            if(u.getEmail().compareTo(nova.getEmail())!=0) {
                indiceEmail.delete(Math.abs(u.getEmail().hashCode()));
                indiceEmail.create(new ParEmailId(nova.getEmail(), nova.getID()));
            }
            if(u.getNome().compareTo(nova.getNome())!=0) {
                indiceNome.delete(new ParNomeId(u.getNome(), u.getID()));
                indiceNome.create(new ParNomeId( nova.getNome(), nova.getID()));
            }
            return true;
        }
        return false;
    }


    public void close() throws Exception {
        super.close();
        indiceEmail.close();
        indiceNome.close();
    }
}
