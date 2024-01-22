# API de Contas Bancárias

Esta API permite a criação de contas bancárias, adição de contas a usuários existentes e realização de pagamentos entre usuários.

#### Segue o link da documentacao no Swagger 

``
http://localhost:8080/swagger-ui/index.html
``

### Para rodar a aplicacao sera necessario usar o docker ou ajustar as configuracoes no application.yaml para seu banco de preferencia

- Entre na pasta raiz e execute o seguinte comando para subir o banco de dados:

``
  docker-compose up
``

Em seguida rode a aplicacao normalmente na sua IDE de preferencia!

## Endpoints Disponíveis

### 1. Criação de Conta Bancária

- **URL:** `POST /api/bankuser`
- **Descrição:** Cria uma nova conta bancária associada a um usuário.
- **Corpo da Requisição:**
  ```json
  {
  "firstName": "John",
  "lastName": "Doe",
  "gender": "Male",
  "address": "123 Main St",
  "stateOfOriging": "NY",
  "email": "john.doe@example.com",
  "cpf": "123.456.789-09",
  "phoneNumber": "123-456-7890",
  "accountType": "PF"
  }

- **Resposta da Requisição:**

```json

{
  "responseCode": "SUCCESS",
  "responseMessage": "USER_CREATED_SUCCESSFULLY",
  "accountInfoDtoList": [
    {
      "account_id": "b9b11319-f359-4d4d-8d42-6ffc75bedc82",
      "user_id": "04687b26-0101-4b0a-8202-5a757162fa55",
      "accountName": "John Doe",
      "branchNumber": "6062",
      "accountNumber": "490372",
      "accountBalance": 0,
      "cpf": "123.456.789-09",
      "cnpj": null,
      "status": "ATIVA"
    }
  ],
  "userInfoDtoList": {
    "id": "04687b26-0101-4b0a-8202-5a757162fa55",
    "firstName": "John",
    "lastName": "Doe",
    "gender": "Male",
    "address": "123 Main St",
    "stateOfOriging": "NY",
    "email": "john.doe@example.com",
    "cpf": "123.456.789-09",
    "cnpj": null,
    "phoneNumber": "123-456-7890",
    "createdAt": "2024-01-22T14:15:31.669268"
  }
}
```

## 2. Operações de Conta

### 2.1 Adição de Conta a Usuário Existente

- **URL:** `POST /api/bankuser/{userId}/addAccount`
- **Descrição:** Adiciona uma nova conta a um usuário existente.
- **Parâmetros de Caminho (Exemplo):** `{userId}` - ID do usuário existente.
- **Resposta:**
  ```json
  {
	"responseCode": "SUCCESS",
	"responseMessage": "ACCOUNT_CREATED_SUCCESSFULLY",
	"accountInfoDtoList": [
		{
			"account_id": "5044e527-d69d-45b5-804e-7c503fd89f83",
			"user_id": "04687b26-0101-4b0a-8202-5a757162fa55",
			"accountName": "John Doe",
			"branchNumber": "6062",
			"accountNumber": "490372",
			"accountBalance": 0.00,
			"cpf": "123.456.789-09",
			"cnpj": null,
			"status": "ATIVA"
		}
]
}

## 3. Pagamento entre Usuários com Notificação

### 3.1 Realização de Pagamento com Notificação

- **URL:** `POST /api/bankuser/{userId}/makePayment`
- **Descrição:** Realiza um pagamento entre dois usuários com notificação.
- **Parâmetros de Caminho (Exemplo):** `{userId}` - ID do usuário de origem
- **Corpo da Requisição Para PF e se for PJ substitua o campo cpf por cnpj:**
  ```json
  {
  "amount": 100.00,
  "cpf": "123.456.789-09",
  "destinationBranch": "1477",
  "destinationAccountNumber": "808777",
  "accountType": "PF",
  "sourceBranch": "1477",
  "sourceAccountNumber": "215482",
  "sourceAccountType": "PF"
  }

- **Resposta:**

```json
{
  "responseCode": "SUCCESS",
  "responseMessage": "TRANSACTION_SUCCESSFULLY_COMPLETED",
  "accountInfoDtoList": [
    {
      "account_id": "577f0403-ed5f-4e5c-9996-7d568777ab08",
      "user_id": "b1f31cfd-f17a-447b-b712-e0352cebade4",
      "accountName": "John Doe",
      "branchNumber": "4758",
      "accountNumber": "964362",
      "accountBalance": 850.00,
      "cpf": "123.456.789-09",
      "cnpj": null,
      "status": "ATIVA"
    },
    {
      "account_id": "d61e7b0f-e5ed-4bdf-aca2-aee362519100",
      "user_id": "4da854a4-06b2-4f99-914f-7758599eafbd",
      "accountName": "John Doe",
      "branchNumber": "5093",
      "accountNumber": "158139",
      "accountBalance": 150.00,
      "cpf": null,
      "cnpj": "63.698.103/0001-97",
      "status": "ATIVA"
    }
  ],
  "notificationSent": true
}
```

### 4. Deposito bancario

- **URL:** `POST /api/bankuser/deposit`
- **Descrição:** Realiza um deposito em uma conta bancária associada a um usuário.
- **Corpo da Requisição:**
  ```json
  {
  "branchNumber": "7255",
  "accountNumber": "699697",
  "amount": 1000.00
  }

- **Resposta da Requisição:**

```json

{
  "account_id": "577f0403-ed5f-4e5c-9996-7d568777ab08",
  "user_id": "b1f31cfd-f17a-447b-b712-e0352cebade4",
  "accountName": "John Doe",
  "branchNumber": "4758",
  "accountNumber": "964362",
  "accountBalance": 1000.00,
  "status": "ATIVA"
}
```

## 5. Consulta de Todas as Contas de Usuários

### 5.1 Obtenção de Todas as Contas

- **URL:** `GET /api/bankuser/allUsersAccounts`
- **Descrição:** Retorna informações de todas as contas de usuários.
  - **Resposta (Exemplo):**
    ```json
    [
      {
        "responseCode": "SUCCESS",
        "responseMessage": "USER_FOUND_SUCCESSFULLY",
        "accountInfoList": [
          {
            "account_id": "577f0403-ed5f-4e5c-9996-7d568777ab08",
            "user_id": "b1f31cfd-f17a-447b-b712-e0352cebade4",
            "accountName": "John Doe",
            "branchNumber": "9399",
            "accountNumber": "404959",
            "accountBalance": 850.00,
            "cpf": "123.456.789-09",
            "cnpj": null,
            "status": "ATIVA"
          }
        ]
      },
      {
        "responseCode": "SUCCESS",
        "responseMessage": "USER_FOUND_SUCCESSFULLY",
        "accountInfoList": [
          {
            "account_id": "d61e7b0f-e5ed-4bdf-aca2-aee362519100",
            "user_id": "4da854a4-06b2-4f99-914f-7758599eafbd",
            "accountName": "Jane Doe",
            "branchNumber": "9250",
            "accountNumber": "395074",
            "accountBalance": 150.00,
            "cpf": null,
            "cnpj": "63.698.103/0001-97",
            "status": "ATIVA"
          }
        ]
      }
    ]

## 6. Consulta de por Id

### 6.1 Obtenção de Todas as Contas de um usuario por ID

- **URL:** `GET /api/bankuser/userAccounts/{clientId}`
- **Descrição:** Retorna informações de todas as contas de usuários.
  - **Resposta (Exemplo):**
    ```json
    [
      {
          "account_id": "577f0403-ed5f-4e5c-9996-7d568777ab08",
          "user_id": "b1f31cfd-f17a-447b-b712-e0352cebade4",
          "accountName": "John Doe",
          "branchNumber": "4758",
          "accountNumber": "964362",
          "accountBalance": 850.00,
          "cpf": "123.456.789-09",
          "cnpj": null,
          "status": "ATIVA"
      }
    ]
  
# Sugestao de melhoria:

### 1. Logging Aprimorado:

Melhorar o sistema de logging do projeto. Atualmente, o sistema está usando o Logger do SLF4J.

### 2. Validações e Tratamento de Erros:

Aprimorar as validações de entrada nos endpoints da API e fornecer respostas de erro mais descritivas. Mensagens claras de erro podem ser úteis para os consumidores da API.

### 3. Segurança:

Implementar medidas de segurança, como autenticação e autorização. Sugiro usar o Spring Security para proteger os endpoints.

### 4. Documentação da API:

Continuar usando o Swagger para documentar a API automaticamente. Isso é útil para os desenvolvedores que interagem com a API.

### 5. Testes:

Definir um percentual de cobertura adequada para os testes unitários e de integração.

### 6. Gerenciamento de Transações:

Verificar se as transações estão sendo gerenciadas corretamente, especialmente nos métodos do serviço que envolvem interações com o banco de dados.

### 7. Tratamento de Exceções Personalizadas:

Criar mais exceções personalizadas para tratar situações no domínio, pois isso pode ajudar a tornar o codigo mais legivel e facilitar o tratamento de erros.

### 8. Monitoramento e Métricas:

Integrar o aplicativo com ferramentas de monitoramento e geração de métricas para rastrear o desempenho e o comportamento em produção

### 9. Cache:

Avaliar a oportunidade de usar cache em algumas consultas ou operações que sao executadas com frequência

### 10. Refatoração e Otimização:

Continuar refatorando o código para garantir que seja limpo, modular e siga as melhores práticas, alem disso migrar para uma estrutura hexagonal seria interessante pois conseguimos ter uma melhor separacao das responsabilidades, testabilidade, adaptadores para Interfaces Externas e facilita a evolucao.