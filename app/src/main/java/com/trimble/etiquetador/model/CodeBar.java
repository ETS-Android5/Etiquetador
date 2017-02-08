package com.trimble.etiquetador.model;

/**
 * Created by jorge on 19/1/17.
 */
public class CodeBar {
    private String code;
    private String estado;

    public CodeBar(String code, String estado){
        this.code=code;
        this.estado=estado;
    }

    public void setCode(String code){
        this.code=code;
    }

    public void setEstado(String estado){
        this.estado=estado;
    }

    public String getCode(){
        return code;
    }

    public String getEstado(){ return estado;}

}
