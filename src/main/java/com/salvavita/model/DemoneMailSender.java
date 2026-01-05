package com.salvavita.model;

import java.time.LocalDateTime;

/**
 * Modello per i demoni mail sender
 */
public class DemoneMailSender {

    private String ente;
    private String codiNome;
    private Integer isWorking;
    private LocalDateTime dateStartWorking;
    private LocalDateTime dateStopWorking;

    // Costruttore vuoto
    public DemoneMailSender() {
    }

    // Getters e Setters
    public String getEnte() {
        return ente;
    }

    public void setEnte(String ente) {
        this.ente = ente;
    }

    public String getCodiNome() {
        return codiNome;
    }

    public void setCodiNome(String codiNome) {
        this.codiNome = codiNome;
    }

    public Integer getIsWorking() {
        return isWorking;
    }

    public void setIsWorking(Integer isWorking) {
        this.isWorking = isWorking;
    }

    public LocalDateTime getDateStartWorking() {
        return dateStartWorking;
    }

    public void setDateStartWorking(LocalDateTime dateStartWorking) {
        this.dateStartWorking = dateStartWorking;
    }

    public LocalDateTime getDateStopWorking() {
        return dateStopWorking;
    }

    public void setDateStopWorking(LocalDateTime dateStopWorking) {
        this.dateStopWorking = dateStopWorking;
    }

    @Override
    public String toString() {
        return "DemoneMailSender{" +
                "ente='" + ente + '\'' +
                ", codiNome='" + codiNome + '\'' +
                ", isWorking=" + isWorking +
                ", dateStartWorking=" + dateStartWorking +
                ", dateStopWorking=" + dateStopWorking +
                '}';
    }
}
