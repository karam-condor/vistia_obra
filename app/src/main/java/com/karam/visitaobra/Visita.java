package com.karam.visitaobra;

public class Visita {
    private String periodo,obs,fase_obra,isModified;

    public Visita(String periodo, String obs, String fase_obra,String isModified) {
        this.periodo = periodo;
        this.obs = obs;
        this.fase_obra = fase_obra;
        this.isModified = isModified;
    }

    public String getIsModified() {
        return isModified;
    }

    public void setIsModified(String isModified) {
        this.isModified = isModified;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getFase_obra() {
        return fase_obra;
    }

    public void setFase_obra(String fase_obra) {
        this.fase_obra = fase_obra;
    }


    public int getColumnCount() {
        return getClass().getDeclaredFields().length;
    }
}
