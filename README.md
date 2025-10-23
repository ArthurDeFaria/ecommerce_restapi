# 📦 API REST de E-commerce

![Java](https://img.shields.io/badge/Java-21%2B-red?style=for-the-badge&logo=openjdk) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?style=for-the-badge&logo=spring) ![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=for-the-badge) ![License](https://img.shields.io/badge/License-MIT-purple?style=for-the-badge) ![CI/CD](https://github.com/ArthurDeFaria/ecommerce_restapi/actions/workflows/build.yml/badge.svg)

## 📜 Descrição

Esta é uma API REST robusta desenvolvida em Java com Spring Boot, projetada para servir como o backend completo para uma plataforma de E-commerce moderna. Ela gerencia todo o ciclo de vida da loja virtual, desde o cadastro e autenticação de usuários, catálogo de produtos, carrinho de compras, finalização de pedidos, até integrações essenciais com sistemas de pagamento e cálculo de frete.

O principal objetivo deste projeto é fornecer uma base sólida, segura, testada e escalável para aplicações de e-commerce, implementando as melhores práticas de desenvolvimento backend.

## ✨ Funcionalidades Principais

* **Autenticação e Autorização:** Cadastro de usuários, login seguro com JWT (JSON Web Tokens) e controle de acesso baseado em papéis (USER, MANAGER, ADMIN).
* **Gestão de Produtos:** CRUD completo para produtos, busca por categoria e pesquisa por nome.
* **Carrinho de Compras:** Adição, remoção, atualização de quantidade e limpeza do carrinho por usuário.
* **Gestão de Pedidos:** Criação de pedidos a partir do carrinho, listagem de pedidos (geral e por usuário).
* **Integração de Pagamento (Mercado Pago):** Geração de preferência de pagamento, redirecionamento para checkout externo e recebimento de status via webhook para atualização do pedido.
* **Cálculo de Frete (SuperFrete):** Cotação de frete em tempo real com base nos produtos e CEP de destino.
* **Gestão de Stock:** Abate automático de stock após confirmação de pagamento.
* **Outros:** Gestão de Endereços, Favoritos e Avaliações de Produtos.
* **Testes Automatizados:** Suíte de testes unitários e de integração abrangente.
* **Migrações de Base de Dados:** Gerenciamento do schema da base de dados com Flyway.
* **Containerização:** Suporte completo a Docker e Docker Compose para fácil execução.
* **CI/CD:** Pipeline básico de Integração Contínua com GitHub Actions para build e teste automáticos.

## 🚀 Tecnologias Utilizadas

* **Linguagem:** Java 21+
* **Framework Principal:** Spring Boot 3.x
* **Persistência:** Spring Data JPA / Hibernate
* **Segurança:** Spring Security (com autenticação JWT)
* **Base de Dados:** PostgreSQL 14+ (Produção/Desenvolvimento), H2 (Testes)
* **Migrações:** Flyway
* **Build:** Maven
* **Documentação API:** SpringDoc OpenAPI (Swagger UI)
* **Requisições HTTP (Cliente):** Spring WebFlux WebClient (para SuperFrete)
* **Containerização:** Docker, Docker Compose
* **Utilitários:** Lombok

## 🛠️ Guia de Instalação e Execução

Existem duas formas principais de executar o projeto: localmente ou via Docker.

### Pré-requisitos Comuns

* Git
* Docker e Docker Compose (Recomendado para facilidade)
* JDK 21+ (Se for rodar localmente)
* Maven 3.8+ (Se for rodar localmente)
* Cliente PostgreSQL (Opcional, para inspecionar a base de dados)

### Opção 1: Execução com Docker (Recomendado)

Esta é a forma mais simples e rápida, pois gerencia a base de dados e a aplicação automaticamente.

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/ArthurDeFaria/ecommerce_restapi.git](https://github.com/ArthurDeFaria/ecommerce_restapi.git)
    cd ecommerce_restapi
    ```
2.  **Configure as variáveis de ambiente:**
    * Copie o ficheiro `.env.example` para `.env`:
        ```bash
        cp .env.example .env
        ```
    * Edite o ficheiro `.env` e preencha **todas** as variáveis com os seus tokens reais e segredos. Certifique-se de que `DATASOURCE_USERNAME` e `DATASOURCE_PASSWORD` estão definidos (podem ser `user`/`password` para o ambiente Docker).
3.  **Execute o Docker Compose:**
    ```bash
    docker-compose up --build
    ```
    * Na primeira execução (ou após alterações no código), o `--build` é importante para construir a imagem da API.
    * Aguarde o download da imagem do PostgreSQL, a construção da imagem da API e o arranque de ambos os containers. O Flyway aplicará as migrações na base de dados automaticamente.
4.  **Acesse a API:** A API estará disponível em `http://localhost:8080`.

### Opção 2: Execução Local (Requer Instalação Manual)

1.  **Clone o repositório:** (Igual ao passo 1 do Docker)
2.  **Configure as variáveis de ambiente:** (Igual ao passo 2 do Docker, mas **certifique-se** de que a variável `DATASOURCE_URL` no `.env` aponta para a sua instância PostgreSQL local ou remota corretamente, incluindo utilizador e password se necessário, ou use `DATASOURCE_USERNAME`/`DATASOURCE_PASSWORD`).
3.  **Prepare a Base de Dados PostgreSQL:**
    * Certifique-se de ter uma instância PostgreSQL 14+ a rodar.
    * Crie uma base de dados vazia (ex: `ecommerce_db`).
    * Configure o utilizador e password correspondentes no seu ficheiro `.env`.
4.  **Execute a Aplicação com Maven Wrapper:**
    * No terminal, na raiz do projeto, execute:
        ```bash
        ./mvnw spring-boot:run
        ```
    * O Maven irá compilar o código, baixar dependências e iniciar a aplicação. O Flyway tentará conectar-se à base de dados configurada no `.env` e aplicar as migrações.
5.  **Acesse a API:** A API estará disponível em `http://localhost:8080`.

## ⚙️ Configuração (Variáveis de Ambiente)

A aplicação utiliza variáveis de ambiente para configurações sensíveis (tokens, senhas) e URLs. Crie um ficheiro `.env` na raiz do projeto baseado no `.env.example` e preencha os seguintes valores:

* `DATASOURCE_URL`: A URL JDBC completa para sua base de dados PostgreSQL (usada principalmente para execução local sem Docker).
* `DATASOURCE_DB_NAME`: Nome da base de dados (usado pelo Docker Compose).
* `DATASOURCE_USERNAME`: Usuário da base de dados.
* `DATASOURCE_PASSWORD`: Senha da base de dados.
* `JWT_SECRET`: Uma chave secreta longa e segura para assinar os tokens JWT.
* `SUPER_FRETE_TOKEN`: Seu token de API da SuperFrete.
* `MERCADO_PAGO_TOKEN`: Seu token de acesso do Mercado Pago (geralmente de desenvolvedor/sandbox).
* `MERCADO_PAGO_NOTIFICATION_URL`: A URL **pública** onde o Mercado Pago enviará as notificações de webhook (use `ngrok` ou similar durante o desenvolvimento local/Docker).

## 🗃️ Base de Dados

* **SGBD Principal:** PostgreSQL 14+
* **Flexibilidade:** Graças ao uso de Spring Data JPA/Hibernate, a aplicação pode ser adaptada para outros bancos de dados relacionais (MySQL, MariaDB, SQL Server, Oracle) com alterações mínimas:
    1.  Adicione a dependência do driver JDBC correspondente no `pom.xml`.
    2.  Atualize a `DATASOURCE_URL` e `spring.jpa.database-platform` no `application-dev.properties`.
    3.  Ajuste os scripts de migração do Flyway para a sintaxe SQL específica do novo banco, se necessário.
* **Gerenciamento de Schema:** O schema da base de dados é gerenciado exclusivamente pelo **Flyway**. As migrações SQL versionadas encontram-se em `src/main/resources/db/migration`. A propriedade `spring.jpa.hibernate.ddl-auto` **não** é utilizada para ambientes de desenvolvimento/produção.

## 🔒 Autenticação

A API utiliza **JWT (JSON Web Token)** para autenticação.
1.  Obtenha um token através do endpoint `POST /auth/login`.
2.  Para aceder a endpoints protegidos, inclua o token no cabeçalho `Authorization` de cada requisição:
    `Authorization: Bearer <seu_token_jwt>`

## 📚 Documentação da API (Swagger)

A API possui documentação interativa gerada automaticamente com SpringDoc OpenAPI (Swagger UI). Após iniciar a aplicação (localmente ou via Docker), acesse:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Lá, você pode explorar todos os endpoints, ver os modelos de dados (DTOs) e até mesmo testar as requisições diretamente pelo navegador (lembre-se de usar o botão "Authorize" para adicionar seu token JWT para endpoints protegidos).

## 📌 Endpoints da API

A tabela abaixo lista os principais endpoints disponíveis. Consulte o Swagger UI para detalhes completos sobre parâmetros, corpos de requisição/resposta e códigos de status.

| Método | Endpoint                      | Descrição                                 | Acesso                 | Status       |
| :----- | :---------------------------- | :---------------------------------------- | :--------------------- | :----------- |
| POST   | /auth/registro            | Cria uma nova conta de usuário.           | Público                | ✅ Pronto    |
| POST   | /auth/login               | Autentica um usuário e retorna um token.  | Público                | ✅ Pronto    |
| GET    | /usuarios/info            | Obtém informações do usuário logado.      | USER, MANAGER, ADMIN   | ✅ Pronto    |
| PUT    | /usuarios/info            | Atualiza informações do usuário logado.   | USER, MANAGER, ADMIN   | ✅ Pronto    |
| DELETE | /usuarios/info            | Deleta o usuário logado.                  | USER, MANAGER, ADMIN   | ✅ Pronto    |
| GET    | /usuarios/{id}            | Obtém informações de um usuário.          | MANAGER, ADMIN         | ✅ Pronto    |
| PUT    | /usuarios/{id}            | Atualiza dados de um usuário.             | MANAGER, ADMIN         | ✅ Pronto    |
| DELETE | /usuarios/{id}            | Deleta qualquer conta.                    | ADMIN                  | ✅ Pronto    |
| POST   | /auth/registro/adm        | Cria conta com permissões de ADMIN.       | ADMIN                  | ✅ Pronto    |
| GET    | /usuarios                 | Lista todos os usuários.                  | ADMIN                  | ✅ Pronto    |
| POST   | /enderecos/info           | Cria endereço para o usuário logado.      | USER, MANAGER, ADMIN   | ✅ Pronto    |
| GET    | /enderecos/info           | Lista endereços do usuário logado.        | USER, MANAGER, ADMIN   | ✅ Pronto    |
| GET    | /enderecos/info/{id}      | Obtém endereço do usuário logado.         | USER, MANAGER, ADMIN   | ✅ Pronto    |
| PUT    | /enderecos/info/{id}      | Atualiza endereço do usuário logado.      | USER, MANAGER, ADMIN   | ✅ Pronto    |
| DELETE | /enderecos/info/{id}      | Remove endereço do usuário logado.        | USER, MANAGER, ADMIN   | ✅ Pronto    |
| GET    | /enderecos/usuario/{id}   | Lista endereços de um usuário.            | MANAGER, ADMIN         | ✅ Pronto    |
| POST   | /carrinho/adicionar        | Adiciona um produto ao carrinho.          | USER, MANAGER, ADMIN   | ✅ Pronto    |
| GET    | /carrinho/info             | Obtém os itens do carrinho.               | USER, MANAGER, ADMIN   | ✅ Pronto    |
| PUT    | /carrinho/atualizar/{itemId} | Atualiza a quantidade de um item.       | USER, MANAGER, ADMIN   | ✅ Pronto    |
| DELETE | /carrinho/remover/{itemId} | Remove um item do carrinho.             | USER, MANAGER, ADMIN   | ✅ Pronto    |
| DELETE | /carrinho/limpar           | Esvazia o carrinho do usuário.            | USER, MANAGER, ADMIN   | ✅ Pronto    |
| GET    | /produtos                     | Lista todos os produtos.                  | Público                | ✅ Pronto    |
| GET    | /produtos/categoria/{categoria} | Lista produtos por categoria.             | Público                | ✅ Pronto    |
| GET    | /produtos/search              | Busca produtos por nome.                  | Público                | ✅ Pronto    |
| GET    | /produtos/{id}                | Obtém um produto específico.              | Público                | ✅ Pronto    |
| POST   | /produtos                     | Cria um novo produto.                     | MANAGER, ADMIN         | ✅ Pronto    |
| PUT    | /produtos                     | Atualiza um produto existente.            | MANAGER, ADMIN         | ✅ Pronto    |
| DELETE | /produtos/{id}                | Remove um produto.                        | ADMIN                  | ✅ Pronto    |
| POST   | /favoritos             | Adiciona um produto aos favoritos.        | USER, MANAGER, ADMIN   | ✅ Pronto    |
| GET    | /favoritos/info        | Lista os favoritos do usuário.            | USER, MANAGER, ADMIN   | ✅ Pronto    |
| DELETE | /favoritos/{id}        | Remove um produto dos favoritos.          | USER, MANAGER, ADMIN   | ✅ Pronto    |
| POST   | /pedidos/finalizar           | Cria um novo pedido a partir do carrinho. | USER, MANAGER, ADMIN   | ✅ Pronto    |
| GET    | /pedidos/info                | Lista os pedidos do usuário logado.       | USER, MANAGER, ADMIN   | ✅ Pronto    |
| GET    | /pedidos/{id}                | Obtém detalhes de um pedido.              | USER, MANAGER, ADMIN   | ✅ Pronto    |
| GET    | /pedidos                     | Lista todos os pedidos do sistema.        | MANAGER, ADMIN         | ✅ Pronto    |
| GET    | /pagamentos/{Id}       | Obtém o status do pagamento de um pedido. | USER, MANAGER, ADMIN   | ✅ Pronto    |
| POST   | /pagamentos/webhook    | Recebe notificações do Mercado Pago.      | Público (Webhook)      | ✅ Pronto    |
| POST   | /envios/cotarfrete     | Cota o valor do frete para um CEP.        | Público                | ✅ Pronto    |
| GET    | /envios/{Id}           | Consulta o status do envio.               | USER, MANAGER, ADMIN   | ⏳ Planejado |
| PUT    | /envios/{Id}           | Atualiza o status do envio.               | MANAGER, ADMIN         | ⏳ Planejado |
| GET    | /cupons/{codigo}       | Verifica a validade de um cupom.          | USER, MANAGER, ADMIN   | ⏳ Planejado |
| GET    | /cupons                | Lista todos os cupons disponíveis.        | MANAGER, ADMIN         | ⏳ Planejado |
| POST   | /cupons                | Cria um novo cupom.                       | ADMIN                  | ⏳ Planejado |
| DELETE | /cupons/{id}           | Exclui um cupom.                          | ADMIN                  | ⏳ Planejado |
| POST   | /avaliacoes            | Cria uma avaliação para um produto.       | USER, MANAGER, ADMIN   | ✅ Pronto    |
| GET    | /avaliacoes/{produtoId} | Lista as avaliações de um produto.        | Público                | ✅ Pronto    |

### Exemplos de Requisição/Resposta

#### 1. Registrar Novo Usuário

* **Endpoint:** `POST /auth/registro`
* **Acesso:** Público
* **Requisição (Body):**
    ```json
    {
      "nome": "João Silva",
      "email": "joao.silva@example.com",
      "senha": "senhaSegura123",
      "cpf": "12345678900",
      "dataNascimento": "10/05/1990",
      "telefone": "19912345678"
    }
    ```
* **Resposta (Sucesso):** `200 OK` (Corpo vazio)
* **Resposta (Erro - Email Duplicado):** `400 Bad Request`

#### 2. Autenticar Usuário (Login)

* **Endpoint:** `POST /auth/login`
* **Acesso:** Público
* **Requisição (Body):**
    ```json
    {
      "email": "joao.silva@example.com",
      "senha": "senhaSegura123"
    }
    ```
* **Resposta (Sucesso):** `200 OK`
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." // Seu token JWT
    }
    ```
* **Resposta (Erro - Credenciais Inválidas):** `403 Forbidden` (Ou outro status, dependendo da configuração de segurança)

#### 3. Listar Produtos

* **Endpoint:** `GET /produtos`
* **Acesso:** Público
* **Requisição:** N/A (Sem corpo)
* **Resposta (Sucesso):** `200 OK`
    ```json
    [
      {
        "id": 1,
        "nome": "Smartphone XPTO",
        "descricao": "Ótimo smartphone com câmera tripla.",
        "preco": 1999.90,
        "categoria": "Eletrônicos",
        "imagemUrl": "[http://example.com/images/sphone.jpg](http://example.com/images/sphone.jpg)",
        "avaliacoes": [
            // ... lista de objetos Review
        ],
        "peso": 0.180,
        "altura": 15.0,
        "largura": 7.0,
        "comprimento": 0.8
      },
      {
        "id": 2,
        "nome": "Notebook ABC",
        // ... outros detalhes
      }
      // ... outros produtos
    ]
    ```

#### 4. Adicionar Item ao Carrinho

* **Endpoint:** `POST /carrinho/adicionar`
* **Acesso:** USER, MANAGER, ADMIN (Requer Bearer Token)
* **Requisição (Body):**
    ```json
    {
      "idProduto": 1, // ID do produto a adicionar
      "quantidade": 2
    }
    ```
* **Resposta (Sucesso):** `200 OK` (Retorna o estado atualizado do carrinho)
    ```json
    {
      "id": 123, // ID do carrinho
      "itens": [
        {
          "id": 456, // ID do item no carrinho
          "nome": "Smartphone XPTO",
          "preco": 1999.90,
          "imagem_url": "[http://example.com/images/sphone.jpg](http://example.com/images/sphone.jpg)",
          "quantidade": 2
        }
        // ... outros itens no carrinho
      ]
    }
    ```

## ✅ Executando os Testes

Para garantir a qualidade e a estabilidade do código, execute a suíte completa de testes unitários e de integração:

```bash
./mvnw clean verify
```

## 🤝 Como Contribuir 

Contribuições são bem vindas! Siga esses passos:

1. Faça um Fork do projeto.
2. Crie sua Feature Branch (```git checkout -b feature/NovaFuncionalidade```).
3. Faça o Commit de suas alterações (```git commit -m 'feat: Adiciona NovaFuncionalidade'```).
4. Faça o Push para a Branch (```git push origin feature/NovaFuncionalidade```).
5. Abra um Pull Request.

## 📜 Licença

Distribuído sob a licença MIT. Veja ```LICENSE.md``` para mais informações.

## Observações

**Owner/Grant no SQL:** Removi `OWNER TO` e `GRANT` do script Flyway para compatibilidade com H2, para produção PostgreSQL, pode ser necessário ajustar permissões manualmente ou adicionar comandos `GRANT` específicos se o utilizador da aplicação não for o dono do schema. No entanto, para a maioria das configurações, não será preciso.