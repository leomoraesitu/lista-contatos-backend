# Lista de Contatos | Java + Spring Boot

API REST de portfólio full stack para gerenciamento de contatos, desenvolvida com **Java 21 e Spring Boot** e integrada a um frontend em **Angular e TypeScript**. Implementa cadastro, consulta, edição e exclusão de contatos com persistência relacional via Spring Data JPA.

O projeto demonstra o caminho entre uma requisição HTTP e o banco de dados, com responsabilidades organizadas em controllers, services e repositories. Este repositório contém o backend; a interface está em [lista-contatos-frontend](https://github.com/leomoraesitu/lista-contatos-frontend).

## Funcionalidades

- Cadastro de nome, email, telefone e endereço completo.
- Listagem de contatos e consulta por identificador.
- Atualização e exclusão de registros.
- Validação de campos obrigatórios no cadastro e na atualização com Bean Validation.
- DTO de entrada sem ID, com identificação gerada no cadastro e definida pela URL na atualização.
- Respostas estruturadas para validação (`422`), recurso não encontrado (`404`)
  e falhas de persistência (`500`), estas últimas implementadas localmente.

## Competências demonstradas

- **Desenvolvimento de APIs:** endpoints REST, métodos HTTP e respostas com status e cabeçalho `Location` no cadastro.
- **Organização do backend:** separação entre camada HTTP, serviços e acesso aos dados, com DTO de entrada, conversão explícita para entidade e injeção de dependências do Spring.
- **Persistência relacional:** mapeamento de entidades com JPA, identificadores gerados pelo banco e operações com Spring Data JPA.
- **Integração full stack:** comunicação em JSON com uma aplicação Angular independente.
- **Testes automatizados:** integração HTTP com MockMvc e H2, incluindo validação, persistência e contratos de resposta.
- **Ambiente de desenvolvimento:** execução pelo Maven Wrapper e configuração documentada de runtime e banco local.

## Tecnologias

| Camada | Tecnologias |
| --- | --- |
| Backend — este repositório | Java 21, Spring Boot 4.1.2, Spring Web MVC e Bean Validation |
| Persistência | Spring Data JPA, H2 em memória e SQL para a carga inicial |
| Build | Maven 3.9.5 via Maven Wrapper |
| Frontend — repositório complementar | Angular 16, TypeScript, RxJS e Bootstrap 5 |

## Bootstrap local (macOS com Apple Silicon)

As instruções reproduzem o ambiente configurado com Homebrew e shell Zsh. Não é necessário instalar Maven globalmente, Docker ou um servidor de banco externo. A primeira execução exige acesso à internet para baixar o Maven e as dependências.

### 1. Instalar e configurar o JDK 21

Com o [Homebrew](https://brew.sh/) disponível:

```bash
brew install openjdk@21
```

No `~/.zprofile`, adicione uma única vez ou substitua a configuração anterior do Java por:

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
```

Remova ou substitua a linha antiga que prioriza `/opt/homebrew/opt/openjdk/bin`, para não selecionar o JDK de outra versão. Esses caminhos pressupõem Homebrew em `/opt/homebrew`.

Abra um novo terminal de login no VS Code e confira:

```bash
java -version
javac -version
```

Ambos devem mostrar a versão `21`.

### 2. Executar a API

O exemplo pressupõe o repositório clonado em `~/Documents/Dev/Portfolio`. Ajuste o caminho se necessário.

```bash
cd ~/Documents/Dev/Portfolio/lista-contatos-backend
bash ./mvnw --version
bash ./mvnw spring-boot:run
```

O wrapper deve informar Maven `3.9.5` e Java `21`. O uso de `bash ./mvnw` permite executar o arquivo mesmo sem a permissão de execução.

A API fica disponível em **http://127.0.0.1:8080/contatos**. Mantenha o terminal em execução e use `Ctrl+C` para encerrar.

### 3. Configuração do banco e variáveis

O arquivo `src/main/resources/application.properties` já configura o ambiente local:

| Configuração | Valor |
| --- | --- |
| URL JDBC | `jdbc:h2:mem:testdb` |
| Usuário | `sa` |
| Senha | Vazia |
| Porta HTTP padrão | `8080` |

Não há necessidade de `.env`, perfil adicional ou credenciais externas. O H2 é iniciado dentro da aplicação; os dados são descartados quando o backend é encerrado e a massa de `data.sql` é carregada novamente na próxima inicialização.

### 4. Conferir a disponibilidade e integrar o frontend

Em outro terminal:

```bash
curl --fail --silent --show-error --output /dev/null --write-out 'Backend HTTP %{http_code}\n' http://127.0.0.1:8080/contatos
```

O resultado esperado é `Backend HTTP 200`. Essa conferência valida a disponibilidade, sem substituir os testes do CRUD.

Para executar a interface, siga o [bootstrap do frontend](https://github.com/leomoraesitu/lista-contatos-frontend#-bootstrap-local-macos-com-apple-silicon). Ele utiliza Node `18.20.8` via fnm e inicia em `http://localhost:4200`. Node e npm são necessários apenas para o frontend. A URL da API está fixa no serviço Angular; mantenha a porta `8080` para a integração atual.

### Extensões opcionais do VS Code

As extensões ajudam na edição, navegação e depuração, mas não são necessárias para executar a API pelo terminal:

```bash
code --install-extension vscjava.vscode-java-pack
code --install-extension vmware.vscode-boot-dev-pack
```

## Endpoints

| Método | Rota | Comportamento e resposta de sucesso |
| --- | --- | --- |
| GET | `/contatos` | Lista os contatos — `200` com array JSON |
| GET | `/contatos/{id}` | Consulta um contato — `200` com objeto JSON |
| POST | `/contatos` | Cadastra um contato — `201` com objeto JSON e `Location` |
| PUT | `/contatos/{id}` | Atualiza um contato — `200` sem corpo |
| DELETE | `/contatos/{id}` | Exclui um contato — `204` sem corpo |

Exemplo de corpo JSON para cadastro ou atualização:

```json
{
  "name": "Contato de Exemplo",
  "email": "contato@example.com",
  "telefone": "11900000000",
  "endereco": "Rua Exemplo, 100",
  "cidade": "Itu",
  "cep": "13300000",
  "estado": "SP"
}
```

POST e PUT recebem o record
[ContatoRequest](src/main/java/br/com/fatec/listacontatosbackend/dto/ContatoRequest.java),
com os sete campos acima e sem componente `id`. O controller converte o DTO para
uma entidade nova por um método privado compartilhado; as assinaturas do serviço
e as validações da entidade foram mantidas.

A propriedade JSON `id`, quando enviada, é explicitamente ignorada. No POST,
o banco gera o identificador, mesmo que o corpo informe um ID existente. No PUT,
somente o ID da URL determina qual registro será atualizado. O Angular pode
continuar enviando seu objeto atual, incluindo `id` no corpo da atualização.

No cadastro e na atualização, os sete campos possuem validação `@NotBlank`;
campos omitidos, nulos, vazios ou contendo somente espaços retornam `422`, com mensagens
na lista `errors`. Uma atualização rejeitada não modifica o registro persistido.
Não há validação de formato de email, telefone, CEP ou UF no backend atual.
Consultas e exclusões de identificadores inexistentes retornam `404`; no PUT,
um identificador inexistente com payload válido também retorna `404`.

### Falhas de persistência

Na entrega local `fix/contact-persistence-errors`, `ContatoService.update()`
preserva o tratamento de contato inexistente e deixa falhas de persistência
propagarem. O advice trata `DataAccessException` com status HTTP `500` e o
seguinte formato de resposta (horário e caminho variam por requisição):

```json
{
  "timeStamp": "2026-10-28T12:00:00Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Não foi possível concluir a operação.",
  "path": "/contatos/1"
}
```

A exceção completa é registrada no servidor com mensagem fixa, sem incluir o
corpo da requisição na mensagem de log. A resposta usa mensagem genérica e não
expõe SQL ou mensagens internas. O tratamento se aplica às exceções de acesso
a dados do Spring; não é um handler geral para todos os erros HTTP.

## Testes

Na raiz deste repositório, com JDK 21:

```bash
bash ./mvnw test
git diff --check
```

A suíte de integração em
[ContatoControllerIntegrationTests.java](src/test/java/br/com/fatec/listacontatosbackend/resources/ContatoControllerIntegrationTests.java)
usa MockMvc com o contexto Spring e o banco H2 `contact-validation-tests`, separado
do banco local da aplicação. A carga de `data.sql` é desativada nesses testes;
os contatos são fictícios e cada caso limpa esse banco. Não é necessário iniciar
a API separadamente nem executar o frontend.

A cobertura inclui:

- POST e PUT com cada um dos sete campos obrigatórios nulo, vazio ou contendo
  somente espaços: `422` com a mensagem correspondente.
- Ausência de persistência no POST inválido e preservação do registro no PUT inválido.
- POST válido: `201`, corpo JSON e cabeçalho `Location`.
- PUT válido: `200` sem corpo e atualização dos dados no banco.
- PUT com payload válido e ID inexistente: `404`.
- POST com ID existente no corpo: novo identificador, corpo e `Location` corretos,
  preservando o contato original.
- PUT com IDs divergentes: somente o contato da URL é atualizado.
- Cada um dos sete campos omitido em POST e PUT: `422`, sem criação ou alteração
  indevida de registros.

Os testes adicionais são:

- `ContatoServiceTests`: dois testes unitários com repositório simulado; o save
  com falha propaga a mesma instância da exceção, e contato inexistente mantém
  `EntityNotFoundException` sem tentar gravar.
- `ContatoPersistenceErrorTests`: dois testes HTTP com MockMvc standalone,
  controller, serviço e advice reais e repositório simulado. POST e PUT retornam
  `500` com mensagem genérica, horário e caminho; a resposta não contém a mensagem
  interna simulada, SQL simulado, trace ou exception.

Para executar somente os quatro novos testes:

```bash
bash ./mvnw -Dtest=ContatoServiceTests,ContatoPersistenceErrorTests test
```

Em **28/10/2026**, `bash ./mvnw test` com JDK 21 passou com **66 testes**, sem
falhas, erros ou ignorados: 61 casos de integração, um teste de contexto,
dois unitários do serviço e dois HTTP standalone. PUT válido continua retornando
`200` sem corpo, ID inexistente com payload válido `404` e campos inválidos `422`,
com o registro original preservado nas atualizações rejeitadas.

Na reconciliação documental, os relatórios dessa execução foram conferidos;
a suíte não foi repetida porque não houve novas mudanças de código. Os testes
com repositório simulado não verificam uma indisponibilidade real do banco nem
o conteúdo do log automaticamente. Não houve teste ponta a ponta com Angular.

## Estado da entrega e próximos passos

A correção da validação do PUT e seus testes foram integrados à `main` em
`74a689f` (PR #2).

A entrega do DTO de entrada foi integrada em `8435497` (PR #3), incluindo
o record `ContatoRequest`, adaptação do controller e os testes de identificação.

O commit `39c1d52` atualiza o Spring Boot para 4.1.2, usa injeção por construtor
no controller e no serviço e adapta os testes ao Jackson 3 e ao Spring Web MVC.
O agente do Mockito é carregado na inicialização da JVM de testes pelo Maven,
sem necessidade de parâmetros adicionais no comando `bash ./mvnw test`.

A branch `fix/contact-persistence-errors` contém alterações locais sem commit:
remoção da conversão genérica em `404`, handler de `DataAccessException`, log da
exceção e quatro novos testes, com a suíte de 66 casos aprovada. Os dois arquivos
novos de testes ainda precisam ser incluídos no staging explícito ao preparar a
entrega. Commit, push e PR ficam para quando solicitados; não há merge automático.

Transações, regras de formato, CORS por ambiente, persistência durável, CI e
tratamento geral de erros HTTP continuam pendentes. A próxima ação é revisar e
preparar a entrega atual para integração. O plano e as evidências dos dois
repositórios estão no
[MEMORY.md do frontend](https://github.com/leomoraesitu/lista-contatos-frontend/blob/main/MEMORY.md).

## Vídeo de apresentação

[<img src="https://github.com/leomoraesitu/lista-contatos-backend/assets/128606856/0b2a27f1-fb7a-4cd7-b1ff-348f2a90b56f" alt="Assistir à demonstração da aplicação Lista de Contatos" width="300">](https://fatecspgov-my.sharepoint.com/:v:/g/personal/leonardo_souza111_fatec_sp_gov_br/EUp1ie-4dh5BtpeXh8RrRlQBP80T7IlbTXLM2Qh7ni_kWw?e=JxYh4E)
