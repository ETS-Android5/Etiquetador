package com.trimble.etiquetador.model;


public class Cable {
    private String dimension;
    private String tagid;
    private String tipo;
    private String uso;
    private boolean escable;
    private String operadora;

    public Cable(String tagid, String tipo, String uso, boolean escable, String operadora, String dimension){
        this.tagid = tagid;
        this.tipo = tipo;
        this.uso = uso;
        this.escable = escable;
        this.operadora = operadora;
        this.dimension = dimension;
    }

    public String getDimension() {
        return dimension;
    }
    public boolean isEscable() {
        return escable;
    }

    public String getTagid() {
        return tagid;
    }

    public String getOperadora() {
        return operadora;
    }

    public String getTipo() {
        return tipo;
    }

    public String getUso() {
        return uso;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public void setEscable(boolean escable) {
        this.escable = escable;
    }

    public void setOperadora(String operadora) {
        this.operadora = operadora;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setUso(String uso) {
        this.uso = uso;
    }
}
