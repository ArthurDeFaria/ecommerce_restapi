# 📦 API REST de E-commerce

![Java](https://img.shields.io/badge/Java-17%2B-red?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?style=for-the-badge&logo=spring)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-purple?style=for-the-badge)

API REST robusta para uma solução completa de E-commerce. Este projeto serve como um backend completo, gerenciando usuários, produtos, carrinho de compras, pedidos e pagamentos, com integrações prontas para serviços de frete e pagamento.

## 📋 Sumário

- [🛠️ Guia de Instalação e Execução](#️-guia-de-instalação-e-execução)
  - [Pré-requisitos](#pré-requisitos)
  - [Passos para Instalação](#passos-para-instalação)
- [🚀 Tecnologias](#-tecnologias)
- [🔗 Integrações](#-integrações)
  - [Integração com SuperFrete](#-integração-com-superfrete)
  - [Integração com Mercado Pago](#-integração-com-mercado-pago)
- [📚 Swagger (Documentação da API)](#-swagger-documentação-da-api)
- [🗃️ Banco de Dados](#️-banco-de-dados)
  - [⚙️ Configuração](#️-configuração)
  - [🔄 Compatibilidade](#-compatibilidade)
  - [🧱 Estrutura do Banco](#-estrutura-do-banco)
- [📌 Perfis de Usuário](#-perfis-de-usuário)
- [🔒 Autenticação](#-autenticação)
- [📌 Endpoints](#-endpoints)
  - [🧑 Usuários](#-usuários)
  - [🏠 Endereços](#-endereços)
  - [🛂 Carrinho de Compras](#-carrinho-de-compras)
  - [📦 Produtos](#-produtos)
  - [⭐ Favoritos](#-favoritos)
  - [🛒 Pedidos](#-pedidos)
  - [💳 Pagamento](#-pagamento)
  - [🚚 Envio](#-envio)
  - [🎟️ Cupons de Desconto](#-cupons-de-desconto)
  - [📝 Avaliações de Produtos](#-avaliações-de-produtos)
- [🤝 Como Contribuir](#-como-contribuir)
- [📜 Licença](#-licença)

## 🛠️ Guia de Instalação e Execução

### Pré-requisitos
- Java 17+
- Maven 3.8+
- PostgreSQL 14+ (ou uma instância Docker)
- Git

### Passos para Instalação
1. **Clone o repositório:**
   ```sh
   git clone [https://github.com/ArthurDeFaria/ecommerce_restapi.git](https://github.com/ArthurDeFaria/ecommerce_restapi.git)
   cd ecommerce_restapi
2. **Configure as variáveis de ambiente:**
  - Crie um arquivo .env na raiz do projeto.
  - Adicione as seguintes variáveis, substituindo pelos seus valores (Local ou Nuvem):
    ```sh
    DATASOURCE_URL="jdbc:postgresql://localhost:5432/nome_do_banco"
    DATASOURCE_USERNAME="seu_usuario_postgres"
    DATASOURCE_PASSWORD="sua_senha_postgres"
    JWT_SECRET="sua_chave_secreta_super_segura_aqui"
    SUPER_FRETE_TOKEN="seu_token_da_super_frete"
    MERCADO_PAGO_TOKEN="seu_token_do_mercado_pago"
4. **Execute a aplicação**
A API estará disponível em [http://localhost:8080](http://localhost:8080)

## 🚀 Tecnologias
- **Java** & **Spring Boot**: Base da aplicação.
- **Spring Security (JWT)**: Para autenticação e autorização.
- **JPA/Hibernate**: Para persistência de dados.
- **PostgreSQL**: Banco de dados principal.
- **Swagger**: Documentação interativa da API.

## 🔗 Integrações
### Integração com SuperFrete
Este projeto integra a API da [SuperFrete](https://superfrete.com.br/) para realizar cotações de frete em tempo real (SEDEX, PAC, etc.), com base em CEP, dimensões do pacote e valor segurado.

### Integração com Mercado Pago
Utiliza a API oficial do [Mercado Pago](https://www.mercadopago.com.br/developers/pt) para processar pagamentos de forma segura.
- Criação de preferências de pagamento.
- Redirecionamento para o checkout do Mercado Pago.
- Recebimento de notificações de status via **webhook**.
- Suporte a pagamentos via cartão, Pix e boleto.

## 📚 Swagger (Documentação da API)
A API conta com documentação interativa do Swagger. Após iniciar o projeto localmente, acesse a URL abaixo no navegador para explorar e testar os endpoints:
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
> ⚠️ Certifique-se de que a aplicação esteja rodando e o perfil `dev` ativado.

## 🗃️ Banco de Dados
O projeto utiliza **PostgreSQL**, mas é compatível com outros bancos de dados relacionais.

### ⚙️ Configuração
A conexão é definida no `application.properties` e alimentada por variáveis de ambiente do arquivo `.env`.

### 🔄 Compatibilidade
Para usar outro banco (MySQL, MariaDB, etc.):
1. Adicione a dependência do driver JDBC desejado no `pom.xml`.
2. Altere a `DATASOURCE_URL` e outras variáveis de conexão no arquivo `.env`.

### 🧱 Estrutura do Banco
As tabelas são geradas e atualizadas automaticamente pelo Hibernate (`ddl-auto=update`).
> ⚠️ Para produção, recomenda-se usar `validate` e ferramentas de versionamento como **Flyway** ou **Liquibase**.

## 📌 Perfis de Usuário
- **USER**: Cliente padrão, pode gerenciar sua conta, carrinho, pedidos e favoritos.
- **MANAGER**: Pode gerenciar produtos e pedidos de todos os usuários.
- **ADMIN**: Acesso total para gerenciar usuários, produtos, pedidos e cupons.

## 🔒 Autenticação
A API usa **JWT (JSON Web Token)**. Endpoints protegidos exigem um **token Bearer** no cabeçalho `Authorization`.

## 📌 Endpoints
### 🧑 Usuários
| Método | Endpoint                  | Descrição                                 | Acesso                 | Status    |
|--------|---------------------------|-------------------------------------------|------------------------|-----------|
| POST   | /auth/registro            | Cria uma nova conta de usuário.           | Público                | ✅ Pronto |
| POST   | /auth/login               | Autentica um usuário e retorna um token.  | Público                | ✅ Pronto |
| GET    | /usuarios/info            | Obtém informações do usuário logado.      | USER, MANAGER, ADMIN   | ✅ Pronto |
| PUT    | /usuarios/info            | Atualiza informações do usuário logado.   | USER, MANAGER, ADMIN   | ✅ Pronto |
| DELETE | /usuarios/info            | Deleta o usuário logado.                  | USER, MANAGER, ADMIN   | ✅ Pronto |
| GET    | /usuarios/{id}            | Obtém informações de um usuário.          | MANAGER, ADMIN         | ✅ Pronto |
| PUT    | /usuarios/{id}            | Atualiza dados de um usuário.             | MANAGER, ADMIN         | ✅ Pronto |
| DELETE | /usuarios/{id}            | Deleta qualquer conta.                    | ADMIN                  | ✅ Pronto |
| POST   | /auth/registro/adm        | Cria conta com permissões de ADMIN.       | ADMIN                  | ✅ Pronto |
| GET    | /usuarios                 | Lista todos os usuários.                  | ADMIN                  | ✅ Pronto |

### 🏠 Endereços
| Método | Endpoint                  | Descrição                                 | Acesso                 | Status    |
|--------|---------------------------|-------------------------------------------|------------------------|-----------|
| POST   | /enderecos/info           | Cria endereço para o usuário logado.      | USER, MANAGER, ADMIN   | ✅ Pronto |
| GET    | /enderecos/info           | Lista endereços do usuário logado.        | USER, MANAGER, ADMIN   | ✅ Pronto |
| GET    | /enderecos/info/{id}      | Obtém endereço do usuário logado.         | USER, MANAGER, ADMIN   | ✅ Pronto |
| PUT    | /enderecos/info/{id}      | Atualiza endereço do usuário logado.      | USER, MANAGER, ADMIN   | ✅ Pronto |
| DELETE | /enderecos/info/{id}      | Remove endereço do usuário logado.        | USER, MANAGER, ADMIN   | ✅ Pronto |
| GET    | /enderecos/usuario/{id}   | Lista endereços de um usuário.            | MANAGER, ADMIN         | ✅ Pronto |

### 🛂 Carrinho de Compras
| Método | Endpoint                   | Descrição                                 | Acesso                 | Status    |
|--------|----------------------------|-------------------------------------------|------------------------|-----------|
| POST   | /carrinho/adicionar        | Adiciona um produto ao carrinho.          | USER, MANAGER, ADMIN   | ✅ Pronto |
| GET    | /carrinho/info             | Obtém os itens do carrinho.               | USER, MANAGER, ADMIN   | ✅ Pronto |
| PUT    | /carrinho/atualizar/{itemId} | Atualiza a quantidade de um item.       | USER, MANAGER, ADMIN   | ✅ Pronto |
| DELETE | /carrinho/remover/{itemId} | Remove um item do carrinho.             | USER, MANAGER, ADMIN   | ✅ Pronto |
| DELETE | /carrinho/limpar           | Esvazia o carrinho do usuário.            | USER, MANAGER, ADMIN   | ✅ Pronto |

### 📦 Produtos
| Método | Endpoint                      | Descrição                                 | Acesso         | Status    |
|--------|-------------------------------|-------------------------------------------|----------------|-----------|
| GET    | /produtos                     | Lista todos os produtos.                  | Público        | ✅ Pronto |
| GET    | /produtos/categoria/{categoria} | Lista produtos por categoria.             | Público        | ✅ Pronto |
| GET    | /produtos/search              | Busca produtos por nome.                  | Público        | ✅ Pronto |
| GET    | /produtos/{id}                | Obtém um produto específico.              | Público        | ✅ Pronto |
| POST   | /produtos                     | Cria um novo produto.                     | MANAGER, ADMIN | ✅ Pronto |
| PUT    | /produtos                     | Atualiza um produto existente.            | MANAGER, ADMIN | ✅ Pronto |
| DELETE | /produtos/{id}                | Remove um produto.                        | ADMIN          | ✅ Pronto |

### ⭐ Favoritos
| Método | Endpoint               | Descrição                                 | Acesso                 | Status    |
|--------|------------------------|-------------------------------------------|------------------------|-----------|
| POST   | /favoritos             | Adiciona um produto aos favoritos.        | USER, MANAGER, ADMIN   | ✅ Pronto |
| GET    | /favoritos/info        | Lista os favoritos do usuário.            | USER, MANAGER, ADMIN   | ✅ Pronto |
| DELETE | /favoritos/{id}        | Remove um produto dos favoritos.          | USER, MANAGER, ADMIN   | ✅ Pronto |

### 🛒 Pedidos
| Método | Endpoint                     | Descrição                                 | Acesso                 | Status    |
|--------|------------------------------|-------------------------------------------|------------------------|-----------|
| POST   | /pedidos/finalizar           | Cria um novo pedido a partir do carrinho. | USER, MANAGER, ADMIN   | ✅ Pronto |
| GET    | /pedidos/info                | Lista os pedidos do usuário logado.       | USER, MANAGER, ADMIN   | ✅ Pronto |
| GET    | /pedidos/{id}                | Obtém detalhes de um pedido.              | USER, MANAGER, ADMIN   | ✅ Pronto |
| GET    | /pedidos                     | Lista todos os pedidos do sistema.        | MANAGER, ADMIN         | ✅ Pronto |

### 💳 Pagamento
| Método | Endpoint               | Descrição                                 | Acesso                 | Status    |
|--------|------------------------|-------------------------------------------|------------------------|-----------|
| GET    | /pagamentos/{Id}       | Obtém o status do pagamento de um pedido. | USER, MANAGER, ADMIN   | ✅ Pronto |
| POST   | /pagamentos/webhook    | Recebe notificações do Mercado Pago.      | Público (Webhook)      | ✅ Pronto |

### 🚚 Envio
| Método | Endpoint               | Descrição                                 | Acesso                 | Status       |
|--------|------------------------|-------------------------------------------|------------------------|--------------|
| POST   | /envios/cotarfrete     | Cota o valor do frete para um CEP.        | Público                | ✅ Pronto    |
| GET    | /envios/{Id}           | Consulta o status do envio.               | USER, MANAGER, ADMIN   | ⏳ Planejado |
| PUT    | /envios/{Id}           | Atualiza o status do envio.               | MANAGER, ADMIN         | ⏳ Planejado |

### 🎟️ Cupons de Desconto
| Método | Endpoint               | Descrição                                 | Acesso                 | Status       |
|--------|------------------------|-------------------------------------------|------------------------|--------------|
| GET    | /cupons/{codigo}       | Verifica a validade de um cupom.          | USER, MANAGER, ADMIN   | ⏳ Planejado |
| GET    | /cupons                | Lista todos os cupons disponíveis.        | MANAGER, ADMIN         | ⏳ Planejado |
| POST   | /cupons                | Cria um novo cupom.                       | ADMIN                  | ⏳ Planejado |
| DELETE | /cupons/{id}           | Exclui um cupom.                          | ADMIN                  | ⏳ Planejado |

### 📝 Avaliações de Produtos
| Método | Endpoint               | Descrição                                 | Acesso                 | Status    |
|--------|------------------------|-------------------------------------------|------------------------|-----------|
| POST   | /avaliacoes            | Cria uma avaliação para um produto.       | USER, MANAGER, ADMIN   | ✅ Pronto |
| GET    | /avaliacoes/{produtoId} | Lista as avaliações de um produto.        | Público                | ✅ Pronto |

## 🤝 Como Contribuir
Contribuições são o que fazem a comunidade open source um lugar incrível para aprender, inspirar e criar. Qualquer contribuição que você fizer será **muito apreciada**.

1. Faça um Fork do projeto.
2. Crie sua Feature Branch (`git checkout -b feature/AmazingFeature`).
3. Faça o Commit de suas alterações (`git commit -m 'Add some AmazingFeature'`).
4. Faça o Push para a Branch (`git push origin feature/AmazingFeature`).
5. Abra um Pull Request.

## 📜 Licença
Distribuído sob a licença MIT. Veja `LICENSE.txt` para mais informações.