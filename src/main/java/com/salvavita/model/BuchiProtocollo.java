package com.salvavita.model;

import java.time.LocalDateTime;

/**
 * Modello per i buchi di protocollo
 */
public class BuchiProtocollo {

    private String ente;
    private int count;
    private String errore;
    private Long minId;
    private String numeroProtocollo;
    private Long idAoo;
    private String codiceAoo;
    private LocalDateTime minDataIns;
    private LocalDateTime maxDataIns;

    // Costruttore vuoto
    public BuchiProtocollo() {
    }

    // Getters e Setters
    public String getEnte() {
        return ente;
    }

    public void setEnte(String ente) {
        this.ente = ente;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getErrore() {
        return errore;
    }

    public void setErrore(String errore) {
        this.errore = errore;
    }

    public Long getMinId() {
        return minId;
    }

    public void setMinId(Long minId) {
        this.minId = minId;
    }

    public String getNumeroProtocollo() {
        return numeroProtocollo;
    }

    public void setNumeroProtocollo(String numeroProtocollo) {
        this.numeroProtocollo = numeroProtocollo;
    }

    public Long getIdAoo() {
        return idAoo;
    }

    public void setIdAoo(Long idAoo) {
        this.idAoo = idAoo;
    }

    public String getCodiceAoo() {
        return codiceAoo;
    }

    public void setCodiceAoo(String codiceAoo) {
        this.codiceAoo = codiceAoo;
    }

    public LocalDateTime getMinDataIns() {
        return minDataIns;
    }

    public void setMinDataIns(LocalDateTime minDataIns) {
        this.minDataIns = minDataIns;
    }

    public LocalDateTime getMaxDataIns() {
        return maxDataIns;
    }

    public void setMaxDataIns(LocalDateTime maxDataIns) {
        this.maxDataIns = maxDataIns;
    }

    @Override
    public String toString() {
        return "BuchiProtocollo{" +
                "ente='" + ente + '\'' +
                ", count=" + count +
                ", errore='" + errore + '\'' +
                ", minId=" + minId +
                ", numeroProtocollo='" + numeroProtocollo + '\'' +
                ", idAoo=" + idAoo +
                ", codiceAoo='" + codiceAoo + '\'' +
                ", minDataIns=" + minDataIns +
                ", maxDataIns=" + maxDataIns +
                '}';
    }
}
