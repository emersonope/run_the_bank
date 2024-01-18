# API de Contas Bancárias

Esta API permite a criação de contas bancárias, adição de contas a usuários existentes e realização de pagamentos entre usuários.

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
  "accountInfoList": [
    {
      "accountName": "John Doe",
      "branchNumber": "9399",
      "accountNumber": "404959",
      "accountBalance": 0.00,
      "cpf": "123.456.789-09",
      "cnpj": null,
      "status": "ATIVA"
    }
  ]
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
    "accountInfoList": [
      {
        "accountName": "John Doe",
        "branchNumber": "9399",
        "accountNumber": "404959",
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
- **Parâmetros de Caminho (Exemplo):** `{sourceUserId}` - ID do usuário de origem, `{targetUserId}` - ID do usuário de destino.
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
  "accountInfoList": [
    {
      "accountName": "John Doe",
      "branchNumber": "1477",
      "accountNumber": "215482",
      "accountBalance": 900.00,
      "cpf": "123.456.789-09",
      "cnpj": null,
      "status": "ATIVA"
    },
    {
      "accountName": "John Doe",
      "branchNumber": "1477",
      "accountNumber": "808777",
      "accountBalance": 100.00,
      "cpf": "123.456.789-09",
      "cnpj": null,
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
  "accountInfoList": [
    {
      "accountName": "John Doe",
      "branchNumber": "7255",
      "accountNumber": "699697",
      "accountBalance": 1000.00,
      "cpf": "123.456.789-09",
      "cnpj": null,
      "status": "ATIVA"
    }
  ],
  "notificationSent": false
}
```

## 5. Consulta de Todas as Contas de Usuários

### 5.1 Obtenção de Todas as Contas

- **URL:** `GET /api/bankuser/allUsersAndAccounts`
- **Descrição:** Retorna informações de todas as contas de usuários.
- **Resposta (Exemplo):**
  ```json
  [
    {
      "responseCode": "SUCCESS",
      "responseMessage": "USER_FOUND_SUCCESSFULLY",
      "accountInfoList": [
        {
          "accountName": "John Doe",
          "branchNumber": "9399",
          "accountNumber": "404959",
          "accountBalance": 50.00,
          "cpf": "123.456.789-09",
          "cnpj": null,
          "status": "ATIVA"
        }
      ]
    },
    {
      "responseCode": "SUCCESS",
      "responseMessage": "USER_CREATED_SUCCESSFULLY",
      "accountInfoList": [
        {
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
		"accountName": "John Doe",
		"branchNumber": "5444",
		"accountNumber": "496376",
		"accountBalance": 350.00,
		"cpf": null,
		"cnpj": "63.698.103/0001-97",
		"status": "ATIVA"
    }
  ]