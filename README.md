# Sistema de Gestão de Alunos

## Descrição do Projeto

O Sistema de Gestão de Alunos é uma aplicação Java desenvolvida como parte do Laboratório 3 da disciplina de Programação Orientada a Objetos. O sistema oferece recursos para cadastrar, visualizar, atualizar e excluir informações de alunos, proporcionando uma maneira eficiente de gerenciar dados acadêmicos.

## Desenvolvedores
- [Tomaz de Aquino Ribeiro Junior](https://github.com/dev1) - RA: 202107638
- [Murilo Luciano dos Santos](https://github.com/dev2) - RA: 202107638 

## Tecnologia Empregada

### Escolha das Tecnologias

- **Linguagem de Programação: Java**
  - Justificativa: A escolha do Java se deve à sua ampla adoção, portabilidade e robustez, proporcionando uma base sólida para o desenvolvimento de aplicativos corporativos.

- **Framework: Spring Boot**
  - Justificativa: O Spring Boot simplifica o desenvolvimento de aplicativos Java, oferecendo configuração mínima e facilitando a construção de aplicativos prontos para produção.

- **Banco de Dados: H2 Database (ou outro de sua escolha)**
  - Justificativa: O H2 Database foi escolhido para ambientes de desenvolvimento e teste devido à sua natureza incorporada e facilidade de configuração. Em ambientes de produção, seria substituído por um banco de dados mais robusto.

## Descrição da Arquitetura

A aplicação segue uma arquitetura MVC (Model-View-Controller), onde as classes do modelo representam os alunos, o controlador gerencia as requisições HTTP e a camada de visualização (ainda não implementada) lida com a apresentação dos dados.

## Funcionalidade

A aplicação oferece as seguintes funcionalidades:

- **Cadastro de Alunos:** Permite o registro de novos alunos com informações como nome, matrícula e notas.

- **Consulta de Alunos:** Possibilita a visualização de detalhes de alunos existentes.

- **Atualização de Dados:** Permite a modificação das informações de um aluno, incluindo nome, matrícula e notas.

- **Exclusão de Alunos:** Permite a remoção de registros de alunos do sistema.

## Documentação

A documentação do projeto inclui comentários no código, explicação de classes e métodos, além de instruções claras sobre como executar a aplicação.

# **API Documentation**
## **GET Todos Alunos:**
**Método:** GET

**URL:** http://localhost:8080/alunos

**Resposta:**

- **Status 200 OK:** Retorna uma lista de todos os alunos.

\--- 
## **GET Aluno por Matrícula:**
**Método:** GET

**URL:** http://localhost:8080/alunos/{matricula}

**Parâmetros de Caminho:**

- {matricula}: Matrícula do aluno a ser recuperado.

**Respostas:**

- **Status 200 OK:** Retorna os detalhes do aluno correspondente à matrícula fornecida.
- **Status 404 Not Found:** Se não encontrar um aluno com a matrícula especificada.
  - **Corpo da Resposta:**

    { "message": "Aluno não encontrado com a matrícula: {matricula}" } --- 
## **POST Adicionar Aluno:**
**Método:** POST

**URL:** http://localhost:8080/alunos

**Corpo (Body):**

{ "nome": "Nome do Aluno", "matricula": "202115951", "notaN1": 7.5, "notaN2": 8.0 } 

**Respostas:**

- **Status 201 Created:** Retorna os detalhes do aluno recém-criado.
- **Status 400 Bad Request:** Se o corpo da solicitação estiver ausente ou malformado.
  - **Corpo da Resposta:**

    { "message": "Corpo da solicitação ausente ou malformado." } 

- **Status 409 Conflict:** Se houver uma tentativa de criar um aluno com uma matrícula já existente.
  - **Corpo da Resposta:**

    { "message": "Matrícula duplicada. Verifique os dados e tente novamente." } --- 
## **PUT Atualizar Aluno por Matrícula:**
**Método:** PUT

**URL:** http://localhost:8080/alunos/{matricula}

**Parâmetros de Caminho:**

- {matricula}: Matrícula do aluno a ser atualizado.

**Corpo (Body):**

{ "nome": "Novo Nome", "matricula": "Nova Matrícula", "notaN1": 7.5, "notaN2": 8.0 } 

**Respostas:**

- **Status 200 OK:** Retorna os detalhes do aluno atualizado.
- **Status 400 Bad Request:** Se o corpo da solicitação estiver ausente ou malformado.
  - **Corpo da Resposta:**

    { "message": "Corpo da solicitação ausente ou malformado." } 

- **Status 404 Not Found:** Se não encontrar um aluno com a matrícula especificada.
  - **Corpo da Resposta:**

    { "message": "Aluno não encontrado com a matrícula: {matricula}" } --- 
## **PATCH Atualizar Parcialmente Aluno por Matrícula:**
**Método:** PATCH

**URL:** http://localhost:8080/alunos/{matricula}

**Parâmetros de Caminho:**

- {matricula}: Matrícula do aluno a ser atualizado parcialmente.

**Corpo (Body):**

{ "nome": "Novo Nome", "notaN1": 8.0 } 

**Respostas:**

- **Status 200 OK:** Retorna os detalhes do aluno atualizado.
- **Status 400 Bad Request:** Se o corpo da solicitação estiver ausente ou malformado.
  - **Corpo da Resposta:**

    { "message": "Corpo da solicitação ausente ou malformado." } 

- **Status 404 Not Found:** Se não encontrar um aluno 


