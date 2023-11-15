package org.example;

public class MachineComponents {
    private Boolean processador;
    private Integer idProcessador;
    private Boolean ram;
    private Integer idRam;
    private Boolean disco;
    private Integer idDisco;

    public MachineComponents(Boolean processador, Integer idProcessador, Boolean ram, Integer idRam, Boolean disco, Integer idDisco) {
        this.processador = processador;
        this.idProcessador = idProcessador;
        this.ram = ram;
        this.idRam = idRam;
        this.disco = disco;
        this.idDisco = idDisco;
    }

    public Boolean getProcessador() {
        return processador;
    }

    public void setProcessador(Boolean processador) {
        this.processador = processador;
    }

    public Integer getIdProcessador() {
        return idProcessador;
    }

    public void setIdProcessador(Integer idProcessador) {
        this.idProcessador = idProcessador;
    }

    public Boolean getRam() {
        return ram;
    }

    public void setRam(Boolean ram) {
        this.ram = ram;
    }

    public Integer getIdRam() {
        return idRam;
    }

    public void setIdRam(Integer idRam) {
        this.idRam = idRam;
    }

    public Boolean getDisco() {
        return disco;
    }

    public void setDisco(Boolean disco) {
        this.disco = disco;
    }

    public Integer getIdDisco() {
        return idDisco;
    }

    public void setIdDisco(Integer idDisco) {
        this.idDisco = idDisco;
    }
}
