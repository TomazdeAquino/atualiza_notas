package com.CadastroDeNotasEAlunoapi.crm.controller;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.CadastroDeNotasEAlunoapi.crm.model.Aluno;
import com.CadastroDeNotasEAlunoapi.crm.repository.AlunoRepository;
import com.CadastroDeNotasEAlunoapi.crm.Excecoes.Excecoes;
import com.CadastroDeNotasEAlunoapi.crm.resultadoDTO.ResultadoDTO;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    @Autowired
    private AlunoRepository alunoRepository;

    @GetMapping
    public ResponseEntity<?> listar() {
        List<Aluno> alunos = alunoRepository.findAll();
        alunos.forEach(this::atualizarMediaESituacao);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("Cadastro", alunos.size() == 1 ? alunos.get(0) : alunos);

        return ResponseEntity.ok().body(response);
    }
    
    @GetMapping("/{matricula}")
    public ResponseEntity<?> buscarPorMatricula(@PathVariable String matricula) {
        Optional<Aluno> alunoOptional = alunoRepository.findByMatricula(matricula);
        if (alunoOptional.isPresent()) {
            Aluno aluno = alunoOptional.get();
            atualizarMediaESituacao(aluno);

            Map<String, Aluno> response = Collections.singletonMap("Cadastro", aluno);

            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aluno não encontrado com a matrícula: " + matricula);
        }
    }

    @PostMapping
    public ResponseEntity<?> adicionar(@RequestBody Aluno aluno) {
        ResponseEntity<?> validacao = validarAluno(aluno);
        if (validacao != null) {
            return validacao;
        }

        try {
            Aluno alunoSalvo = alunoRepository.save(aluno);
            atualizarMediaESituacao(alunoSalvo);

            Map<String, Aluno> response = Collections.singletonMap("Cadastro", alunoSalvo);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Matrícula duplicada. Verifique os dados e tente novamente.");
        }
    }

    @PutMapping("/{matricula}")
    public ResponseEntity<?> atualizar(@PathVariable String matricula, @RequestBody Aluno alunoAtualizado) {
        ResponseEntity<?> validacao = validarAluno(alunoAtualizado);
        if (validacao != null) {
            return validacao;
        }

        Optional<Aluno> alunoOptional = alunoRepository.findByMatricula(matricula);
        if (alunoOptional.isPresent()) {
            Aluno aluno = alunoOptional.get();
            atualizarDadosAluno(aluno, alunoAtualizado);
            atualizarMediaESituacao(aluno);

            Map<String, Aluno> response = Collections.singletonMap("Cadastro", alunoRepository.save(aluno));

            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Aluno não encontrado com a matrícula: " + matricula);
        }
    }

    @PatchMapping("/{matricula}")
    public ResponseEntity<?> atualizarParcial(@PathVariable String matricula, @RequestBody Map<String, Object> campos) {
        Optional<Aluno> alunoOptional = alunoRepository.findByMatricula(matricula);
        return alunoOptional.map(aluno -> {
            try {
                validarNotas(aluno);
                campos.forEach((campo, valor) -> atualizarCampo(aluno, campo, valor));
                atualizarMediaESituacao(aluno);

                Map<String, Aluno> response = Collections.singletonMap("Cadastro", alunoRepository.save(aluno));

                return ResponseEntity.ok().body(response);
            } catch (NumberFormatException | Excecoes.CampoDesconhecidoException | DataIntegrityViolationException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            }
        }).orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Aluno não encontrado com a matrícula: " + matricula));
    }

    @DeleteMapping("/{matricula}")
    public ResponseEntity<?> excluir(@PathVariable String matricula) {
        Optional<Aluno> alunoOptional = alunoRepository.findByMatricula(matricula);
        return alunoOptional.map(aluno -> {
            alunoRepository.delete(aluno);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Aluno não encontrado com a matrícula: " + matricula));
    }

    private ResponseEntity<?> validarAluno(Aluno aluno) {
        if (aluno.getMatricula() == null || aluno.getMatricula().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("A matrícula não pode ser nula ou vazia.");
        }

        if (aluno.getNome() == null || aluno.getNome().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("O nome não pode ser nulo ou vazio.");
        }

        if (aluno.getNotaN1() == null || aluno.getNotaN2() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("As notas não podem ser nulas.");
        }

        if (!(aluno.getNotaN1() instanceof Number) || !(aluno.getNotaN2() instanceof Number)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("As notas devem ser números válidos.");
        }

        try {
            aluno.setNotaN1(parseNota(aluno.getNotaN1()));
            aluno.setNotaN2(parseNota(aluno.getNotaN2()));
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("As notas devem ser números válidos.");
        }

        return null;
    }

    private void atualizarDadosAluno(Aluno aluno, Aluno alunoAtualizado) {
        aluno.setNome(alunoAtualizado.getNome());
        aluno.setMatricula(alunoAtualizado.getMatricula());
        aluno.setNotaN1(alunoAtualizado.getNotaN1());
        aluno.setNotaN2(alunoAtualizado.getNotaN2());
    }

    private void validarNotas(Aluno aluno) {
        aluno.setNotaN1(converterParaNumero(aluno.getNotaN1()));
        aluno.setNotaN2(converterParaNumero(aluno.getNotaN2()));
    }

    private void atualizarCampo(Aluno aluno, String campo, Object valor) {
        String[] parts = campo.split("_");
        StringBuilder camelCase = new StringBuilder(parts[0]);
        switch (camelCase.toString()) {
            case "notaN1":
                aluno.setNotaN1(converterParaNumero(valor));
                break;
            case "notaN2":
                aluno.setNotaN2(converterParaNumero(valor));
                break;
            case "nome":
                aluno.setNome((String) valor);
                break;
            case "matricula":
                aluno.setMatricula((String) valor);
                break;
            default:
                throw new Excecoes.CampoDesconhecidoException("Campo desconhecido: " + campo);
        }
    }

    private void atualizarMediaESituacao(Aluno aluno) {
        ResultadoDTO resultado = aluno.calcularMediaEAtualizarStatus();
        aluno.setMedia(resultado.getMedia());
        aluno.setSituacao(resultado.getSituacao());
    }

    private Double parseNota(Object valor) {
        if (valor instanceof Number) {
            return ((Number) valor).doubleValue();
        } else {
            throw new NumberFormatException("Valor não é um número válido.");
        }
    }

    private Double converterParaNumero(Object valor) {
        if (valor instanceof Number) {
            return ((Number) valor).doubleValue();
        } else if (valor instanceof String) {
            try {
                return Double.parseDouble((String) valor);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("As notas devem ser números válidos.");
            }
        } else {
            throw new NumberFormatException("Valor não é um número válido.");
        }
    }
}
