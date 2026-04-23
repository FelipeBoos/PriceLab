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

###  Cadastro de categorias
![Categorias](docs/screenshots/categorias_cadastro_video.gif)
 
### Listagem de categorias
![Categorias](docs/screenshots/categorias_listagem.png)

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

## Decisões Técnicas

Algumas escolhas do projeto foram feitas com foco em escalabilidade, organização e boas práticas de desenvolvimento.

### <img src="docs/screenshots/icons/dollar-symbol.png" width="25" />  Suporte a múltiplas moedas

Produtos podem ser cadastrados em BRL, USD ou EUR, com conversão automática para reais.

**Foi utilizada a API [Frankfurter](https://www.frankfurter.dev/) para buscar valores atualizados para cotação das moedas estrangeiras.**

**Motivação:**
- Abstrair a moeda como um atributo do produto, desacoplando o valor de entrada do custo final em BRL
- Centralizar a lógica de conversão no backend, evitando cálculos distribuídos no frontend
- Consumir cotações em tempo real via API externa para não depender de valores estáticos

### <img src="docs/screenshots/icons/flyway.png" alt="Flyway" width="25" />  Flyway — Versionamento do Banco de Dados

Utilizei o Flyway para versionamento das migrations do banco de dados.

**Motivação:**
- Garantir controle de versão do schema
- Evitar inconsistência entre ambientes
- Facilitar evolução do banco de forma segura

### <img src="docs/screenshots/icons/docker.png" alt="Docker" width="25" />  Docker - Containerização da aplicação

A aplicação foi totalmente containerizada, incluindo backend, frontend e banco de dados.

**Motivação:**
- Padronizar o ambiente de execução
- Facilitar o setup do projeto
- Melhorar a portabilidade

### <img src="docs/screenshots/icons/github.png" alt="Docker" width="25" /> Versionamento Semântico Automatizado (GitHub Actions)

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

## Testes

Os testes unitários rodam com JUnit 5 e são executados automaticamente no pipeline de CI via GitHub Actions a cada push.

Como rodar localmente:

```bash
cd backend
./mvn test
```

---

## <img src="docs/screenshots/icons/github.png" alt="Docker" width="25" />  CI/CD — GitHub Actions

O projeto conta com um workflow de integração contínua que executa os testes automaticamente em cada push ou pull request para a branch `main`.

Workflows disponíveis:

- backend-ci.yml
- frontend-ci.yml
- docker-ci.yml
- release.yml

---

## Estrutura do Projeto

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

## Status do Projeto

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

# Como executar o projeto

Existem três formas de executar o projeto: Acessando ao link da demo online, executando pelo Docker ou executando localmente na sua máquina.

## <img src="docs/screenshots/icons/internet.png" alt="Internet" width="25" /> Acessando a demo online

Você pode acessar a aplicação pelo link: [Price Lab](https://pricelab-app.onrender.com/)

Frontend: https://pricelab-app.onrender.com  
Backend API: https://pricelab-api.onrender.com

### Exemplos de endpoints do backend: 
- https://pricelab-api.onrender.com/categorias
- https://pricelab-api.onrender.com/produtos
- https://pricelab-api.onrender.com/estrategias-preco

> Nota: o ambiente de demonstração utiliza plano gratuito do Render, então a primeira requisição após inatividade pode demorar alguns minutos.

## <img src="docs/screenshots/icons/docker.png" alt="Docker" width="25" /> Executando com Docker

### Pré-requisitos

- Docker Desktop instalado e em execução

### 1. Clonar o repositório

Clone o repositório na sua máquina executando os comandos abaixo:

```bash
git clone https://github.com/FelipeBoos/PriceLab.git
```

### 2. Criar o arquivo .env

Copie o arquivo **.env.example** para **.env** executando o comando abaixo na raiz do projeto:

```bash
cd PriceLab
cp .env.example .env
```

> ⚠️ O arquivo `.env` é obrigatório. Sem ele, o banco de dados não será iniciado corretamente.
Obs: Se você alterar as credenciais do banco depois da primeira inicialização, pode ser necessário recriar o volume com o comando **docker compose down -v**.

### 3. Subir toda a aplicação

Execute os comandos abaixo no diretório em que o repositório foi clonado para executar a aplicação:

```bash
docker compose up --build
```

### 4. Acessar a aplicação

Depois de subir a aplicação, os serviços ficam disponíveis em:

- Frontend: `http://localhost:4200`
- Backend: `http://localhost:8080`
- PostgreSQL: `localhost:5433`

### 5. Encerrar os containers

Execute o comando abaixo na raiz do projeto:

```bash
docker compose down
```

### 6. Resetar o banco de dados (opcional)

Se quiser iniciar com o banco limpo:

```bash
docker compose down -v
```

### Observações

- As migrations do Flyway são executadas automaticamente ao iniciar o backend
- Os dados do banco são persistidos em um volume Docker
- O frontend é servido em container com Nginx
- O backend consulta a API externa de câmbio para obter as cotações utilizadas nos cálculos de importação. Se quiser conferir a documentação do serviço, veja a [Frankfurter API](https://www.frankfurter.dev/).

---

## <img src="docs/screenshots/icons/computer.png" alt="Computer" width="25" /> Executando localmente

### Pré-requisitos

- Java 17+
- Node.js 18+ e npm
- PostgreSQL rodando localmente
- Angular CLI: `npm install -g @angular/cli`

### 1. Criar o banco de dados

Crie um banco PostgreSQL chamado pricelab.

### 2. Configurar variáveis de ambiente do backend

O backend lê as credenciais do banco por variáveis de ambiente. Exemplo no PowerShell:

```PowerShell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/pricelab"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="sua_senha"
```

As migrations do Flyway serão aplicadas automaticamente ao iniciar o backend.

### 3. Iniciar o Backend

Execute o comando abaixo no diretório em que o projeto foi clonado

```bash
cd backend
./mvnw spring-boot:run
```

API disponível em: `http://localhost:8080`

### 4. Iniciar o Frontend

```bash
cd frontend
npm install
ng serve
```

Aplicação disponível em: `http://localhost:4200`

---

## 👤 Autor

**Felipe Boos**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/felipe-boos-922380241/)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/FelipeBoos)