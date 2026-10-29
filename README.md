# Lista de Contatos | Java + Spring Boot

API REST de portfólio full stack para gerenciamento de contatos, desenvolvida com **Java 21 e Spring Boot** e integrada a um frontend em **Angular e TypeScript**. Implementa cadastro, consulta, edição e exclusão de contatos com persistência relacional via Spring Data JPA.

O projeto demonstra o caminho entre uma requisição HTTP e o banco de dados, com responsabilidades organizadas em controllers, services e repositories. Este repositório contém o backend; a interface está em [lista-contatos-frontend](https://github.com/leomoraesitu/lista-contatos-frontend).

## Funcionalidades

- Cadastro de nome, email, telefone e endereço completo.
- Listagem de contatos e consulta por identificador.
- Atualização e exclusão de registros.
- Validação de campos obrigatórios no cadastro e na atualização com Bean Validation.
- DTO de entrada sem ID, com identificação gerada no cadastro e definida pela URL na atualização.
- Respostas estruturadas para erros de validação e recursos não encontrados.

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
| Backend — este repositório | Java 21, Spring Boot 3.1.6, Spring Web e Bean Validation |
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

Em **28/10/2026**, na revisão do DTO, `bash ./mvnw test` com JDK 21 passou com
**62 testes**, sem falhas, erros ou testes ignorados: 61 casos de integração e o
teste existente de inicialização do contexto. Os 46 testes anteriores foram
preservados; foram adicionados dois casos de identificação e 14 de campos omitidos.
Antes da correção já integrada do PUT, os 21 casos de atualização inválida
retornavam `404` e falhavam.

Na revisão, `git diff --check` também passou. Nesta reconciliação exclusivamente
documental, foram consultados os relatórios da execução; a suíte não foi repetida.
Essa evidência não inclui teste ponta a ponta com a interface Angular; novas
alterações de código exigem nova validação.

## Estado da entrega e próximos passos

A correção da validação do PUT e seus testes foram integrados à `main` em
`74a689f` (PR #2).

A entrega atual do DTO de entrada está local na branch `refactor/contact-input-dto`,
baseada nesse commit, revisada e ainda sem commit, push ou PR em 28/10/2026.
Inclui o record `ContatoRequest`, adaptação do controller, testes e este README.
As alterações do serviço são apenas de formatação. Os contratos HTTP foram
preservados e os 62 testes passaram.

A próxima ação é preparar essa entrega para integração quando solicitada,
incluindo o novo arquivo do DTO no staging explícito. A reconciliação do README
e da memória do frontend está na branch `docs/reconcile-contact-dto`, sem commit
ou push. Não há merge automático.

Tratamento de erros internos, injeção por construtor, transações, regras de formato,
CORS por ambiente, persistência durável e CI continuam pendentes. Após a integração,
defina uma etapa pequena para corrigir o tratamento de exceções genéricas do
serviço, que ainda as converte em recurso não encontrado. O plano completo e as
evidências dos dois repositórios estão no
[MEMORY.md do frontend](https://github.com/leomoraesitu/lista-contatos-frontend/blob/main/MEMORY.md).

## Vídeo de apresentação

[<img src="https://github.com/leomoraesitu/lista-contatos-backend/assets/128606856/0b2a27f1-fb7a-4cd7-b1ff-348f2a90b56f" alt="Assistir à demonstração da aplicação Lista de Contatos" width="300">](https://fatecspgov-my.sharepoint.com/:v:/g/personal/leonardo_souza111_fatec_sp_gov_br/EUp1ie-4dh5BtpeXh8RrRlQBP80T7IlbTXLM2Qh7ni_kWw?e=JxYh4E)
