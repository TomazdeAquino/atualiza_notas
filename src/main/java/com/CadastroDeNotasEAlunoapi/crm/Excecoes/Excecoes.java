package com.CadastroDeNotasEAlunoapi.crm.Excecoes;

public class Excecoes {

    public static class AlunoNotFoundException extends RuntimeException {
        public AlunoNotFoundException(String matricula) {
            super("Aluno não encontrado com a matrícula: " + matricula);
        }
    }

    public static class CampoDesconhecidoException extends RuntimeException {
        public CampoDesconhecidoException(String campo) {
            super("Campo desconhecido: " + campo);
        }
    }

    public static class MatriculaDuplicadaException extends RuntimeException {
        public MatriculaDuplicadaException(String mensagem) {
            super(mensagem);
        }
    }

    public static class PropriedadeNulaException extends RuntimeException {
        public PropriedadeNulaException(String mensagem) {
            super(mensagem);
        }
    }
    
    public static class FormatoNotaInvalidoException extends RuntimeException {
        public FormatoNotaInvalidoException(String mensagem) {
            super(mensagem);
        }
    }
    
    // Adicione outras exceções conforme necessário
}
