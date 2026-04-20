# 📊 PriceLab

> PriceLab é um sistema fullstack de gestão de produtos com suporte a múltiplas moedas e cálculo automático de custos de importação, permitindo simular estratégias de precificação com base em margem, impostos e elasticidade de demanda.

Desenvolvido por **Felipe Boos**

---

## Estratégias de Preço 

### Simulação de estratégias de preço
![Simular Estratégia](docs/screenshots/estrategias-simular.gif)

### Listagem de estratégias de preço
![Estratégias de Preço](docs/screenshots/estrategias-lista.png)

## Produtos

###  Cadastro de Produtos
![Produtos](docs/screenshots/produtos_cadastro.png)

###  Fluxo de Cadastro (com cálculo automático)
![Produtos](docs/screenshots/produtos_cadastro_video.gif)

### Listagem de produtos
![Produtos](docs/screenshots/produtos_listagem.png)

## Categorias
 
### Listagem de categorias
![Categorias](docs/screenshots/categorias.png)

---

## Stack

### Backend
| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem principal |
| Spring Boot | Framework web |
| Spring Web | API REST |
| Spring Data JPA | Persistência de dados |
| PostgreSQL | Banco de dados relacional |
| Flyway | Versionamento de migrations do banco |
| Maven | Gerenciador de dependências |
| JUnit 5 | Testes unitários automatizados |

### Frontend
| Tecnologia | Uso |
|---|---|
| Angular | Framework SPA |
| TypeScript | Linguagem principal |
| HTML / CSS | Interface |

### DevOps
| Tecnologia | Uso |
|---|---|
| GitHub Actions | CI/CD com workflow de testes automatizados |

---

## 🧠 Decisões Técnicas

Algumas escolhas do projeto foram feitas com foco em escalabilidade, organização e boas práticas de desenvolvimento.

### 🌍 Suporte a múltiplas moedas

Produtos podem ser cadastrados em BRL, USD ou EUR, com conversão automática para reais.

**Motivação:**
- Simular cenários reais de importação
- Permitir análise financeira mais completa
- Desacoplar moeda de origem do custo final

### 📦 Flyway — Versionamento do Banco de Dados

Utilizei o Flyway para versionamento das migrations do banco de dados.

**Motivação:**
- Garantir controle de versão do schema
- Evitar inconsistência entre ambientes
- Facilitar evolução do banco de forma segura

### Versionamento Semântico Automatizado (GitHub Actions)

O versionamento do projeto é realizado automaticamente utilizando GitHub Actions com base em versionamento semântico.

**Motivação:**
- Padronizar a evolução das versões do projeto
- Evitar versionamento manual e erros humanos
- Gerar histórico claro de mudanças (features, correções, refactors)

---

## Funcionalidades

- **CRUD completo de Produtos** — criação, listagem, edição e exclusão
- **CRUD completo de Categorias** — criação, listagem, edição e exclusão
- **Simulação de Estratégias de Preço** — cálculo de preço sugerido com base em margem de lucro, impostos e elasticidade de demanda
- **Análise financeira visual** — composição de custo, margem e imposto com gráfico interativo
- **API REST** documentada e integrada ao frontend Angular (SPA)

---

## Como executar localmente

### Pré-requisitos

- Java 17+
- Node.js 18+ e npm
- PostgreSQL rodando localmente
- Angular CLI: `npm install -g @angular/cli`

### 1. Configurar o banco de dados

Crie um banco PostgreSQL e configure as credenciais no `application.properties` (ou `application.yml`) do backend:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pricelab
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

As migrations do Flyway serão aplicadas automaticamente ao iniciar o backend.

### 2. Iniciar o Backend

```bash
cd backend
./mvnw spring-boot:run
```

API disponível em: `http://localhost:8080`

### 3. Iniciar o Frontend

```bash
cd frontend
npm install
ng serve
```

Aplicação disponível em: `http://localhost:4200`

---

## Testes

Os testes unitários rodam com JUnit 5 e são executados automaticamente no pipeline de CI via GitHub Actions a cada push.

Como rodar localmente:

```bash
cd backend
./mvn test
```

---

## ⚙️ CI/CD — GitHub Actions

O projeto conta com um workflow de integração contínua que executa os testes automaticamente em cada push ou pull request para a branch `main`.

Arquivo de configuração: `.github/workflows/tests.yml`

---

## 📁 Estrutura do Projeto

```
pricelab/
├── backend/  
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       
│   │   │   └── resources/
│   │   │       ├── db/migration/  
│   │   │       └── application.properties
│   │   └── test/        
│   └── pom.xml
├── frontend/          
│   ├── src/
│   │   └── app/
│   └── package.json
└── README.md
```

---

## 📌 Status do Projeto

🟡 **Em desenvolvimento**

| Funcionalidade | Status |
|---|---|
| CRUD de Categorias | ✅ Concluído |
| CRUD de Produtos | ✅ Concluído |
| Simulação de Estratégias de Preço | ✅ Concluído |
| Importação e Moeda | ✅ Concluído |
| Integração Angular + REST API | ✅ Concluído |
| GitHub Actions (CI com testes) | ✅ Concluído |
| Testes unitários (JUnit) | ✅ Concluído |
| Autenticação JWT | 🔲 Planejado |
| Filtros e paginação | 🔲 Planejado |
| Validações visuais no frontend | 🔲 Planejado |

---

## 👤 Autor

**Felipe Boos**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/felipe-boos-922380241/)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/FelipeBoos)
