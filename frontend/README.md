# Gestão de Produtos

Sistema de gestão de produtos e categorias desenvolvido com **Spring Boot** no backend e **Angular** no frontend.

A aplicação permite o gerenciamento completo de **categorias** e **produtos**, por meio de uma **API REST** integrada a uma interface web no estilo **SPA (Single Page Application)**.

O projeto foi organizado como um **monorepo**, mantendo backend e frontend no mesmo repositório para facilitar a evolução, o versionamento e a integração da aplicação full stack.

---

## Estrutura do Projeto

```bash
gestao-de-produtos
│
├── backend/                      # API REST em Spring Boot
│   ├── src/main/java/com/felipeboos/gestao_produtos
│   │
│   ├── controller/               # Endpoints REST
│   ├── service/                  # Regras de negócio
│   ├── repository/               # Acesso a dados com Spring Data JPA
│   ├── entity/                   # Entidades JPA
│   ├── dto/                      # Objetos de transferência de dados
│   │   ├── categoria/
│   │   └── produto/
│   └── exception/                # Tratamento de exceções
│
├── frontend/                     # Aplicação Angular
│   ├── src/app
│   │   ├── core/
│   │   ├── shared/
│   │   ├── features/
│   │   └── layout/
│
└── README.md
Funcionalidades
Backend

API REST para gerenciamento de categorias

API REST para gerenciamento de produtos

Estrutura em camadas

Uso de DTOs para entrada, saída e atualização de dados

Tratamento de exceções

Integração com banco de dados via Spring Data JPA

Frontend

Listagem de categorias

Cadastro de categorias

Edição de categorias

Exclusão de categorias

Listagem de produtos

Cadastro de produtos

Edição de produtos

Exclusão de produtos

Layout inicial em estilo dashboard

Sidebar fixa de navegação

Estrutura visual com páginas em card

Tecnologias Utilizadas
Backend

Java

Spring Boot

Spring Web

Spring Data JPA

Maven

REST API

DTO Pattern

Frontend

Angular

TypeScript

HTML

CSS

Arquitetura Backend

O backend foi desenvolvido em camadas, seguindo a estrutura:

Controller → Service → Repository → Database
Controller

Responsável pelos endpoints da API.

Exemplos:

CategoriaController

ProdutoController

Service

Responsável pelas regras de negócio.

Exemplos:

CategoriaService

ProdutoService

Repository

Responsável pela comunicação com o banco de dados via Spring Data JPA.

Exemplos:

CategoriaRepository

ProdutoRepository

Entity

Representam as entidades persistidas no banco de dados.

Principais entidades:

Categoria

Produto

EntidadeBase

DTO

Os DTOs foram utilizados para evitar expor diretamente as entidades da aplicação.

Tipos utilizados:

RequestDTO → dados recebidos na requisição

ResponseDTO → dados retornados pela API

UpdateDTO → dados utilizados na atualização

Exemplos:

CategoriaRequestDTO
CategoriaResponseDTO
CategoriaUpdateDTO

ProdutoRequestDTO
ProdutoResponseDTO
ProdutoUpdateDTO
Frontend

O frontend foi desenvolvido com Angular, utilizando uma estrutura voltada para organização e reaproveitamento de componentes.

A interface foi pensada como um painel administrativo simples, com:

navegação lateral fixa

área principal de conteúdo

páginas organizadas em cards

integração com a API REST do backend

Layout Atual

O frontend está passando por uma etapa de estilização inicial com foco em melhorar a organização visual da aplicação.

Atualmente, o sistema possui:

sidebar fixa à esquerda

conteúdo principal à direita

fundo geral em tom suave

páginas exibidas em cards brancos

base de layout inspirada em dashboards administrativos

Endpoints da API
Produtos
GET    /produtos
GET    /produtos/{id}
POST   /produtos
PUT    /produtos/{id}
DELETE /produtos/{id}
Categorias
GET    /categorias
GET    /categorias/{id}
POST   /categorias
PUT    /categorias/{id}
DELETE /categorias/{id}
Como Executar o Projeto
1. Clonar o repositório
git clone https://github.com/seu-usuario/gestao-de-produtos.git
cd gestao-de-produtos
Executando o Backend

Entre na pasta do backend:

cd backend

Execute o projeto:

Linux / Mac
./mvnw spring-boot:run
Windows
mvnw.cmd spring-boot:run

O backend ficará disponível em:

http://localhost:8080
Executando o Frontend

Abra outro terminal e entre na pasta do frontend:

cd frontend

Instale as dependências:

npm install

Execute a aplicação Angular:

ng serve

O frontend ficará disponível em:

http://localhost:4200
Comunicação Frontend / Backend

O frontend consome a API REST do backend por meio de requisições HTTP.

Exemplos de operações já implementadas na interface:

buscar categorias

cadastrar categorias

editar categorias

excluir categorias

buscar produtos

cadastrar produtos

editar produtos

excluir produtos

Histórico de Desenvolvimento

Algumas etapas importantes do desenvolvimento:

Commit inicial
primeiro commit
Implementação da entidade Categoria
feat(Entity): add Categoria entity, repository, service and controller
Implementação de DTOs para Categoria
feat(dto): add CategoriaRequestDTO
feat(dto): add CategoriaResponseDTO
Implementação de DTOs para Produto
feat(dto): add RequestDTO, ResponseDTO and UpdateDTO for entity Produto
Estruturação do projeto em monorepo

Separação da aplicação em:

backend/

frontend/

Implementação do CRUD de Categorias no frontend
feat(frontend): implement categoria CRUD
Implementação do CRUD de Produtos no frontend
feat(frontend): implement produto CRUD
Implementação do layout inicial do dashboard
feat(layout): implement initial dashboard layout with sidebar and page container
Objetivo do Projeto

Este projeto tem como objetivo praticar e consolidar conhecimentos em:

desenvolvimento backend com Spring Boot

arquitetura REST

Spring Data JPA

DTO Pattern

desenvolvimento frontend com Angular

integração full stack

organização de projetos em monorepo

construção de interfaces administrativas

consumo de API REST no frontend

Melhorias Futuras

Algumas melhorias que podem ser implementadas futuramente:

aprimoramento visual das tabelas

estilização dos botões e formulários

responsividade

validações visuais no frontend

mensagens de sucesso e erro para o usuário

paginação e filtros

autenticação e autorização

documentação da API com Swagger

Autor

Felipe Boos

Projeto desenvolvido para estudos de desenvolvimento Full Stack com Java e Angular.