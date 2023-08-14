# Projeto Assíncrono de Processamento de Postagens

Este projeto tem como objetivo criar um aplicativo que busca assincronamente postagens de uma API externa, enriquece essas postagens com dados de comentários e mantém um registro de atualizações de processamento. Os clientes poderão pesquisar postagens e o histórico de estados por meio da API imediatamente.

## Funcionalidades da API

### 1. Processar Postagem

- **Descrição:** Processa uma postagem.
- **Método:** POST
- **Caminho:** `/posts/{postId}`
- **Requisitos:**
  - `postId` deve ser um número entre 1 e 100.
  - `postId` existente não deve ser aceito.

### 2. Desabilitar Postagem

- **Descrição:** Desabilita uma postagem que está no estado "ENABLED".
- **Método:** DELETE
- **Caminho:** `/posts/{postId}`
- **Requisitos:**
  - `postId` deve ser um número entre 1 e 100.
  - `postId` deve estar no estado "ENABLED".

### 3. Reprocessar Postagem

- **Descrição:** Reprocessa uma postagem que está nos estados "ENABLED" ou "DISABLED".
- **Método:** PUT
- **Caminho:** `/posts/{postId}`
- **Requisitos:**
  - `postId` deve ser um número entre 1 e 100.
  - `postId` deve estar nos estados "ENABLED" ou "DISABLED".

### 4. Consultar Postagens

- **Descrição:** Fornece uma lista de postagens.
- **Método:** GET
- **Caminho:** `/posts`

## Requisitos Técnicos do Projeto

- [x] O aplicativo deve ser executado na porta 8080.
- [x] O banco de dados deve ser um banco de dados H2 embutido.
- [x] A configuração `spring.jpa.hibernate.ddl-auto` deve ser definida como `create-drop`.
- [x] Organize o código em classes e utilize boas práticas de programação.
- [x] Se houver um message broker, ele também deve ser embutido.

## Especificações de Desenvolvimento

- **Versão Java:** 17.0.0
- **IDE:** IntelliJ IDEA Community Edition 2023.1.3
- **Fonte de Dados Externa:** [https://jsonplaceholder.typicode.com](https://jsonplaceholder.typicode.com)
- **Sistema Operacional:** Windows 10 Pro x64

## Requisitos

1. Java SE Development Kit 17.0.0 - [https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
