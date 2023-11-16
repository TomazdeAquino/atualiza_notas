package com.CadastroDeNotasEAlunoapi.crm.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import com.CadastroDeNotasEAlunoapi.crm.resultadoDTO.ResultadoDTO;


@Entity
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String matricula;

    @Column
    private Double notaN1;

    @Column
    private Double notaN2;
    
    private Double media;
    private String situacao;
    
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
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

    public ResultadoDTO calcularMediaEAtualizarStatus() {
        // Calcular a média
        Double media = calcularMedia();

        // Determinar a situação
        String situacao = (media != null && media >= 6) ? "Aprovado" : "Reprovado";

        // Retornar o resultado
        return new ResultadoDTO(media, situacao);
    }

    private Double calcularMedia() {
        // Implementar o cálculo da média com as notas
        if (notaN1 == null || notaN2 == null) {
            // Se alguma nota for nula, a média também é nula
            return null;
        }
        return (notaN1 + notaN2) / 2;
    }
      

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Aluno other = (Aluno) obj;
        return Objects.equals(id, other.id);
    }
}
