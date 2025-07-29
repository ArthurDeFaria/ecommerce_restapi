# 📦 API REST de E-commerce

Esta API permite gerenciar um e-commerce, incluindo usuários, produtos, carrinho de compras, pedidos, pagamentos e mais.

## 🚀 Tecnologias
- **Java 24**
- **Spring Boot**
- **Spring Security (JWT)**
- **JPA/Hibernate**
- **PostgreSQL**
- **Swagger (Documentação)**

## 📚 Swagger
A API conta com documentação interativa do Swagger, permitindo visualizar modelos de requisição/resposta e entender a estrutura da API.

### 🔗 Como acessar a documentação
Após iniciar o projeto localmente, acesse a URL abaixo no navegador:
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
> ⚠️ Certifique-se de que a aplicação esteja rodando e o perfil de desenvolvimento esteja ativado (`dev`). Caso esteja rodando em um servidor, acesse o endpoint `/swagger-ui/index.html`.

<!-- Banco de dados -->

## 📌 Perfis de Usuário
- **USER**: Pode criar conta, fazer login, gerenciar carrinho, pedidos e favoritos.
- **MANAGER**: Pode gerenciar produtos e pedidos.
- **ADMIN**: Pode gerenciar usuários, produtos, pedidos e cupons.

## 🔒 Autenticação
A API usa **JWT (JSON Web Token)** para autenticação. Endpoints protegidos exigem um **token Bearer** no cabeçalho `Authorization`.

## 📌 Endpoints

### 🧑 Usuários
- `POST /auth/registro` - Criar conta **(Público)**
- `POST /auth/login` - Autenticar usuário e gerar token JWT **(Público)**
- `GET /usuarios/info` - Obter informações do usuário logado **(USER, MANAGER, ADMIN)**
- `PUT /usuarios/info` - Atualizar informações do usuário logado **(USER, MANAGER, ADMIN)**
- `DELETE /usuarios/info` - Deletar usuário logado **(USER, MANAGER, ADMIN)**
- `GET /usuarios/{id}` - Obter informações de um usuário **(MANAGER, ADMIN)**
- `PUT /usuarios/{id}` - Atualizar dados de um usuário **(MANAGER, ADMIN)**
- `DELETE /usuarios/{id}` - Deletar qualquer conta **(ADMIN)**
- `POST /auth/registro/adm` - Criar conta com permissões de ADMIN **(ADMIN)**
- `GET /usuarios` - Listar todos os usuários **(ADMIN)**

| Método | Endpoint                  | Acesso                 | Status              |
|--------|---------------------------|------------------------|---------------------|
| POST   | /auth/registro            | Público                | ✅ Pronto           |
| POST   | /auth/login               | Público                | ✅ Pronto           |
| GET    | /usuarios/info            | USER, MANAGER, ADMIN   | ✅ Pronto           |
| PUT    | /usuarios/info            | USER, MANAGER, ADMIN   | ✅ Pronto           |
| DELETE | /usuarios/info            | USER, MANAGER, ADMIN   | ✅ Pronto           |
| GET    | /usuarios/{id}            | MANAGER, ADMIN         | ✅ Pronto           |
| PUT    | /usuarios/{id}            | MANAGER, ADMIN         | ✅ Pronto           |
| DELETE | /usuarios/{id}            | ADMIN                  | ✅ Pronto           |
| POST   | /auth/registro/adm        | ADMIN                  | ✅ Pronto           |
| GET    | /usuarios                 | ADMIN                  | ✅ Pronto           |

### 🏠 Endereços
- `POST /enderecos` - Criar um novo endereço **(MANAGER, ADMIN)**
- `GET /enderecos/{id}` - Obter um endereço específico **(MANAGER, ADMIN)**
- `GET /enderecos/usuario/{usuarioId}` - Listar endereços de um usuário qualquer **(MANAGER, ADMIN)**
- `PUT /enderecos/{id}` - Atualizar um endereço qualquer **(MANAGER, ADMIN)**
- `DELETE /enderecos/{id}` - Remover um endereço qualquer **(ADMIN)**
- `POST /enderecos/info` - Criar um novo endereço para o usuário logado **(USER, MANAGER, ADMIN)**
- `GET /enderecos/info/{id}` - Obter um endereço específico do usuário logado **(USER, MANAGER, ADMIN)**
- `GET /enderecos/info` - Listar endereços do usuário logado **(USER, MANAGER, ADMIN)**
- `PUT /enderecos/{id}` - Atualizar o endereço do usuário logado **(USER, MANAGER, ADMIN)**
- `DELETE /enderecos/{id}` - Remover o endereço do usuário logado **(USER, MANAGER, ADMIN)**


| Método | Endpoint                               | Acesso               | Status              |
|--------|----------------------------------------|----------------------|---------------------|
| POST   | /enderecos                             | MANAGER, ADMIN       | ✅ Pronto           |
| GET    | /enderecos/{id}                        | MANAGER, ADMIN       | ✅ Pronto           |
| GET    | /enderecos/usuarios/{usuarioId}        | MANAGER, ADMIN       | ✅ Pronto           |
| PUT    | /enderecos                             | MANAGER, ADMIN       | ✅ Pronto           |
| DELETE | /enderecos/{id}                        | ADMIN                | ✅ Pronto           |
| POST   | /enderecos/info                        | USER, MANAGER, ADMIN | ✅ Pronto           |
| GET    | /enderecos/info/{id}                   | USER, MANAGER, ADMIN | ✅ Pronto           |
| GET    | /enderecos/info                        | USER, MANAGER, ADMIN | ✅ Pronto           |
| PUT    | /enderecos/info                        | USER, MANAGER, ADMIN | ✅ Pronto           |
| DELETE | /enderecos/info/{id}                   | USER, MANAGER, ADMIN | ✅ Pronto           |



### 🛂 Carrinho de Compras
- `POST /carrinho/adicionar` - Adicionar produto ao carrinho **(USER, MANAGER, ADMIN)**
- `GET /carrinho/{usuarioId}` - Obter itens do carrinho **(USER, MANAGER, ADMIN)**
- `PUT /carrinho/atualizar/{itemId}` - Atualizar quantidade de um item **(USER, MANAGER, ADMIN)**
- `DELETE /carrinho/remover/{itemId}` - Remover item do carrinho **(USER, MANAGER, ADMIN)**
- `DELETE /carrinho/limpar` - Esvaziar carrinho **(USER, MANAGER, ADMIN)**

| Método | Endpoint                               | Acesso               | Status         |
|--------|----------------------------------------|----------------------|----------------|
| POST   | /carrinho/adicionar                    | USER, MANAGER, ADMIN | ✅ Pronto      |
| GET    | /carrinho/{usuarioId}                  | USER, MANAGER, ADMIN | ✅ Pronto      |
| PUT    | /carrinho/atualizar/{itemId}           | USER, MANAGER, ADMIN | ✅ Pronto      |
| DELETE | /carrinho/remover/{itemId}             | USER, MANAGER, ADMIN | ✅ Pronto      |
| DELETE | /carrinho/limpar                       | USER, MANAGER, ADMIN | ✅ Pronto      |

### 📦 Produtos
- `GET /produtos` - Listar todos os produtos **(Público)**
- `GET /produtos/categoria/{categoria}` - Listar produtos por categoria **(Público)**
- `GET /produtos/search` - Buscar produtos por nome **(Público)**
- `GET /produtos/{id}` - Obter um produto específico **(Público)**
- `POST /produtos` - Criar novo produto **(MANAGER, ADMIN)**
- `PUT /produtos` - Atualizar produto existente **(MANAGER, ADMIN)**
- `DELETE /produtos/{id}` - Remover um produto **(ADMIN)**

| Método | Endpoint                        | Acesso         | Status   |
| ------ | ------------------------------- | -------------- | -------- |
| GET    | /produtos                       | Público        | ✅ Pronto |
| GET    | /produtos/categoria/{categoria} | Público        | ✅ Pronto |
| GET    | /produtos/search                | Público        | ✅ Pronto |
| GET    | /produtos/{id}                  | Público        | ✅ Pronto |
| POST   | /produtos                       | MANAGER, ADMIN | ✅ Pronto |
| PUT    | /produtos                       | MANAGER, ADMIN | ✅ Pronto |
| DELETE | /produtos/{id}                  | ADMIN          | ✅ Pronto |

### ⭐ Favoritos
- `POST /favoritos` - Adicionar produto aos favoritos **(USER, MANAGER, ADMIN)**
- `GET /favoritos/{usuarioId}` - Listar favoritos do usuário **(USER, MANAGER, ADMIN)**
- `DELETE /favoritos/{id}` - Remover produto dos favoritos **(USER, MANAGER, ADMIN)**

| Método | Endpoint                               | Acesso               | Status        |
|--------|----------------------------------------|----------------------|---------------|
| POST   | /favoritos                             | USER, MANAGER, ADMIN | ⏳ Planejado   |
| GET    | /favoritos/{usuarioId}                 | USER, MANAGER, ADMIN | ⏳ Planejado   |
| DELETE | /favoritos/{id}                        | USER, MANAGER, ADMIN | ⏳ Planejado   |

### 🛒 Pedidos
- `POST /pedidos/finalizar` - Criar um novo pedido **(USER, MANAGER, ADMIN)**
- `GET /pedidos/{id}` - Obter detalhes de um pedido **(USER, MANAGER, ADMIN)**
- `GET /pedidos/usuario/{usuarioId}` - Listar pedidos do usuário **(USER, MANAGER, ADMIN)**
- `GET /pedidos` - Listar todos os pedidos **(MANAGER, ADMIN)**

| Método | Endpoint                               | Acesso               | Status        |
|--------|----------------------------------------|----------------------|---------------|
| POST   | /pedidos/finalizar                     | USER, MANAGER, ADMIN | ⏳ Planejado   |
| GET    | /pedidos/{id}                          | USER, MANAGER, ADMIN | ⏳ Planejado   |
| GET    | /pedidos/usuario/{usuarioId}           | USER, MANAGER, ADMIN | ⏳ Planejado   |
| GET    | /pedidos                               | MANAGER, ADMIN       | ⏳ Planejado   |

### 📋 Itens do Pedido
- `GET /itens-pedido/{pedidoId}` - Listar itens de um pedido **(USER, MANAGER, ADMIN)**

| Método | Endpoint                               | Acesso               | Status        |
|--------|----------------------------------------|----------------------|---------------|
| GET    | /itens-pedido/{pedidoId}               | USER, MANAGER, ADMIN | ⏳ Planejado   |

### 💳 Pagamento
- `POST /pagamentos` - Processar pagamento **(USER, MANAGER, ADMIN)**
- `GET /pagamentos/{pedidoId}` - Obter status do pagamento **(USER, MANAGER, ADMIN)**

| Método | Endpoint                               | Acesso               | Status        |
|--------|----------------------------------------|----------------------|---------------|
| POST   | /pagamentos                            | USER, MANAGER, ADMIN | ⏳ Planejado   |
| GET    | /pagamentos/{pedidoId}                 | USER, MANAGER, ADMIN | ⏳ Planejado   |

### 🚚 Envio
- `GET /envios/{pedidoId}` - Consultar status do envio **(USER, MANAGER, ADMIN)**
- `PUT /envios/{pedidoId}` - Atualizar status do envio **(MANAGER, ADMIN)**

| Método | Endpoint                               | Acesso               | Status        |
|--------|----------------------------------------|----------------------|---------------|
| GET    | /envios/{pedidoId}                     | USER, MANAGER, ADMIN | ⏳ Planejado |
| PUT    | /envios/{pedidoId}                     | MANAGER, ADMIN       | ⏳ Planejado   |

### 🎟️ Cupons de Desconto
- `POST /cupons` - Criar um novo cupom **(ADMIN)**
- `GET /cupons` - Listar cupons disponíveis **(MANAGER, ADMIN)**
- `GET /cupons/{codigo}` - Verificar se um cupom é válido **(USER, MANAGER, ADMIN)**
- `DELETE /cupons/{id}` - Excluir um cupom **(ADMIN)**

| Método | Endpoint                               | Acesso             | Status        |
|--------|----------------------------------------|--------------------|---------------|
| POST   | /cupons                                | ADMIN              | ⏳ Planejado   |
| GET    | /cupons                                | MANAGER, ADMIN     | ⏳ Planejado   |
| GET    | /cupons/{codigo}                       | USER, MANAGER, ADMIN | ⏳ Planejado |
| DELETE | /cupons/{id}                           | ADMIN              | ⏳ Planejado   |

### 📝 Avaliações de Produtos
- `POST /avaliacoes` - Criar uma avaliação para um produto **(USER, MANAGER, ADMIN)**
- `GET /avaliacoes/{produtoId}` - Listar avaliações de um produto **(Público)**

| Método | Endpoint                               | Acesso               | Status        |
|--------|----------------------------------------|----------------------|---------------|
| GET    | /avaliacoes/{produtoId}                | Público              | ✅ Pronto     |
| POST   | /avaliacoes                            | USER, MANAGER, ADMIN | ✅ Pronto     |

## 🛠️ Como Configurar o Projeto
1. Clone o repositório: `git clone <URL_DO_REPOSITORIO>`
2. Acesse a pasta do projeto: `cd nome-do-projeto`
3. Crie um arquivo `.env`
3. Configure as variáveis de ambiente no `.env` com base no `.env.example`
