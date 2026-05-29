package entidades.cursos;

import aed3.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.text.Normalizer;
import java.io.File;

public class ArquivoCurso extends Arquivo<Curso> {
    
    HashExtensivel<ParCodigoId> indiceCodigo;
    ArvoreBMais<ParNomeId> indiceNome;
    ArvoreBMais<ParIdId> indiceUsuarioCurso;
    ListaInvertida indiceInvertido;

    private static final Set<String> STOPWORDS = new HashSet<String>() {{
        String[] words = new String[] {
            "a","o","as","os","um","uma","uns","umas",
            "de","do","da","dos","das","em","no","na","nos","nas",
            "para","por","e","com","sem","ao","aos","como","que",
            "quem","onde","quando","se","sao","ser","ter","sob","sobre",
            "entre","ate","ou","mas","so","sua","seu","seus","suas",
            "num","numa","nao","naõ","não","pelo","pela","pelos","pelas",
            "este","esta","estes","estas","aquele","aquela","aqueles","aquelas",
            "isto","isso","aquilo","me","mim","comigo","te","ti","contigo",
            "lhe","lhes","nos","vos","eles","elas","ele","ela","eu","tu",
            "por","sobre","entre","sob","através","apos","antes","durante",
            "quando","onde","porque","porquê","porq","peloque","como","qual","quais",
            "seu","sua","meu","minha","nosso","nossa","vosso","vossa",
            "já","ja","ainda","tambem","também","mais","menos","muito","muitos","muita","muitas",
            "cada","todo","todos","toda","todas","algum","alguma","alguns","algumas","nenhum","nenhuma",
            "haja","ha","houve","foi","era","são","sera","será","está","esta","estao","estavam",
            "comigo","consigo","sem","sob","per","pela","pelas","pelos","ao","aos",
            "dos","das","num","numa","nos","na","no","nao","sim","nao","não",
            "-",""};
        for(String w : words) add(w);
    }};

    public ArquivoCurso() throws Exception {
        super("curso", Curso.class.getConstructor());
        indiceCodigo = new HashExtensivel<>(
            ParCodigoId.class.getConstructor(),
            4, 
           "./dados/curso/indiceCodigo.d.db",
           "./dados/curso/indiceCodigo.c.db");
        indiceNome = new ArvoreBMais<>(
            ParNomeId.class.getConstructor(),
            4,
           "./dados/curso/indiceNome.db");
        // se o indiceNome estiver em formato antigo/incompatível, reconstruir
        try {
            indiceNome.read(null);
        } catch(Exception e) {
            try { indiceNome.close(); } catch(Exception ex) {}
            File f = new File("./dados/curso/indiceNome.db");
            if(f.exists()) f.renameTo(new File("./dados/curso/indiceNome.db.bak"));
            indiceNome = new ArvoreBMais<>(ParNomeId.class.getConstructor(), 4, "./dados/curso/indiceNome.db");
            // rebuild: iterate over stored cursos
            int last = super.getLastID();
            for(int id=1; id<=last; id++) {
                Curso cur = super.read(id);
                if(cur!=null) {
                    try { indiceNome.create(new ParNomeId(cur.getNome(), cur.getID())); } catch(Exception ex) {}
                }
            }
        }
        indiceUsuarioCurso = new ArvoreBMais<>(
            ParIdId.class.getConstructor(),
            4, "./dados/curso/indiceUsuarioCurso.db");
        // criar pasta do índice invertido (dados/listainv)
        File dir = new File("./dados/listainv");
        if(!dir.exists()) dir.mkdirs();
        // backup e reconstrução completa do índice invertido para garantir consistência
        try {
            File dict = new File("./dados/listainv/indiceInvertido.d.db");
            File blocks = new File("./dados/listainv/indiceInvertido.c.db");
            // se existirem arquivos antigos em listainv, faz backup
            if(dict.exists()) dict.renameTo(new File("./dados/listainv/indiceInvertido.d.db.bak"));
            if(blocks.exists()) blocks.renameTo(new File("./dados/listainv/indiceInvertido.c.db.bak"));
        } catch(Exception e) {
            // ignore backup failures
        }
        indiceInvertido = new ListaInvertida(4, "./dados/listainv/indiceInvertido.d.db", "./dados/listainv/indiceInvertido.c.db");
        try {
            Curso[] all = readAll();
            for(Curso c : all) {
                Map<String, Float> tfmap = computeTF(c.getNome());
                for(Map.Entry<String, Float> en : tfmap.entrySet()) {
                    try { indiceInvertido.create(en.getKey(), new ElementoLista(c.getID(), en.getValue())); } catch(Exception ex) {}
                }
            }
        } catch(Exception e) {
            // se falhar, índice ficará vazio e será preenchido por futuras criações/atualizações
        }
    }

    @Override
    public int create(Curso c) throws Exception {
        // garante código único
        if(c.getCodigoCompartilhavel() == null || c.getCodigoCompartilhavel().length()==0) {
            String codigo;
            do {
                codigo = Curso.gerarCodigoCompartilhavel();
            } while(indiceCodigo.read(Math.abs(codigo.hashCode()))!=null);
            c.setCodigoCompartilhavel(codigo);
        } else {
            while(indiceCodigo.read(Math.abs(c.getCodigoCompartilhavel().hashCode()))!=null) {
                c.setCodigoCompartilhavel(Curso.gerarCodigoCompartilhavel());
            }
        }

        int id = super.create(c);
        indiceCodigo.create(new ParCodigoId(c.getCodigoCompartilhavel(), id));
        indiceNome.create(new ParNomeId(c.getNome(), id));
        indiceUsuarioCurso.create(new ParIdId(c.getIdUsuario(), id));
        // atualiza indice invertido: palavras do nome
        Map<String, Float> tf = computeTF(c.getNome());
        for(Map.Entry<String, Float> en : tf.entrySet()) {
            try { indiceInvertido.create(en.getKey(), new ElementoLista(id, en.getValue())); } catch(Exception ex) {}
        }
        return id;
    }

    public Curso readCodigo(String codigo) throws Exception {
        ParCodigoId pci = indiceCodigo.read(Math.abs(codigo.hashCode()));
        if(pci == null)
            return null;
        Curso c = read(pci.getId());
        return c;
    }

    public Curso[] readNome(String nome) throws Exception {
        ArrayList<ParNomeId> pnis = indiceNome.read(new ParNomeId(nome,-1));  
        if(pnis.isEmpty())
            return new Curso[0];

        Curso[] cursos = new Curso[pnis.size()];
        int i=0;
        for (ParNomeId pni : pnis) {
            cursos[i++] = super.read(pni.getId());            
        }
        return cursos;
    }

    public Curso[] readUsuario(int idUsuario) throws Exception {
        // Retorna cursos do usuário ordenados alfabeticamente pelo nome
        ArrayList<ParNomeId> pnis = indiceNome.read(null);
        if(pnis.isEmpty())
            return new Curso[0];

        ArrayList<Curso> lista = new ArrayList<>();
        for(ParNomeId pni : pnis) {
            Curso c = super.read(pni.getId());
            if(c!=null && c.getIdUsuario()==idUsuario)
                lista.add(c);
        }
        Curso[] resultado = new Curso[lista.size()];
        lista.toArray(resultado);
        return resultado;
    }

    public Curso[] readAll() throws Exception {
        ArrayList<ParNomeId> pnis = indiceNome.read(null);
        if(pnis.isEmpty())
            return new Curso[0];

        Curso[] cursos = new Curso[pnis.size()];
        int i=0;
        for (ParNomeId pni : pnis) {
            cursos[i++] = super.read(pni.getId());            
        }
        return cursos;
    }

    public Curso[] readAllByDate() throws Exception {
        ArrayList<ParNomeId> pnis = indiceNome.read(null);
        if(pnis.isEmpty())
            return new Curso[0];

        ArrayList<Curso> lista = new ArrayList<>();
        for (ParNomeId pni : pnis) {
            Curso c = super.read(pni.getId());
            if(c!=null) lista.add(c);
        }
        Collections.sort(lista, new Comparator<Curso>() {
            public int compare(Curso a, Curso b) {
                return a.getDataInicio().compareTo(b.getDataInicio());
            }
        });
        Curso[] cursos = new Curso[lista.size()];
        lista.toArray(cursos);
        return cursos;
    }
    
    @Override
    public boolean delete(int id) throws Exception {
        Curso c = read(id);
        if(c!=null)
            if(super.delete(id)) {
                indiceCodigo.delete(Math.abs(c.getCodigoCompartilhavel().hashCode()));
                indiceNome.delete(new ParNomeId(c.getNome(), c.getID()));
                indiceUsuarioCurso.delete(new ParIdId(c.getIdUsuario(), c.getID()));
                // remove do indice invertido
                Map<String, Float> tf = computeTF(c.getNome());
                for(String termo : tf.keySet()) {
                    try { indiceInvertido.delete(termo, id); } catch(Exception ex) {}
                }
                return true;
            }
        return false;
    }

    @Override
    public boolean update(Curso novoCurso) throws Exception {
        Curso c = read(novoCurso.getID());
        if(c==null)
            return false;
        if(super.update(novoCurso)) {
            if(c.getCodigoCompartilhavel().compareTo(novoCurso.getCodigoCompartilhavel())!=0) {
                indiceCodigo.delete(Math.abs(c.getCodigoCompartilhavel().hashCode()));
                indiceCodigo.create(new ParCodigoId(novoCurso.getCodigoCompartilhavel(), novoCurso.getID()));
            }
            if(c.getNome().compareTo(novoCurso.getNome())!=0) {
                indiceNome.delete(new ParNomeId(c.getNome(), c.getID()));
                indiceNome.create(new ParNomeId( novoCurso.getNome(), novoCurso.getID()));
                // atualizar indice invertido: comparar termos antigos e novos
                Map<String, Float> antigos = computeTF(c.getNome());
                Map<String, Float> novos = computeTF(novoCurso.getNome());
                // remover termos que ficaram
                for(String t : antigos.keySet()) {
                    if(!novos.containsKey(t)) {
                        try { indiceInvertido.delete(t, novoCurso.getID()); } catch(Exception ex) {}
                    }
                }
                // adicionar / atualizar termos novos
                for(Map.Entry<String, Float> en : novos.entrySet()) {
                    String t = en.getKey();
                    float tfv = en.getValue();
                    try {
                        ElementoLista el = indiceInvertido.read(t, novoCurso.getID());
                        if(el==null) {
                            indiceInvertido.create(t, new ElementoLista(novoCurso.getID(), tfv));
                        } else {
                            el.setFrequencia(tfv);
                            indiceInvertido.update(t, el);
                        }
                    } catch(Exception ex) {}
                }
            }
            if(c.getIdUsuario() != novoCurso.getIdUsuario()) {
                indiceUsuarioCurso.delete(new ParIdId(c.getIdUsuario(), c.getID()));
                indiceUsuarioCurso.create(new ParIdId(novoCurso.getIdUsuario(), novoCurso.getID()));
            }
            return true;
        }
        return false;
    }


    public void close() throws Exception {
        super.close();
        indiceCodigo.close();
        indiceNome.close();
        indiceUsuarioCurso.close();
        try { if(indiceInvertido!=null) indiceInvertido.close(); } catch(Exception e) {}
    }

    private Map<String, Float> computeTF(String texto) {
        Map<String, Integer> counts = new HashMap<>();
        if(texto==null) return new HashMap<String, Float>();
        // normalizar: remover acentos, toLower, manter letras e numeros
        String s = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        s = s.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        String[] parts = s.split("\\s+");
        int total = 0;
        for(String p : parts) {
            if(p==null) continue;
            p = p.trim();
            if(p.length()==0) continue;
            if(STOPWORDS.contains(p)) continue;
            String stem = stemWord(p);
            if(stem.length()==0) continue;
            if(STOPWORDS.contains(stem)) continue;
            counts.put(stem, counts.getOrDefault(stem, 0) + 1);
            total++;
        }
        Map<String, Float> tf = new HashMap<>();
        if(total==0) return tf;
        for(Map.Entry<String, Integer> en : counts.entrySet()) {
            tf.put(en.getKey(), ((float)en.getValue())/((float)total));
        }
        return tf;
    }

    // Simple Portuguese stemmer / lemmatizer (heuristic)
    private String stemWord(String w) {
        if(w==null) return "";
        String s = w;
        // if token is numeric, keep as is
        if(s.matches("\\d+")) return s;
        // remove common plural endings
        if(s.endsWith("ões")) s = s.substring(0, s.length()-3) + "ao"; // aceitacao -> aceitacao
        if(s.endsWith("oes")) s = s.substring(0, s.length()-3) + "ao";
        // common suffixes to strip
        String[] suffixes = new String[]{"amentos","imento","mente","acao","acoes","idade","idades","ismo","ismos","ista","istas","izar","izacao","izar","izar","ivo","iva","ivos","ivas","mente"};
        for(String suf : suffixes) {
            if(s.length()> (suf.length()+2) && s.endsWith(suf)) {
                s = s.substring(0, s.length()-suf.length());
                break;
            }
        }
        // remove plural 's' or 'es'
        if(s.endsWith("ses") && s.length()>4) { s = s.substring(0, s.length()-2); }
        if(s.endsWith("es") && s.length()>3) s = s.substring(0, s.length()-2);
        else if(s.endsWith("s") && s.length()>3) s = s.substring(0, s.length()-1);
        // remove infinitive endings if present
        if((s.endsWith("ar") || s.endsWith("er") || s.endsWith("ir")) && s.length()>4) s = s.substring(0, s.length()-2);
        return s;
    }

    // Busca por palavras-chave usando TFxIDF
    public Curso[] searchByKeywords(String consulta) throws Exception {
        Map<Integer, Float> scores = new HashMap<>();
        Map<String, Float> termos = computeTF(consulta);
        if(termos.isEmpty()) return new Curso[0];
        Curso[] todos = readAll();
        int N = todos.length;
        if(N==0) return new Curso[0];
        for(String termo : termos.keySet()) {
            ElementoLista[] lista = indiceInvertido.read(termo);
            if(lista.length==0) continue;
            int df = lista.length;
            double idf = Math.log10((double)N / (double)df) + 1.0;
            for(ElementoLista el : lista) {
                float tf = el.getFrequencia();
                float tfidf = (float)(tf * idf);
                scores.put(el.getId(), scores.getOrDefault(el.getId(), 0f) + tfidf);
            }
        }
        if(scores.isEmpty()) return new Curso[0];
        // ordenar por score desc
        ArrayList<Map.Entry<Integer, Float>> list = new ArrayList<>(scores.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Float>>() {
            public int compare(Map.Entry<Integer, Float> a, Map.Entry<Integer, Float> b) {
                return Float.compare(b.getValue(), a.getValue());
            }
        });
        ArrayList<Curso> resultado = new ArrayList<>();
        for(Map.Entry<Integer, Float> e : list) {
            Curso c = read(e.getKey());
            if(c!=null) resultado.add(c);
        }
        Curso[] arr = new Curso[resultado.size()];
        resultado.toArray(arr);
        return arr;
    }
}
