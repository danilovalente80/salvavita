package com.salvavita.model;

/**
 * Modello per gli allineamenti processi
 */
public class AllineamentoProcesso {

    private int count;
    private String tipo;

    // Costruttore vuoto
    public AllineamentoProcesso() {
    }

    // Costruttore con parametri
    public AllineamentoProcesso(int count, String tipo) {
        this.count = count;
        this.tipo = tipo;
    }

    // Getters e Setters
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "AllineamentoProcesso{" +
                "count=" + count +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
