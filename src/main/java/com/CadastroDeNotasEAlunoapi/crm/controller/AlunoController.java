package com.CadastroDeNotasEAlunoapi.crm.controller;

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
    public List<Aluno> listar() {
        List<Aluno> alunos = alunoRepository.findAll();

        // Atualiza a média e situação para cada aluno
        alunos.forEach(this::atualizarMediaESituacao);

        return alunos;
    }

    @GetMapping("/{matricula}")
    public ResponseEntity<Aluno> buscarPorMatricula(@PathVariable String matricula) {
        Optional<Aluno> alunoOptional = alunoRepository.findByMatricula(matricula);
        if (alunoOptional.isPresent()) {
            Aluno aluno = alunoOptional.get();
            atualizarMediaESituacao(aluno);
            return ResponseEntity.ok().body(aluno);
        } else {
            throw new Excecoes.AlunoNotFoundException(matricula);
        }
    }

    @PostMapping
    public ResponseEntity<Aluno> adicionar(@RequestBody Aluno aluno) {
        try {
            validarAluno(aluno);

            Aluno alunoSalvo = alunoRepository.save(aluno);
            atualizarMediaESituacao(alunoSalvo);
            return ResponseEntity.status(HttpStatus.CREATED).body(alunoSalvo);
        } catch (DataIntegrityViolationException e) {
            throw new Excecoes.MatriculaDuplicadaException("Matrícula duplicada. Verifique os dados e tente novamente.");
        } catch (Excecoes.PropriedadeNulaException | Excecoes.FormatoNotaInvalidoException e) {
            throw e;
        } catch (Exception e) {
            throw new Excecoes.PropriedadeNulaException("Erro ao processar a requisição.");
        }
    }

    @PutMapping("/{matricula}")
    public ResponseEntity<Aluno> atualizar(@PathVariable String matricula, @RequestBody Aluno alunoAtualizado) {
        try {
            validarAluno(alunoAtualizado);

            Optional<Aluno> alunoOptional = alunoRepository.findByMatricula(matricula);
            if (alunoOptional.isPresent()) {
                Aluno aluno = alunoOptional.get();
                atualizarDadosAluno(aluno, alunoAtualizado);

                atualizarMediaESituacao(aluno);

                return ResponseEntity.ok().body(alunoRepository.save(aluno));
            } else {
                throw new Excecoes.AlunoNotFoundException(matricula);
            }
        } catch (DataIntegrityViolationException e) {
            throw new Excecoes.MatriculaDuplicadaException("Matrícula duplicada. Verifique os dados e tente novamente.");
        } catch (Excecoes.PropriedadeNulaException | Excecoes.FormatoNotaInvalidoException | Excecoes.AlunoNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new Excecoes.PropriedadeNulaException("Erro ao processar a requisição.");
        }
    }

    @PatchMapping("/{matricula}")
    public ResponseEntity<Aluno> atualizarParcial(@PathVariable String matricula, @RequestBody Map<String, Object> campos) {
        Optional<Aluno> alunoOptional = alunoRepository.findByMatricula(matricula);
        if (alunoOptional.isPresent()) {
            Aluno aluno = alunoOptional.get();
            atualizarCampos(aluno, campos);

            try {
                return ResponseEntity.ok().body(alunoRepository.save(aluno));
            } catch (DataIntegrityViolationException e) {
                throw new Excecoes.MatriculaDuplicadaException("Matrícula duplicada. Verifique os dados e tente novamente.");
            }
        } else {
            throw new Excecoes.AlunoNotFoundException(matricula);
        }
    }

    @DeleteMapping("/{matricula}")
    public ResponseEntity<Void> excluir(@PathVariable String matricula) {
        Optional<Aluno> alunoOptional = alunoRepository.findByMatricula(matricula);
        if (alunoOptional.isPresent()) {
            alunoRepository.delete(alunoOptional.get());
            return ResponseEntity.noContent().build();
        } else {
            throw new Excecoes.AlunoNotFoundException(matricula);
        }
    }

 // Método privado para validar um aluno
    private void validarAluno(Aluno aluno) {
        if (aluno.getMatricula() == null || aluno.getMatricula().trim().isEmpty()) {
            throw new Excecoes.PropriedadeNulaException("A matrícula não pode ser nula ou vazia.");
        }

        if (aluno.getNome() == null || aluno.getNome().trim().isEmpty()) {
            throw new Excecoes.PropriedadeNulaException("O nome não pode ser nulo ou vazio.");
        }

        if (aluno.getNotaN1() == null || aluno.getNotaN2() == null) {
            throw new Excecoes.PropriedadeNulaException("As notas não podem ser nulas.");
        }

     // Verifica se as notas são strings
        if (!(aluno.getNotaN1() instanceof Number) || !(aluno.getNotaN2() instanceof Number)) {
            throw new Excecoes.FormatoNotaInvalidoException("As notas devem ser números válidos.");
        }

        try {
            // Verifica se as notas são números válidos
            aluno.setNotaN1(parseNota(aluno.getNotaN1()));
            aluno.setNotaN2(parseNota(aluno.getNotaN2()));
        } catch (NumberFormatException e) {
            throw new Excecoes.FormatoNotaInvalidoException("As notas devem ser números válidos.");
        }
    }


    // Método privado para atualizar dados do aluno
    private void atualizarDadosAluno(Aluno aluno, Aluno alunoAtualizado) {
        aluno.setNome(alunoAtualizado.getNome());
        aluno.setMatricula(alunoAtualizado.getMatricula());
        aluno.setNotaN1(alunoAtualizado.getNotaN1());
        aluno.setNotaN2(alunoAtualizado.getNotaN2());
    }

    // Método privado para atualizar campos parciais
    private void atualizarCampos(Aluno aluno, Map<String, Object> campos) {
        campos.forEach((campo, valor) -> {
            // Convertendo a chave para camelCase manualmente
            String[] parts = campo.split("_");
            StringBuilder camelCase = new StringBuilder(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                camelCase.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
            }

            switch (camelCase.toString()) {
                case "notaN1":
                    aluno.setNotaN1(parseNota(valor));
                    break;
                case "notaN2":
                    aluno.setNotaN2(parseNota(valor));
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
        });
    }

    // Método privado para atualizar média e situação de um aluno
    private void atualizarMediaESituacao(Aluno aluno) {
        ResultadoDTO resultado = aluno.calcularMediaEAtualizarStatus();
        aluno.setMedia(resultado.getMedia());
        aluno.setSituacao(resultado.getSituacao());
    }

    // Método privado para converter a nota para Double
    private Double parseNota(Object valor) {
        if (valor instanceof Number) {
            return ((Number) valor).doubleValue();
        } else {
            throw new NumberFormatException("Valor não é um número válido.");
        }
    }
}
