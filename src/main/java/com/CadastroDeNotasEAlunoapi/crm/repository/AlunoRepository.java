package com.CadastroDeNotasEAlunoapi.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.CadastroDeNotasEAlunoapi.crm.model.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
	Optional<Aluno> findByMatricula(String matricula);
}
