package com.salvavita.model;

import java.time.LocalDateTime;

public class ProtocolliSospesi {
    private String ente;
    private Long sequLongId;
    private Integer countRecuperiEjb;
    private Integer presaVisione;
    private Integer idTransizionePresente;
    private String aooUfficio;
    private String utenteCreatore;
    private LocalDateTime dataInserimento;
    private Integer statoDocumento;
    private Integer esitoDocumento;
    private String idAtmos;
    private String iChronicleId;
    private String errore;
    private String nomeDocumento;
    private Long seqDocumento;

    // COSTRUTTORE VUOTO
    public ProtocolliSospesi() {
    }

    // COSTRUTTORE CON PARAMETRI
    public ProtocolliSospesi(String ente, Long sequLongId, Integer countRecuperiEjb, 
                            Integer presaVisione, Integer idTransizionePresente, 
                            String aooUfficio, String utenteCreatore, 
                            LocalDateTime dataInserimento, Integer statoDocumento, 
                            Integer esitoDocumento, String idAtmos, String iChronicleId, 
                            String errore, String nomeDocumento, Long seqDocumento) {
        this.ente = ente;
        this.sequLongId = sequLongId;
        this.countRecuperiEjb = countRecuperiEjb;
        this.presaVisione = presaVisione;
        this.idTransizionePresente = idTransizionePresente;
        this.aooUfficio = aooUfficio;
        this.utenteCreatore = utenteCreatore;
        this.dataInserimento = dataInserimento;
        this.statoDocumento = statoDocumento;
        this.esitoDocumento = esitoDocumento;
        this.idAtmos = idAtmos;
        this.iChronicleId = iChronicleId;
        this.errore = errore;
        this.nomeDocumento = nomeDocumento;
        this.seqDocumento = seqDocumento;
    }

    // GETTER E SETTER
    public String getEnte() {
        return ente;
    }

    public void setEnte(String ente) {
        this.ente = ente;
    }

    public Long getSequLongId() {
        return sequLongId;
    }

    public void setSequLongId(Long sequLongId) {
        this.sequLongId = sequLongId;
    }

    public Integer getCountRecuperiEjb() {
        return countRecuperiEjb;
    }

    public void setCountRecuperiEjb(Integer countRecuperiEjb) {
        this.countRecuperiEjb = countRecuperiEjb;
    }

    public Integer getPresaVisione() {
        return presaVisione;
    }

    public void setPresaVisione(Integer presaVisione) {
        this.presaVisione = presaVisione;
    }

    public Integer getIdTransizionePresente() {
        return idTransizionePresente;
    }

    public void setIdTransizionePresente(Integer idTransizionePresente) {
        this.idTransizionePresente = idTransizionePresente;
    }

    public String getAooUfficio() {
        return aooUfficio;
    }

    public void setAooUfficio(String aooUfficio) {
        this.aooUfficio = aooUfficio;
    }

    public String getUtenteCreatore() {
        return utenteCreatore;
    }

    public void setUtenteCreatore(String utenteCreatore) {
        this.utenteCreatore = utenteCreatore;
    }

    public LocalDateTime getDataInserimento() {
        return dataInserimento;
    }

    public void setDataInserimento(LocalDateTime dataInserimento) {
        this.dataInserimento = dataInserimento;
    }

    public Integer getStatoDocumento() {
        return statoDocumento;
    }

    public void setStatoDocumento(Integer statoDocumento) {
        this.statoDocumento = statoDocumento;
    }

    public Integer getEsitoDocumento() {
        return esitoDocumento;
    }

    public void setEsitoDocumento(Integer esitoDocumento) {
        this.esitoDocumento = esitoDocumento;
    }

    public String getIdAtmos() {
        return idAtmos;
    }

    public void setIdAtmos(String idAtmos) {
        this.idAtmos = idAtmos;
    }

    public String getIChronicleId() {
        return iChronicleId;
    }

    public void setIChronicleId(String iChronicleId) {
        this.iChronicleId = iChronicleId;
    }

    public String getErrore() {
        return errore;
    }

    public void setErrore(String errore) {
        this.errore = errore;
    }

    public String getNomeDocumento() {
        return nomeDocumento;
    }

    public void setNomeDocumento(String nomeDocumento) {
        this.nomeDocumento = nomeDocumento;
    }

    public Long getSeqDocumento() {
        return seqDocumento;
    }

    public void setSeqDocumento(Long seqDocumento) {
        this.seqDocumento = seqDocumento;
    }

    // TOSTRING
    @Override
    public String toString() {
        return "ProtocolliSospesi{" +
                "ente='" + ente + '\'' +
                ", sequLongId=" + sequLongId +
                ", countRecuperiEjb=" + countRecuperiEjb +
                ", presaVisione=" + presaVisione +
                ", idTransizionePresente=" + idTransizionePresente +
                ", aooUfficio='" + aooUfficio + '\'' +
                ", utenteCreatore='" + utenteCreatore + '\'' +
                ", dataInserimento=" + dataInserimento +
                ", statoDocumento=" + statoDocumento +
                ", esitoDocumento=" + esitoDocumento +
                ", idAtmos='" + idAtmos + '\'' +
                ", iChronicleId='" + iChronicleId + '\'' +
                ", errore='" + errore + '\'' +
                ", nomeDocumento='" + nomeDocumento + '\'' +
                ", seqDocumento=" + seqDocumento +
                '}';
    }
}