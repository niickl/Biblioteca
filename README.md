# Biblioteca

Este repositório contém um sistema de exemplo chamado "Biblioteca":
- Backend: API em Spring Boot (Java) com JPA/Hibernate, Flyway, segurança por JWT e agendamento de tarefas.
- Frontend: Aplicação SPA em Angular (standalone components) com autenticação e formulários de cadastro/login.

Checklist (nova versão do README)
- [x] Visão geral do projeto
- [x] Lista de funcionalidades / endpoints expostos
- [x] Como executar (backend e frontend) com exemplos em PowerShell
- [x] Configurações de banco de dados e Flyway
- [x] Segurança (JWT, roles) e como usar
- [x] Detalhes do frontend e integração com a API
- [x] Notas sobre o job agendado (envio de e-mails)

Sumário
- Visão geral
- Funcionalidades e endpoints
- Modelos (entities / DTOs)
- Segurança (JWT, roles)
- Execução (backend)
- Execução (frontend)
- Banco de dados, migrações e H2 para desenvolvimento
- Testes
- Dicas e correções (problemas conhecidos)
- Contribuição
- Licença

Visão geral
O backend está em `src/main/java` (pacote `com.example.Biblioteca`) e expõe endpoints REST para gerenciar autores, editoras, livros e usuários. Há também suporte a autenticação JWT e um job agendado que envia e-mails para usuários com empréstimos vencendo.

O frontend está em `biblioteca-frontend` e contém telas de login, cadastro e dashboard. O frontend armazena o token JWT no localStorage e injeta o header Authorization nas requisições.

Funcionalidades e endpoints
API base: http://localhost:8080 (padrão ao rodar o backend localmente)

- Autenticação
  - POST /auth/login
    - Body: { "login": "...", "senha": "..." }
    - Retorna: { "token": "<jwt>" }

- Usuários
  - POST /usuarios
    - Cria um novo usuário (registro). Request body: `UsuarioRequestDTO` (nome, login, senha, roles).
    - Esse endpoint é permitido sem autenticação (login/registro público).

- Autores
  - POST /autores (ROLE_ADMIN)
  - GET /autores
  - GET /autores/{id}
  - GET /autores/nome/{nome} (busca por nome, contains ignore case)
  - PUT /autores/{id}
  - PUT /autores/nome/{nome}
  - DELETE /autores/{id}
  - DELETE /autores/nome/{nome}

- Editoras
  - POST /editoras
  - GET /editoras
  - GET /editoras/{id}
  - GET /editoras/nome/{nome}
  - PUT /editoras/{id}
  - PUT /editoras/nome/{nome}
  - DELETE /editoras/{id}
  - DELETE /editoras/nome/{nome}

- Livros
  - POST /livros
    - Body: LivroRequestDTO { nome, descricao, autorId, editoraId }
  - GET /livros
  - GET /livros/{id}
  - GET /livros/nome/{nome}
  - PUT /livros/{id}
  - PUT /livros/nome/{nome}
  - DELETE /livros/{id}
  - DELETE /livros/nome/{nome}

Observação: O projeto possui uma entidade `Emprestimo` e um repositório `EmprestimoRepository`, mas não há controller público para endpoints CRUD de empréstimos neste repositório — o comportamento observado é o agendamento de notificações por e-mail.

Modelos (entities / DTOs)
- Principais entidades (localizadas em `src/main/java/com/example/Biblioteca/Entity`):
  - `UsuarioEntity` (UUID id, nome, login, senha, roles)
  - `RoleEntity` / `RoleEnum` (roles do sistema)
  - `AutorEntity` (id, nome, idade, biografia)
  - `EditoraEntity` (id, nome, cnpj)
  - `LivroEntity` (id, nome, descricao, lido, autor, editora)
  - `EmprestimoEntity` (usuario, livro, dataEmprestimo, dataDevolucaoPrevista, devolvido)

- DTOs (em `dto/`) usados pela API para entrada/saída: `*RequestDTO` e `*ResponseDTO` (ex.: `LivroRequestDTO`, `LivroResponseDTO`, `UsuarioRequestDTO`, `TokenResponseDTO`, etc.).

Segurança (JWT, roles)
- Autenticação: ao fazer login em `/auth/login` o backend retorna um token JWT gerado por `TokenService`. A chave usada vem de `api.security.token.secret` definida em `application.yaml`.
- Expiração: tokens expiram em 2 horas (configurado em `TokenService`).
- Filtros: `SecurityFilter` recupera o token do header `Authorization: Bearer <token>` e carrega o usuário do banco.
- Configuração: `SecurityConfiguration` permite sem autenticação apenas:
  - POST /usuarios (criar conta)
  - POST /auth/login (autenticar)
  - POST /autores está especificamente protegido com acesso `hasRole("ADMIN")` — outras rotas exigem autenticação.

Agendamento e envio de e-mails
- Job: `VerificarAtrasosJob` roda diariamente às 02:00 (cron `0 0 2 * * *`) e busca empréstimos com devolução prevista para o dia seguinte. Para cada empréstimo encontrado, o job tenta enviar um e-mail de lembrete usando `JavaMailSender`.
- Configuração de e-mail: `application.yaml` contém placeholders (host, port, username, password). Configure as credenciais reais (ou use um serviço de e-mail de testes) para que o envio funcione.

Execução — Backend (passo a passo)
1) Ajuste `src/main/resources/application.yaml` com suas credenciais do banco e e-mail. Importante:
   - `spring.datasource.url`, `username`, `password`
   - `api.security.token.secret` para uma string forte
   - `spring.mail.username` e `spring.mail.password` para envio de e-mails.

2) Compilar e rodar (usar o wrapper Maven):

```powershell
cd C:\Users\nicolas.silva\Downloads\Biblioteca\Biblioteca
.\mvnw.cmd clean package -DskipTests
# ou para rodar em modo de desenvolvimento
.\mvnw.cmd spring-boot:run
```

3) Ao subir, Flyway executará as migrations em `src/main/resources/db.migration` criando as tabelas necessárias.

Execução — Frontend (passo a passo)
1) Abrir terminal na pasta do frontend e instalar dependências:

```powershell
cd C:\Users\nicolas.silva\Downloads\Biblioteca\Biblioteca\biblioteca-frontend
npm install
```

2) Rodar em desenvolvimento:

```powershell
npm start
```

3) Observações sobre integração frontend/backend:
  - O frontend possui um `AuthService` que salva o token em `localStorage` (key `jwt_token`).
  - O `auth-interceptor` (`src/app/core/interceptors/auth-interceptor.ts`) adiciona automaticamente o header `Authorization: Bearer <token>` às requisições quando o token estiver presente.

Problema conhecido (atenção):
- No frontend, `AuthService` define `apiUrl = 'http://localhost:8080/usuarios'` e em seguida faz `this.http.post(`${this.apiUrl}/login`, ...)` para login — isso resulta em uma chamada para `POST /usuarios/login`. O backend espera o login em `POST /auth/login`.
  - Correção recomendada (escolha uma das duas):
    1) Alterar `AuthService.apiUrl` para `'http://localhost:8080/auth'` e manter `this.http.post(`${this.apiUrl}/login`, ...)` — assim o login irá para `/auth/login`.
    2) Ou alterar a chamada de login para usar `http://localhost:8080/auth/login` explicitamente.

Exemplos de requisições (cURL)
# Login
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{"login":"usuario@example.com","senha":"senha"}'

# Criar usuário
curl -X POST http://localhost:8080/usuarios -H "Content-Type: application/json" -d '{"nome":"Fulano","login":"fulano@example.com","senha":"senha","roles":["USER"] }'

# Listar livros (autenticado)
curl -H "Authorization: Bearer <token>" http://localhost:8080/livros

Banco de dados e migrações
- Flyway: migrations em `src/main/resources/db.migration` são aplicadas automaticamente no startup.
- PostgreSQL: `application.yaml` apontando para `jdbc:postgresql://localhost:5432/Biblioteca` por padrão.
- H2 local: o repositório já contém um arquivo H2 em `dados/default.mv.db`. Para usar H2 em desenvolvimento, você pode criar um arquivo `application-local.yaml` (não commitá-lo) ou alterar `application.yaml` localmente. Exemplo de `application-local.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:file:./dados/default;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
  jpa:
    hibernate:
      ddl-auto: update

api.security:
  token:
    secret: your_local_dev_secret
```

Testes
- Backend (Maven):

```powershell
.\mvnw.cmd test
```

- Frontend (npm):

```powershell
cd biblioteca-frontend
npm test
```

Dicas e correções rápidas
- Segurança: não deixe `application.yaml` com senhas e chaves em repositórios públicos. Use variáveis de ambiente ou `application-*.yaml` ignorados pelo git.
- Frontend/backend: verifique a URL de login (veja o problema conhecido acima) antes de testar a autenticação.
- Scheduler & e-mail: para testar o envio de e-mails localmente, configure um provedor de SMTP de testes (Mailtrap, Ethereal, etc.) ou ajuste `VerificarAtrasosJob` para rodar manualmente.

Contribuição
1. Abra uma issue descrevendo a feature ou bug.
2. Crie uma branch seguindo o padrão `feature/NOME` ou `fix/NOME`.
3. Abra um Pull Request com descrição e screenshots (quando aplicável).

Licença
- Este projeto não contém informações de licença. Adicione uma licença adequada (por exemplo MIT, Apache-2.0) se for compartilhar publicamente.

Arquivos úteis no repositório
- `pom.xml` — dependências e plugins (Spring Boot 4.x, Flyway, Lombok, etc.)
- `src/main/resources/application.yaml` — config padrão (datasource, mail, api.security.token.secret)
- `src/main/resources/db.migration/` — scripts de criação de tabelas (Flyway)
- `biblioteca-frontend/` — aplicação Angular (login, cadastro, dashboard)



