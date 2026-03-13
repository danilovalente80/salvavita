package com.salvavita.model;

public enum Ente {
    ENTRATE("ENTRATE", "ENTR_ASP"),
    DEMANIO("DEMANIO", "DEM_ASP"),
    AAMS("AAMS", "AAMS_ASP"),
    SOGEI("SOGEI", "SOGEI_ASP"),
    ADER("ADER", "ADER_ASP"),
    ACN("ACN", "ACN_ASP"),
    EQUI("EQUI", "EQUI_ASP"),
    CONSIP("CONSIP", "CONSIP_ASP"),
    DPF("DPF", "DPF_ASP");

    private final String nome;
    private final String schema;

    Ente(String nome, String schema) {
        this.nome = nome;
        this.schema = schema;
    }

    public String getNome() {
        return nome;
    }

    public String getSchema() {
        return schema;
    }

    /**
     * Ritorna l'enum da un nome ente
     */
    public static Ente fromNome(String nome) {
        if (nome == null) {
            return null;
        }
        for (Ente e : Ente.values()) {
            if (e.nome.equalsIgnoreCase(nome)) {
                return e;
            }
        }
        return null;
    }
}