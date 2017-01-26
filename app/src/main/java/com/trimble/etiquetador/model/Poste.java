package com.trimble.etiquetador.model;

public class Poste {
    private String codigo;
    private String sector;

    public Poste(String codigo, String sector){
        this.codigo = codigo;
        this.sector = sector;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getSector() {
        return sector;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }
}
