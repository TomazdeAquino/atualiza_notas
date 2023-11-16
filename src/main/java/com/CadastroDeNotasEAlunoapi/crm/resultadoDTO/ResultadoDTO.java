package com.CadastroDeNotasEAlunoapi.crm.resultadoDTO;

public class ResultadoDTO {

    private Double media;
    private String situacao;

    public ResultadoDTO() {
        // Construtor padrão
    }

    public ResultadoDTO(Double media, String situacao) {
        this.media = media;
        this.situacao = situacao;
    }

    public Double getMedia() {
        return media;
    }

    public void setMedia(Double media) {
        this.media = media;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

}
