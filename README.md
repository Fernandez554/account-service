# Account-service - Bank Account Controller API

## Overview

This API allows users to manage bank accounts. The operations include retrieving all accounts, retrieving a specific account, creating, updating, and deleting bank accounts.

### Base URL
/accounts

### 1. **Retrieve All Bank Accounts**
#### `GET /accounts`
Fetches all bank accounts.

- **Summary**: Retrieves all bank accounts as a reactive Flux.
- **Response**:
  - **200 OK**: Successfully retrieved the list of bank accounts.
  - **204 No Content**: No content available (empty list).

#### Example Request:
GET /accounts

#### Example Response:
```json
[
  {
    "id": "123",
    "accountType": "savings",
    "customerId": "456",
    "balance": 1000.0,
    "maxMonthlyTrans": 5000.0,
    "maintenanceFee": 25.0,
    "allowedWithdrawalDay": "Monday",
    "withdrawAmountMax": 2000.0,
    "lstSigners": ["John Doe"],
    "lstHolders": ["Jane Doe"]
  },
  {
    "id": "124",
    "accountType": "checking",
    "customerId": "457",
    "balance": 1500.0,
    "maxMonthlyTrans": 10000.0,
    "maintenanceFee": 30.0,
    "allowedWithdrawalDay": "Tuesday",
    "withdrawAmountMax": 3000.0,
    "lstSigners": ["Mary Smith"],
    "lstHolders": ["James Smith"]
  },
]
```

### 2. **Retrieve a Specific Bank Account**
#### `GET /accounts/{account_id}`
Fetches a bank account by its unique ID.

- **Summary**: Retrieve a specific bank account by its ID.
- **Parameters**:
  - `account_id` (Path Variable): The ID of the bank account to retrieve.
- **Response**:
  - **200 OK**: Successfully retrieved the bank account.
  - **404 Not Found**: Bank account not found.

#### Example Request:
GET /accounts/123

#### Example Response:
```json
{
  "id": "123",
  "accountType": "savings",
  "customerId": "456",
  "balance": 1000.0,
  "maxMonthlyTrans": 5000.0,
  "maintenanceFee": 25.0,
  "allowedWithdrawalDay": "Monday",
  "withdrawAmountMax": 2000.0,
  "lstSigners": ["John Doe"],
  "lstHolders": ["Jane Doe"]
}
```

### 3. **Create a New Bank Account**
#### `POST /accounts`
Creates a new bank account and returns the newly created resource.

- **Summary**: Create a new bank account.
- **Request Body**:
  - The `BankAccount` object to create, including properties such as `accountType`, `customerId`, `balance`, etc.
- **Response**:
  - **201 Created**: Successfully created the bank account.
  - **404 Not Found**: Creation failed.

#### Example Request:
```json
{
  "accountType": "savings",
  "customerId": "458",
  "balance": 1000.0,
  "maxMonthlyTrans": 5000.0,
  "maintenanceFee": 20.0,
  "allowedWithdrawalDay": "Monday",
  "withdrawAmountMax": 2000.0,
  "lstSigners": ["Alice Cooper"],
  "lstHolders": ["Bob Cooper"]
}
```

### Example Response:
```json
{
  "id": "125",
  "accountType": "savings",
  "customerId": "458",
  "balance": 1000.0,
  "maxMonthlyTrans": 5000.0,
  "maintenanceFee": 20.0,
  "allowedWithdrawalDay": "Monday",
  "withdrawAmountMax": 2000.0,
  "lstSigners": ["Alice Cooper"],
  "lstHolders": ["Bob Cooper"]
}
```

### 4. **Update an Existing Bank Account**
#### `PUT /accounts/{account_id}`
Updates an existing bank account by its ID.

- **Summary**: Updates a bank account by its ID and returns the updated details.
- **Parameters**:
  - `account_id` (Path Variable): The ID of the bank account to update.
- **Request Body**:
  - The `BankAccount` object containing updated details, including `accountType`, `customerId`, `balance`, etc.
- **Response**:
  - **200 OK**: Successfully updated the bank account.
  - **404 Not Found**: Bank account not found.

#### Example Request:
PUT /accounts/{account_id}
```json
{
  "accountType": "checking",
  "customerId": "459",
  "balance": 2000.0,
  "maxMonthlyTrans": 8000.0,
  "maintenanceFee": 15.0,
  "allowedWithdrawalDay": "Tuesday",
  "withdrawAmountMax": 2500.0,
  "lstSigners": ["Chris Brown"],
  "lstHolders": ["Debbie Brown"]
}
```
### Example Response:
```json
{
  "id": "123",
  "accountType": "checking",
  "customerId": "459",
  "balance": 2000.0,
  "maxMonthlyTrans": 8000.0,
  "maintenanceFee": 15.0,
  "allowedWithdrawalDay": "Tuesday",
  "withdrawAmountMax": 2500.0,
  "lstSigners": ["Chris Brown"],
  "lstHolders": ["Debbie Brown"]
}
```
### 5. **Delete a Bank Account**
#### `DELETE /accounts/{account_id}`
Deletes a bank account by its ID.

- **Summary**: Deletes a bank account by its ID.
- **Parameters**:
  - `account_id` (Path Variable): The ID of the bank account to delete.
- **Response**:
  - **204 No Content**: Successfully deleted the bank account.
  - **404 Not Found**: Bank account not found.

#### Example Request:
DELETE /accounts/123
#### Example Response:
```json
{}





