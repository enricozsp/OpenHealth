# OpenHealth · Meu Prontuário

Wallet pessoal de saúde. Os seus dados de saúde pertencem a você, não a clínicas, hospitais ou médicos.

## Conteúdo do prontuário

- **Perfil** — dados demográficos, tipo sanguíneo, contato de emergência
- **Anamnese & Antecedentes** — queixa principal, história, antecedentes pessoais e familiares, hábitos, medicações
- **Alergias** — substância, reação, gravidade
- **Vacinas** — dose, fabricante, lote, datas
- **Cirurgias** — procedimento, data, hospital, equipe
- **Consultas e check-ups** — data, especialidade, profissional, diagnóstico, prescrição
- **Exames clínicos** — categoria, laboratório, resultado

## Stack

- Java 21 (compila em qualquer JDK >= 21) · Spring Boot 3.4
- Spring Web, Spring Data JPA, Bean Validation
- H2 file-mode (`./data/openhealth.mv.db`) — os dados ficam no disco junto ao app
- Frontend vanilla: HTML + CSS + JS (sem build step)

## Como rodar

Pré-requisito: JDK 21+ instalado.

### Via Maven
```bash
mvn spring-boot:run
```

### Via IntelliJ IDEA
Abra a pasta, deixe o IntelliJ importar o `pom.xml`, e execute a classe `com.openhealth.OpenHealthApplication`.

Depois acesse:

- App: http://localhost:8080
- Console H2: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:file:./data/openhealth`, user `sa`, sem senha)

## Autenticação

Wallet single-user: cada instância OpenHealth tem uma única conta dona. A primeira pessoa que acessa cadastra-se em `/cadastro.html`; depois disso, somente login é permitido. Senhas são armazenadas com PBKDF2-HMAC-SHA256 (120k iterações + salt aleatório). A sessão fica em cookie (JSESSIONID).

| Endpoint | Descrição |
|----------|-----------|
| `GET /api/auth/status` | `{ registered, authenticated }` |
| `POST /api/auth/register` | Cria a conta (apenas se ainda não existir) e já loga |
| `POST /api/auth/login` | Email + senha → sessão |
| `POST /api/auth/logout` | Invalida sessão |
| `GET /api/auth/me` | Perfil autenticado ou 401 |

Páginas: `/cadastro.html`, `/login.html`, `/` (wallet, requer login).

## API REST (requer autenticação)

| Recurso | Métodos |
|---------|---------|
| `/api/profile` | GET, PUT (perfil do usuário logado) |
| `/api/anamnese` | GET, PUT (singleton) |
| `/api/allergies` | GET, POST, PUT/{id}, DELETE/{id} |
| `/api/vaccines` | GET, POST, PUT/{id}, DELETE/{id} |
| `/api/surgeries` | GET, POST, PUT/{id}, DELETE/{id} |
| `/api/consultations` | GET, POST, PUT/{id}, DELETE/{id} |
| `/api/exams` | GET, POST, PUT/{id}, DELETE/{id} |

## Persistência

O banco H2 é gravado em `./data/openhealth.mv.db` (modo file). Para resetar a wallet, basta apagar essa pasta `data/`.