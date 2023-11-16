package com.CadastroDeNotasEAlunoapi.crm.dto;

public class CadastroNotas {

    private Double notaN1;
    private Double notaN2;

    public CadastroNotas() {
        // Construtor padrão
    }

    public CadastroNotas(Double notaN1, Double notaN2) {
        this.notaN1 = notaN1;
        this.notaN2 = notaN2;
    }

    public Double getNotaN1() {
        return notaN1;
    }

    public void setNotaN1(Double notaN1) {
        this.notaN1 = notaN1;
    }

    public Double getNotaN2() {
        return notaN2;
    }

    public void setNotaN2(Double notaN2) {
        this.notaN2 = notaN2;
    }
}
